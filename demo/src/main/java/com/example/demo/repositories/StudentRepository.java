package com.example.demo.repositories;

import com.example.data.Student;
import com.example.demo.dto.StudentDto;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StudentRepository extends R2dbcRepository<Student,Long> {

}
