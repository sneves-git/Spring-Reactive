package com.example.demo.ServerFolder.controllers;


import com.example.data.Student;
import com.example.demo.ServerFolder.services.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.lang.Thread.sleep;


@RestController
@RequestMapping(value = "/student")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    @Autowired
    StudentService studentService;


    @PostMapping
    public Mono<Student> createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @GetMapping(value = "/all")
    public Flux<Student> getAllStudents(){
        return studentService.getAllStudents();
    }

    @GetMapping(value = "/{id}")
    public Mono<Student> getStudentById(@PathVariable int id){
        return studentService.getStudentById(id);
    }

    @PutMapping
    public Mono<Student> updateStudent(@RequestBody Student student){
       return studentService.updateStudent(student);
    }
    @GetMapping(value = "/delete/{id}")
    public Mono<Void> deleteStudent(@PathVariable int id){
        return studentService.deleteStudent(id);
    }


}
