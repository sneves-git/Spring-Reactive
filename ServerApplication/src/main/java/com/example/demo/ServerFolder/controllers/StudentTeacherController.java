package com.example.demo.ServerFolder.controllers;

import com.example.data.Teacher_student;
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

    // - Create	relationship.
    // Não sei se funciona, tem que se experimentar
    @PostMapping
    public Mono<Teacher_student> createRelationship(@RequestBody Teacher_student relationship) {
        return relationshipService.createRelationship(relationship);
    }

    @GetMapping(value = "/all")
    public Flux<Teacher_student> getAllRelationships(){
        return relationshipService.getAllRelationships();
    }

    // - Delete	relationship.
    // Não sei se funciona, tem que se experimentar
    @GetMapping(value = "/delete/{id}")
    public Mono<Void> deleteRelationship(@PathVariable int id) {
        return relationshipService.deleteRelationship(id);
    }




}
