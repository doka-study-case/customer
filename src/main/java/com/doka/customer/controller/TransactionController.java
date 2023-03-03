package com.doka.customer.controller;

import com.doka.customer.dto.input.TransactionDto;
import com.doka.customer.dto.output.ApiResponseDto;
import com.doka.customer.queue.EventProducer;
import com.doka.customer.queue.QueueEvent;
import com.doka.customer.service.TransactionService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("transactions")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @Autowired
    EventProducer eventProducer;

    @PostMapping
    @SneakyThrows
    public ResponseEntity<ApiResponseDto> makeTransaction(
            @RequestAttribute("customerId") Long customerId,
            @Valid @RequestBody TransactionDto transactionDto) {
        transactionService.makeTransfer(customerId, transactionDto);

        ApiResponseDto apiResponseDto = new ApiResponseDto("Transaction successful");

        QueueEvent queueEvent = new QueueEvent()
                .setMessage(apiResponseDto.getMessage())
                .addParam("customerId", customerId)
                .addParam("transactionType", transactionDto.getType())
                .addParam("accountId", transactionDto.getAccountId())
                .addParam("amount", transactionDto.getAmount());

        if (transactionDto.getIban() != null) {
            queueEvent.addParam("iban", transactionDto.getIban());
        }

        if (transactionDto.getCorporation() != null) {
            queueEvent.addParam("corporation", transactionDto.getCorporation());
        }

        eventProducer.send(queueEvent);

        return ResponseEntity.ok(apiResponseDto);
    }

}
