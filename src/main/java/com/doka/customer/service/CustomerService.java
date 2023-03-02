package com.doka.customer.service;

import com.doka.customer.dto.input.CustomerRegisterDto;
import com.doka.customer.dto.query.CustomersQueryDto;
import com.doka.customer.entity.CustomerEntity;
import com.doka.customer.exception.DokaException;
import com.doka.customer.repository.CustomerRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ModelMapper modelMapper;

    public List<CustomerEntity> findAll(CustomersQueryDto customersQueryDto) {
        Page<CustomerEntity> pageCustomer;
        if (customersQueryDto.getType() != null) {
            pageCustomer = customerRepository
                    .findAllByType(customersQueryDto.getType(), customersQueryDto.toPageable());
        } else {
            pageCustomer = customerRepository.findAll(customersQueryDto.toPageable());
        }

        return pageCustomer.toList();
    }

    public CustomerEntity register(CustomerRegisterDto customerRegisterDto) {
        return customerRepository.save(modelMapper.map(customerRegisterDto, CustomerEntity.class));
    }

    @SneakyThrows
    public CustomerEntity findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new DokaException("Customer is not found", HttpStatus.NOT_FOUND));
    }

}
