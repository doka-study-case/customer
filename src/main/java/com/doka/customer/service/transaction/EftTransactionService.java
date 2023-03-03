package com.doka.customer.service.transaction;

import com.doka.customer.dto.input.TransactionDto;
import com.doka.customer.entity.AccountEntity;
import com.doka.customer.enums.CustomerType;
import com.doka.customer.enums.TransactionType;
import com.doka.customer.exception.DokaException;
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

    public EftTransactionService(AccountService accountService, FeeCalculationService feeCalculationService) {
        this.accountService = accountService;
        this.feeCalculationService = feeCalculationService;
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


}
