package com.doka.customer.service;

import com.doka.customer.dto.input.TransactionDto;
import com.doka.customer.entity.AccountEntity;
import com.doka.customer.entity.CustomerEntity;
import com.doka.customer.entity.TransactionEntity;
import com.doka.customer.exception.DokaException;
import com.doka.customer.service.transaction.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class TransactionService {

    @Autowired
    AccountService accountService;

    @Autowired
    CustomerService customerService;

    @Autowired
    FeeCalculationService feeCalculationService;

    @Autowired
    ModelMapper modelMapper;

    public TransactionEntity save(Long customerId, TransactionDto transactionDto) {
        TransactionEntity transactionEntity = modelMapper.map(transactionDto, TransactionEntity.class);
        transactionEntity.setCustomerId(customerId);


    }

    @Transactional
    @SneakyThrows
    public void makeTransfer(Long customerId, TransactionDto transactionDto) {
        CustomerEntity customerEntity = customerService.findById(customerId);

        AccountEntity accountEntity = accountService.findById(transactionDto.getAccountId());
        if (accountEntity.getCustomerId() != customerId)
            throw new DokaException("Account doesn't belong to current customer.", HttpStatus.UNAUTHORIZED);

        GenericTransactionService transaction = switch (transactionDto.getType()) {
            case EFT -> new EftTransactionService(accountService, feeCalculationService);
            case BILL_PAYMENT -> new BillPaymentTransactionService(accountService, feeCalculationService);
            case SALARY_PAYMENT -> new SalaryPaymentTransactionService(accountService, feeCalculationService);
        };

        transaction.makeTransfer(customerId, customerEntity.getType(), transactionDto);
    }


}
