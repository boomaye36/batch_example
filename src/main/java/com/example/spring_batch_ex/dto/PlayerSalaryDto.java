package com.example.spring_batch_ex.dto;

import lombok.Data;

@Data
public class PlayerSalaryDto {
    private String ID;
    private String lastName;
    private String firstName;
    private String position;
    private int birthYear;
    private int debutYear;
    private int salary;

    public static PlayerSalaryDto of(PlayerDto playerDto, int salary){
        PlayerSalaryDto playerSalary = new PlayerSalaryDto();
        playerSalary.setID(playerDto.getID());
        playerSalary.setLastName(playerDto.getLastName());
        playerSalary.setFirstName(playerDto.getFirstName());
        playerSalary.setPosition(playerDto.getPosition());
        playerSalary.setBirthYear(playerDto.getBirthYear());
        playerSalary.setDebutYear(playerDto.getDebutYear());
        playerSalary.setSalary(salary);
        return playerSalary;
    }
}
