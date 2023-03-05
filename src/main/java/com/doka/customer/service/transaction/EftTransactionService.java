package com.doka.customer.service.transaction;

import com.doka.customer.dto.input.TransactionDto;
import com.doka.customer.entity.AccountEntity;
import com.doka.customer.enums.CustomerType;
import com.doka.customer.enums.TransactionType;
import com.doka.customer.enums.TransferType;
import com.doka.customer.exception.DokaException;
import com.doka.customer.queue.EventProducer;
import com.doka.customer.queue.QueueNotification;
import com.doka.customer.queue.QueueTransaction;
import com.doka.customer.service.AccountService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class EftTransactionService implements GenericTransactionService {

    private final AccountService accountService;
    private final FeeCalculationService feeCalculationService;
    private final EventProducer eventProducer;

    public EftTransactionService(AccountService accountService, FeeCalculationService feeCalculationService, EventProducer eventProducer) {
        this.accountService = accountService;
        this.feeCalculationService = feeCalculationService;
        this.eventProducer = eventProducer;
    }

    @Override
    public BigDecimal calculateFee(CustomerType customerType, BigDecimal amount) {
        float ratio = feeCalculationService.getRatio(TransactionType.EFT, customerType);
        return amount.multiply(BigDecimal.valueOf(ratio));
    }

    @Override
    @SneakyThrows
    public void makeTransfer(Long customerId, CustomerType customerType, TransactionDto transactionDto) {
        AccountEntity accountEntity = accountService.findById(transactionDto.getAccountId());

        AccountEntity targetAccountEntity = accountService.findByIban(transactionDto.getIban());
        if (accountEntity.getId() == targetAccountEntity.getId()) {
            throw new DokaException("Transfer failed. Target account and source account is same.", HttpStatus.NOT_ACCEPTABLE);
        }

        BigDecimal transactionFee = calculateFee(customerType, transactionDto.getAmount());
        BigDecimal totalCost = transactionFee.add(transactionDto.getAmount());
        if (accountEntity.getBalance().compareTo(totalCost) < 0) {
            throw new DokaException("Insufficient account balance for transaction", HttpStatus.NOT_ACCEPTABLE);
        }

        BigDecimal newBalance = accountEntity.getBalance().subtract(totalCost);
        accountEntity.setBalance(newBalance);
        accountService.updateAccount(accountEntity);

        BigDecimal targetNewBalance = targetAccountEntity.getBalance().add(transactionDto.getAmount());
        targetAccountEntity.setBalance(targetNewBalance);
        accountService.updateAccount(targetAccountEntity);
    }

    @Override
    @SneakyThrows
    public void makeTransfer(CustomerType customerType, AccountEntity sourceAccountEntity,
                             AccountEntity targetAccountEntity, TransactionDto transactionDto) {
        BigDecimal transactionFee = calculateFee(customerType, transactionDto.getAmount());
        BigDecimal totalCost = transactionFee.add(transactionDto.getAmount());
        if (sourceAccountEntity.getBalance().compareTo(totalCost) < 0) {
            throw new DokaException("Insufficient account balance for transaction", HttpStatus.NOT_ACCEPTABLE);
        }

        BigDecimal newBalance = sourceAccountEntity.getBalance().subtract(totalCost);
        sourceAccountEntity.setBalance(newBalance);
        accountService.updateAccount(sourceAccountEntity);

        BigDecimal targetNewBalance = targetAccountEntity.getBalance().add(transactionDto.getAmount());
        targetAccountEntity.setBalance(targetNewBalance);
        accountService.updateAccount(targetAccountEntity);

        // outgoing eft record for source customer
        QueueTransaction sourceQueueTransaction = QueueTransaction.builder()
                .customerId(sourceAccountEntity.getCustomerId())
                .amount(transactionDto.getAmount())
                .sourceAccountId(sourceAccountEntity.getId())
                .targetAccountId(targetAccountEntity.getId())
                .transferType(TransferType.OUTGOING_EFT)
                .build();
        eventProducer.sendTransaction(sourceQueueTransaction);

        // transaction fee record for source customer
        QueueTransaction feeQueueTransaction = QueueTransaction.builder()
                .customerId(sourceAccountEntity.getCustomerId())
                .amount(transactionFee)
                .sourceAccountId(sourceAccountEntity.getId())
                .targetAccountId(targetAccountEntity.getId())
                .transferType(TransferType.TRANSACTION_FEE)
                .build();
        eventProducer.sendTransaction(feeQueueTransaction);

        // incoming eft transaction for target customer
        QueueTransaction targetQueueTransaction = QueueTransaction.builder()
                .customerId(targetAccountEntity.getCustomerId())
                .amount(transactionDto.getAmount())
                .sourceAccountId(sourceAccountEntity.getId())
                .targetAccountId(targetAccountEntity.getId())
                .transferType(TransferType.INCOMING_EFT)
                .build();
        eventProducer.sendTransaction(targetQueueTransaction);

        // eft notification for target customer
        QueueNotification queueNotification = QueueNotification.builder()
                .customerId(targetAccountEntity.getCustomerId())
                .message("You have received an EFT with amount of " + transactionDto.getAmount())
                .build();
        eventProducer.sendNotification(queueNotification);
    }


}
