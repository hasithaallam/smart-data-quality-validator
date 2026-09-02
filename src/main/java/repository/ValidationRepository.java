package com.dataquality.validator.repository;

import com.dataquality.validator.entity.Validation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidationRepository
        extends JpaRepository<Validation, Long> {

    long countByValidTrue();

    long countByValidFalse();
}