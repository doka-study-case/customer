package com.doka.customer.controller;

import com.doka.customer.dto.input.AccountCreateDto;
import com.doka.customer.dto.input.CustomerRegisterDto;
import com.doka.customer.dto.query.CustomersQueryDto;
import com.doka.customer.entity.AccountEntity;
import com.doka.customer.entity.CustomerEntity;
import com.doka.customer.enums.CustomerType;
import com.doka.customer.queue.EventProducer;
import com.doka.customer.queue.QueueEvent;
import com.doka.customer.service.AccountService;
import com.doka.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("customers")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @Autowired
    AccountService accountService;

    @Autowired
    EventProducer eventProducer;

    @GetMapping
    public ResponseEntity<List<CustomerEntity>> findAll(CustomersQueryDto customersQueryDto) {
        List<CustomerEntity> customers = customerService.findAll(customersQueryDto);
        if (customers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(customers);
    }

    @PostMapping
    public ResponseEntity<CustomerEntity> register(@Valid @RequestBody CustomerRegisterDto customerRegisterDto) {
        CustomerEntity registeredCustomer = customerService.register(customerRegisterDto);

        QueueEvent queueEvent = new QueueEvent()
                .setMessage("Customer is registered")
                .addParam("name", registeredCustomer.getName())
                .addParam("customerId", registeredCustomer.getId())
                .addParam("type", registeredCustomer.getType());
        eventProducer.send(queueEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(registeredCustomer);
    }

    @GetMapping("{customerId}")
    public ResponseEntity<CustomerEntity> find(@Valid @PathVariable("customerId") Long customerId) {
        CustomerEntity customer = customerService.findById(customerId);

        return ResponseEntity.ok(customer);
    }

    @GetMapping("{customerId}/accounts")
    public ResponseEntity<List<AccountEntity>> findAllAccounts(@Valid @PathVariable("customerId") Long customerId) {
        List<AccountEntity> accounts = accountService.findAllByCustomerId(customerId);
        if (accounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(accounts);
    }

    @PostMapping("{customerId}/accounts")
    public ResponseEntity<AccountEntity> createAccount(
            @Valid @PathVariable("customerId") Long customerId,
            @Valid @RequestBody AccountCreateDto accountCreateDto) {
        AccountEntity createdAccount = accountService.createAccount(customerId, accountCreateDto);

        QueueEvent queueEvent = new QueueEvent()
                .setMessage("Customer account is registered")
                .addParam("accountId", createdAccount.getId())
                .addParam("iban", createdAccount.getIban());
        eventProducer.send(queueEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

}
