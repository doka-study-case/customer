package com.doka.customer.service;

import com.doka.customer.dto.input.CustomerRegisterDto;
import com.doka.customer.dto.query.PaginationQueryDto;
import com.doka.customer.entity.CustomerEntity;
import com.doka.customer.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ModelMapper modelMapper;

    public List<CustomerEntity> findAll(PaginationQueryDto paginationQueryDto) {
        return customerRepository.findAll(paginationQueryDto.toPageable()).toList();
    }

    public CustomerEntity register(CustomerRegisterDto customerRegisterDto) {
        return customerRepository.save(modelMapper.map(customerRegisterDto, CustomerEntity.class));
    }
}
