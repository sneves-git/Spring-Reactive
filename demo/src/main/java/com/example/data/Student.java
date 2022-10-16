package com.example.data;

import lombok.Builder;
import org.springframework.data.annotation.Id;

import java.sql.Date;

@Builder
public class Student {
    @Id
    private Integer id;

    private String name;
    private Date birth_date;
    private Integer completed_credits;
    private Float average_grade;

    public Student() {
    }

    public Student(Integer id, String name, Date birth_date, Integer completed_credits, Float average_grade) {
        this.id = id;
        this.name = name;
        this.birth_date = birth_date;
        this.completed_credits = completed_credits;
        this.average_grade = average_grade;
    }

    public Student(String name, Date birth_date, Integer completed_credits, Float average_grade) {
        this.name = name;
        this.birth_date = birth_date;
        this.completed_credits = completed_credits;
        this.average_grade = average_grade;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(Date birth_date) {
        this.birth_date = birth_date;
    }

    public Integer getCompleted_credits() {
        return completed_credits;
    }

    public void setCompleted_credits(Integer completed_credits) {
        this.completed_credits = completed_credits;
    }

    public Float getAverage_grade() {
        return average_grade;
    }

    public void setAverage_grade(Float average_grade) {
        this.average_grade = average_grade;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birth_date=" + birth_date +
                ", completed_credits=" + completed_credits +
                ", average_grade=" + average_grade +
                '}';
    }
}
