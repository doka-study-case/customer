package com.doka.customer.security;


import com.doka.customer.entity.CustomerEntity;
import com.doka.customer.exception.DokaException;
import com.doka.customer.repository.CustomerRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    @SneakyThrows
    public UserDetails loadUserByUsername(String username) {
        final Optional<CustomerEntity> customerEntityOptional = customerRepository.findById(Long.parseLong(username));
        if (customerEntityOptional.isEmpty()) {
            throw new DokaException(
                    String.format("User with given id is %s not found", username),
                    HttpStatus.UNAUTHORIZED
            );
        }

        CustomerEntity customerEntity = customerEntityOptional.get();

        return User
                .withUsername(String.valueOf(customerEntity.getId()))
                .password(customerEntity.getPassword())
                .build();
    }
}
