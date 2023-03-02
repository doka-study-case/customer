package com.doka.customer.repository;

import com.doka.customer.entity.CustomerEntity;
import com.doka.customer.enums.CustomerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    Page<CustomerEntity> findAllByType(CustomerType customerType, Pageable pageable);

}
