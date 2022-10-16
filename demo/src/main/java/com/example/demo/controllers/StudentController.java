package com.example.demo.controllers;


import com.example.data.Student;
import com.example.demo.dto.StudentDto;
import com.example.demo.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Date;
import java.util.Calendar;

@RestController
@RequestMapping(value = "/student")
@RequiredArgsConstructor
@Slf4j
public class StudentController {
    private final StudentRepository studentRepository;

    @PostMapping
    public Mono<Student> createStudent(@RequestBody Student student) {
        System.out.println(student.getBirth_date());
        return this.studentRepository.save(student);
    }

    //Falta "Create Relationship"

    @GetMapping(value = "/all")
    public Flux<Student> getAllStudents(){
        return this.studentRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public Mono<Student> getStudentById(@PathVariable int id){
        return this.studentRepository.findById((long)id);
    }

    @PutMapping
    public Mono<Student> updateStudent(@RequestBody Student student){
        return studentRepository
                .findById((long) student.getId())
                .flatMap(studentResult -> studentRepository.save(student));
    }

    @DeleteMapping
    public Mono<Void> deleteStudent(@RequestBody Student student) {
        return studentRepository.deleteById((long)student.getId());
    }
}
