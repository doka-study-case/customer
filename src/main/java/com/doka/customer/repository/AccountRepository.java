package com.doka.customer.repository;

import com.doka.customer.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    List<AccountEntity> findAllByCustomerId(Long customerId);

    @Query("SELECT a FROM AccountEntity a WHERE iban = :iban")
    Optional<AccountEntity> findByIban(String iban);

}
