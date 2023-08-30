package com.example.spring_batch_ex.core.service;

import com.example.spring_batch_ex.dto.PlayerDto;
import com.example.spring_batch_ex.dto.PlayerSalaryDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerSalaryServiceTest {

    private PlayerSalaryService playerSalaryService;

    @BeforeEach
    public void setUP(){
        playerSalaryService = new PlayerSalaryService();
    }
    @Test
    void calSalary() {
        //given
        MockedStatic<Year> mockYearClass = Mockito.mockStatic(Year.class);
        Year mockYear = mock(Year.class);
        when(mockYear.getValue()).thenReturn(2023);
        mockYearClass.when(Year::now).thenReturn(mockYear);
        PlayerDto mockPlayer = mock(PlayerDto.class);
        when(mockPlayer.getBirthYear()).thenReturn(1980);

        //when
        PlayerSalaryDto result = playerSalaryService.calSalary(mockPlayer);
        //then
        Assertions.assertEquals(result.getSalary(), 43000000);
    }
}