package com.example.demo.controllers;

import com.example.data.Teacher_student;
import com.example.demo.repositories.StudentRepository;
import com.example.demo.repositories.StudentTeacherRepository;
import com.example.demo.repositories.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequestMapping(value = "/relationship")
@RequiredArgsConstructor
@Slf4j
public class StudentTeacherController {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentTeacherRepository repository;


// - Create	relationship.
    // Não sei se funciona, tem que se experimentar
    @PostMapping
    public Mono<Teacher_student> createRelationship(@RequestBody Teacher_student relationship) {

        //Verify if student exists
        if(studentRepository
                .findById((long) relationship.getStudent_id())
                .block() == null){
            return null;
        }
        // Verify if teacher exists
        if(teacherRepository
                .findById((long) relationship.getTeacher_id())
                .block() == null) {
            return null;
        }

        // Verify if relationship exists
        Flux<Teacher_student> relationshipFlux = repository.findAll();
        Mono<Long> count = relationshipFlux.count();
        Mono<Long> relationshipFluxVerified = relationshipFlux.filter(s -> ((!Objects.equals(s.getStudent_id(), relationship.getStudent_id()))
                                                                                &&(!Objects.equals(s.getTeacher_id(), relationship.getTeacher_id()))))
                                                                            .count();
        if(count != relationshipFluxVerified){
            return null;
        }

        // Save relationship
        return this.repository.save(relationship);

    }

    @GetMapping(value = "/all")
    public Flux<Teacher_student> getAllRelationships(){
        return this.repository.findAll();
    }



    // - Delete	relationship.
    // Não sei se funciona, tem que se experimentar
    @GetMapping(value = "/delete/{id}")
    public Mono<Void> deleteRelationship(@PathVariable int id) {
        System.out.println("ID: " + id);
        return this.repository.deleteById((long) id);
    }


// - Read	relationship.	This service	can	only	return	the	identifiers	of	some	student's
//professors,	not	the	entire	professor data. I.e.,	students	should	not	create	a	service
//that	 immediately	 provides,	 say,	 a	 student	 with	 all	 data	 of	 all	 the	 student’s
//professors
}
