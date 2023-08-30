package com.example.spring_batch_ex.core.service;

import com.example.spring_batch_ex.dto.PlayerDto;
import com.example.spring_batch_ex.dto.PlayerSalaryDto;
import org.springframework.stereotype.Service;

import java.time.Year;
@Service
public class PlayerSalaryService {

    public PlayerSalaryDto calSalary(PlayerDto playerDto){
        int salary = (Year.now().getValue() - playerDto.getBirthYear()) * 1000000;
        return PlayerSalaryDto.of(playerDto, salary);
    }
}
