package com.doka.customer.service;

import com.doka.customer.dto.input.AccountCreateDto;
import com.doka.customer.entity.AccountEntity;
import com.doka.customer.exception.DokaException;
import com.doka.customer.repository.AccountRepository;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ModelMapper modelMapper;

    public List<AccountEntity> findAllByCustomerId(Long customerId) {
        return accountRepository.findAllByCustomerId(customerId);
    }

    @SneakyThrows
    public AccountEntity createAccount(Long customerId, AccountCreateDto accountCreateDto) {
        Optional<AccountEntity> existedIbanEntity = accountRepository.findByIban(accountCreateDto.getIban());
        if (existedIbanEntity.isPresent())
            throw new DokaException("Iban is already existed", HttpStatus.CONFLICT);

        AccountEntity entity = modelMapper.map(accountCreateDto, AccountEntity.class);
        entity.setCustomerId(customerId);
        return accountRepository.save(entity);
    }



}
