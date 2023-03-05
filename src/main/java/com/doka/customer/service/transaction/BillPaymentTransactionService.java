package com.doka.customer.service.transaction;

import com.doka.customer.dto.input.TransactionDto;
import com.doka.customer.entity.AccountEntity;
import com.doka.customer.enums.CustomerType;
import com.doka.customer.enums.TransactionType;
import com.doka.customer.enums.TransferType;
import com.doka.customer.exception.DokaException;
import com.doka.customer.queue.EventProducer;
import com.doka.customer.queue.QueueTransaction;
import com.doka.customer.service.AccountService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class BillPaymentTransactionService implements GenericTransactionService {

    private final AccountService accountService;
    private final FeeCalculationService feeCalculationService;
    private final EventProducer eventProducer;

    public BillPaymentTransactionService(AccountService accountService, FeeCalculationService feeCalculationService, EventProducer eventProducer) {
        this.accountService = accountService;
        this.feeCalculationService = feeCalculationService;
        this.eventProducer = eventProducer;
    }

    @Override
    public BigDecimal calculateFee(CustomerType customerType, BigDecimal amount) {
        float ratio = feeCalculationService.getRatio(TransactionType.BILL_PAYMENT, customerType);
        return amount.multiply(BigDecimal.valueOf(ratio));
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

        // outgoing bill payment record for source customer
        QueueTransaction sourceQueueTransaction = QueueTransaction.builder()
                .customerId(sourceAccountEntity.getCustomerId())
                .amount(transactionDto.getAmount())
                .sourceAccountId(sourceAccountEntity.getId())
                .corporation(transactionDto.getCorporation())
                .transferType(TransferType.BILL_PAYMENT)
                .build();
        eventProducer.sendTransaction(sourceQueueTransaction);

        // transaction fee record for source customer
        if (transactionFee.compareTo(BigDecimal.valueOf(0)) > 0) {
            QueueTransaction feeQueueTransaction = QueueTransaction.builder()
                    .customerId(sourceAccountEntity.getCustomerId())
                    .amount(transactionFee)
                    .sourceAccountId(sourceAccountEntity.getId())
                    .corporation(transactionDto.getCorporation())
                    .transferType(TransferType.TRANSACTION_FEE)
                    .build();
            eventProducer.sendTransaction(feeQueueTransaction);
        }
    }
}
