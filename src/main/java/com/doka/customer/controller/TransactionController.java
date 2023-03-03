package com.doka.customer.controller;

import com.doka.customer.dto.input.TransactionDto;
import com.doka.customer.dto.output.ApiResponseDto;
import com.doka.customer.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("transactions")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping
    ResponseEntity<ApiResponseDto> makeTransaction(
            @RequestAttribute("customerId") Long customerId,
            @Valid @RequestBody TransactionDto transactionDto) {
        transactionService.makeTransaction(customerId, transactionDto);

        ApiResponseDto apiResponseDto = new ApiResponseDto("current customer id: " + customerId);
        return ResponseEntity.ok(apiResponseDto);
    }

}
