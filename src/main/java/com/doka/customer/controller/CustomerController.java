package com.doka.customer.controller;

import com.doka.customer.dto.input.CustomerRegisterDto;
import com.doka.customer.dto.query.PaginationQueryDto;
import com.doka.customer.entity.CustomerEntity;
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

    @GetMapping
    public ResponseEntity<List<CustomerEntity>> findAll(PaginationQueryDto paginationQueryDto) {
        List<CustomerEntity> customers = customerService.findAll(paginationQueryDto);
        if (customers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(customers);
    }

    @GetMapping("{customerId}")
    public ResponseEntity<CustomerEntity> find(@Valid @PathVariable("customerId") Long customerId) {
        CustomerEntity customer = customerService.findById(customerId);

        return ResponseEntity.ok(customer);
    }

    @PostMapping
    public ResponseEntity<CustomerEntity> register(@Valid @RequestBody CustomerRegisterDto customerRegisterDto) {
        CustomerEntity registerCustomer = customerService.register(customerRegisterDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(registerCustomer);
    }

}
