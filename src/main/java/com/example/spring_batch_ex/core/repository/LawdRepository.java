package com.example.spring_batch_ex.core.repository;

import com.example.spring_batch_ex.core.entity.Lawd;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LawdRepository extends JpaRepository<Lawd, Long> {
}
