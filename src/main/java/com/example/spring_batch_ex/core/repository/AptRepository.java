package com.example.spring_batch_ex.core.repository;

import com.example.spring_batch_ex.core.entity.Apt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AptRepository extends JpaRepository<Apt, Long> {
    Optional<Apt> findAptByAptNameAndJibun(String aptName, String jibun);
}
