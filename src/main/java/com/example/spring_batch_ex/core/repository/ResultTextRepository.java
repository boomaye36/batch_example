package com.example.spring_batch_ex.core.repository;

import com.example.spring_batch_ex.core.domain.ResultText;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultTextRepository extends JpaRepository<ResultText, Integer> {
}
