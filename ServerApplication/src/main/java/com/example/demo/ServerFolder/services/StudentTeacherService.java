package com.example.demo.ServerFolder.services;

import com.example.data.Teacher_student;
import com.example.demo.ServerFolder.repositories.StudentRepository;
import com.example.demo.ServerFolder.repositories.StudentTeacherRepository;
import com.example.demo.ServerFolder.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class StudentTeacherService {
    @Autowired
    private StudentTeacherRepository relationshipRepository;


    // - Create	relationship.
    // Não sei se funciona, tem que se experimentar
    @PostMapping
    public Mono<Teacher_student> createRelationship(@RequestBody Teacher_student relationship) {
        Flux<Teacher_student> relationships = relationshipRepository.findAll();

        Flux<Teacher_student> studentsRelationship = relationships.filter(s -> (s.getStudent_id() == relationship.getStudent_id()));
        Flux<Teacher_student> teachersRelationship = relationships.filter(s -> (s.getTeacher_id() == relationship.getTeacher_id()));
        AtomicInteger aux = new AtomicInteger();
        studentsRelationship.doOnNext( student -> {
            teachersRelationship.doOnNext( teacher -> {
                if(student.getId() == teacher.getId()){
                    System.out.println("ERRO");
                    aux.set(1);
                }
            }).subscribe();
        }).subscribe();

        if(aux.get() == 1){
            System.out.println("HERE1");
            return null;
        }
        System.out.println("HERE2");
        return this.relationshipRepository.save(relationship);

    }

    @GetMapping(value = "/all")
    public Flux<Teacher_student> getAllRelationships(){
        return this.relationshipRepository.findAll();
    }



    // - Delete	relationship.
    // Não sei se funciona, tem que se experimentar
    @GetMapping(value = "/delete/{id}")
    public Mono<Void> deleteRelationship(@PathVariable int id) {
        System.out.println("ID: " + id);
        return this.relationshipRepository.deleteById((long) id);
    }


// - Read	relationship.	This service	can	only	return	the	identifiers	of	some	student's
//professors,	not	the	entire	professor data. I.e.,	students	should	not	create	a	service
//that	 immediately	 provides,	 say,	 a	 student	 with	 all	 data	 of	 all	 the	 student’s
//professors
}
