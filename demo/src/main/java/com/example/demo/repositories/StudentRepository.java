package com.example.demo.repositories;

import com.example.data.Student;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface StudentRepository extends ReactiveCrudRepository<Student,Long> {

}
