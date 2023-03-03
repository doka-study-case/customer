package com.doka.customer.service;

import com.doka.customer.dto.input.TransactionDto;
import com.doka.customer.entity.AccountEntity;
import com.doka.customer.entity.CustomerEntity;
import com.doka.customer.enums.TransactionType;
import com.doka.customer.exception.DokaException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    @Autowired
    AccountService accountService;

    @Autowired
    CustomerService customerService;

    @Transactional
    @SneakyThrows
    public void makeTransaction(Long customerId, TransactionDto transactionDto) {
        CustomerEntity customerEntity = customerService.findById(customerId);

        AccountEntity accountEntity = accountService.findById(transactionDto.getAccountId());
        if (accountEntity.getCustomerId() != customerId)
            throw new DokaException("Account doesn't belong to current customer.", HttpStatus.UNAUTHORIZED);

        GenericTransaction transaction = null;
        if (transactionDto.getType() == TransactionType.EFT) {
            transaction = new EftTransactionService(accountService);
        }

        if (transaction != null) {
            transaction.makeTransfer(customerId, customerEntity.getType(), transactionDto);
        }
    }


}
