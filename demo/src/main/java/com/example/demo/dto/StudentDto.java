package com.example.demo.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class StudentDto {
    private Integer id;
    private String name;
    private Date birth_date;
    private Integer completed_credits;
    private Float average_grade;
}
