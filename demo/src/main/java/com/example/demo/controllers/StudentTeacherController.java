package com.example.demo.controllers;

import com.example.data.Student;
import com.example.data.StudentTeacherRelationship;
import com.example.demo.repositories.StudentRepository;
import com.example.demo.repositories.StudentTeacherRepository;
import com.example.demo.repositories.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StudentTeacherController {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentTeacherRepository repository;

// - Create	relationship.
    // Não sei se funciona, tem que se experimentar
    @PostMapping
    public Mono<StudentTeacherRelationship> createRelationship(@RequestBody StudentTeacherRelationship relationship) {
        if(studentRepository
                .findById((long) relationship.getStudent_id())
                .block() == null){
            return null;
        }
        if(teacherRepository
                .findById((long) relationship.getTeacher_id())
                .block() == null) {
            return null;
        }
        return this.repository.save(relationship);
    }

    // - Delete	relationship.
    // Não sei se funciona, tem que se experimentar
    @DeleteMapping(value = "/{id}")
    public Mono<Void> deleteRelationship(@PathVariable int id) {
        if(repository
                .findById((long) id) != null) {
            return repository.deleteById((long) id);
        }
        return null;
    }
// - Read	relationship.	This service	can	only	return	the	identifiers	of	some	student's
//professors,	not	the	entire	professor data. I.e.,	students	should	not	create	a	service
//that	 immediately	 provides,	 say,	 a	 student	 with	 all	 data	 of	 all	 the	 student’s
//professors
}
