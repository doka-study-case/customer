package com.doka.customer.service;

import com.doka.customer.dto.input.TransactionDto;
import com.doka.customer.entity.AccountEntity;
import com.doka.customer.entity.CustomerEntity;
import com.doka.customer.enums.CustomerType;
import com.doka.customer.exception.DokaException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EftTransactionService implements GenericTransaction {

    @Value("{doka.fee.individual.eft}")
    String individualFeeStr;

    @Value("{doka.fee.business.eft}")
    String businessFeeStr;

    AccountService accountService;

    public EftTransactionService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public float getFeeRatio(CustomerType customerType) {
        return Float.parseFloat(customerType == CustomerType.INDIVIDUAL ? individualFeeStr : businessFeeStr);
    }

    @Override
    public BigDecimal calculateFee(CustomerType customerType, BigDecimal amount) {
        float ratio = getFeeRatio(customerType);
        return amount.multiply(BigDecimal.valueOf(ratio));
    }

    @Override
    @SneakyThrows
    public void makeTransfer(Long customerId, CustomerType customerType, TransactionDto transactionDto) {
        AccountEntity accountEntity = accountService.findById(transactionDto.getAccountId());

        AccountEntity targetAccountEntity = null;
        if (transactionDto.getIban() != null) {
            targetAccountEntity = accountService.findByIban(transactionDto.getIban());
        }

        BigDecimal transactionFee = calculateFee(customerType, transactionDto.getAmount());
        BigDecimal totalCost = transactionFee.add(transactionDto.getAmount());
        if (accountEntity.getBalance().compareTo(totalCost) >= 0) {
            throw new DokaException("Insufficient account balance for transaction", HttpStatus.NOT_ACCEPTABLE);
        }

        BigDecimal newBalance = accountEntity.getBalance().subtract(totalCost);
        accountEntity.setBalance(newBalance);
        accountService.updateAccount(accountEntity);

        if (targetAccountEntity != null) {
            BigDecimal targetNewBalance = targetAccountEntity.getBalance().add(transactionDto.getAmount());
            targetAccountEntity.setBalance(targetNewBalance);
            accountService.updateAccount(targetAccountEntity);
        }
    }


}
