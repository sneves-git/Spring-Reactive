package com.example.demo.ServerFolder.controllers;

import com.example.data.Student;
import com.example.data.Teacher_student;
import com.example.demo.ServerFolder.Server;
import com.example.demo.ServerFolder.services.StudentTeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/relationship")
@RequiredArgsConstructor
@Slf4j
public class StudentTeacherController {
    @Autowired
    StudentTeacherService relationshipService;

    @PostMapping
    public Mono<Teacher_student> createRelationship(@RequestBody Teacher_student relationship) {
        return relationshipService.createRelationship(relationship);
    }

    @GetMapping(value = "/all")
    public Flux<Teacher_student> getAllRelationships(){
        Server.log.info(" [Relationship Controller] Getting all relationships");
        return relationshipService.getAllRelationships();
    }

    @GetMapping(value = "/delete/{id}")
    public Mono<Void> deleteRelationship(@PathVariable int id) {
        Server.log.info(" [Relationship Controller] Getting relationship with id " + id);
        return relationshipService.deleteRelationship(id);
    }


    @GetMapping(value = "/student/{id}")
    public Flux<Teacher_student> getStudentRelationships(@PathVariable int id){
        Server.log.info(" [Relationship Controller] Getting relationships from student with the id " + id);
        return relationshipService.getStudentRelationships(id);
    }

    @GetMapping(value = "/teacher/{id}")
    public Flux<Teacher_student> getTeacherRelationships(@PathVariable int id){
        Server.log.info(" [Relationship Controller] Getting relationships from teacher with the id " + id);
        return relationshipService.getTeacherRelationships(id);
    }

}
