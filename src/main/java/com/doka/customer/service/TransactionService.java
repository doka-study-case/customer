package com.doka.customer.service;

import com.doka.customer.dto.input.TransactionDto;
import com.doka.customer.entity.AccountEntity;
import com.doka.customer.entity.CustomerEntity;
import com.doka.customer.exception.DokaException;
import com.doka.customer.queue.EventProducer;
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

    @Autowired
    EventProducer eventProducer;

    /**
     * transaction will be failed if makeTransfer throws an exception
     * @param customerId: account holder customer id
     * @param transactionDto: transaction details
     */
    @Transactional
    @SneakyThrows
    public void makeTransfer(Long customerId, TransactionDto transactionDto) {
        CustomerEntity customerEntity = customerService.findById(customerId);

        AccountEntity sourceAccountEntity = accountService.findById(transactionDto.getAccountId());
        if (sourceAccountEntity.getCustomerId() != customerId)
            throw new DokaException("Account doesn't belong to current customer.", HttpStatus.UNAUTHORIZED);

        AccountEntity targetAccountEntity = accountService.findByIban(transactionDto.getIban());
        if (sourceAccountEntity.getId() == targetAccountEntity.getId()) {
            throw new DokaException("Transfer failed. Target account and source account is same.", HttpStatus.NOT_ACCEPTABLE);
        }

        GenericTransactionService transaction = switch (transactionDto.getType()) {
            case EFT -> new EftTransactionService(accountService, feeCalculationService, eventProducer);
            case BILL_PAYMENT -> new BillPaymentTransactionService(accountService, feeCalculationService, eventProducer);
            case SALARY_PAYMENT -> new SalaryPaymentTransactionService(accountService, feeCalculationService, eventProducer);
        };

//        transaction.makeTransfer(customerId, customerEntity.getType(), transactionDto);
        transaction.makeTransfer(customerEntity.getType(), sourceAccountEntity, targetAccountEntity, transactionDto);
    }


}
