package com.example.demo.ServerFolder.repositories;

import com.example.data.Teacher_student;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentTeacherRepository extends ReactiveCrudRepository<Teacher_student,Long> {

}
