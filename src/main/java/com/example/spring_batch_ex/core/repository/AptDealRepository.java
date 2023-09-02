package com.example.spring_batch_ex.core.repository;

import com.example.spring_batch_ex.core.entity.Apt;
import com.example.spring_batch_ex.core.entity.AptDeal;
import com.example.spring_batch_ex.dto.AptDealDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AptDealRepository extends JpaRepository<AptDeal, Long> {
    Optional<AptDeal> findAptDealByAptAndExclusiveAreaAndDealDateAndDealAmountAndFloor(
            Apt apt, Double exclusiveArea, LocalDate dealDate, Long dealAmount, Integer floor
    );

    List<AptDeal> findByDealCanceledIsFalseAndDealDateEquals(LocalDate dealDate);

}
