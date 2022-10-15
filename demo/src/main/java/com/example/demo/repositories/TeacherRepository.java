package com.example.demo.repositories;

import com.example.data.Teacher;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends ReactiveCrudRepository<Teacher,Long> {

}