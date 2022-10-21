package com.example.demo.repositories;

import com.example.data.StudentTeacherRelationship;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentTeacherRepository extends ReactiveCrudRepository<StudentTeacherRelationship,Long> {

}
