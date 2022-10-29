package com.example.demo.ServerFolder.repositories;

import com.example.data.Teacher_student;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface StudentTeacherRepository extends ReactiveCrudRepository<Teacher_student,Long> {
    @Query("SELECT * FROM teacher_student WHERE student_id = $1")
    Flux<Teacher_student> findRelationshipsByStudentId(long id);
}
