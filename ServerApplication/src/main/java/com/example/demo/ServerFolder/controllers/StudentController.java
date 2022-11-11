package com.example.demo.ServerFolder.controllers;


import com.example.data.Student;
import com.example.demo.ServerFolder.Server;
import com.example.demo.ServerFolder.services.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;

import static java.lang.Thread.sleep;


@RestController
@RequestMapping(value = "/student")
@RequiredArgsConstructor
public class StudentController {
    String log4jConfigFile = System.getProperty("user.dir")
            + File.separator + "log4j.properties";
    @Autowired
    StudentService studentService;


    @PostMapping
    public Mono<Student> createStudent(@RequestBody Student student) {
        Server.log.info(" [Student Controller] Creating a student");
        return studentService.createStudent(student);
    }

    @GetMapping(value = "/all")
    public Flux<Student> getAllStudents(){
        Server.log.info(" [Student Controller] Getting all students");
        return studentService.getAllStudents();
    }

    @GetMapping(value = "/{id}")
    public Mono<Student> getStudentById(@PathVariable int id){
        Server.log.info(" [Student Controller] Getting the student with the id " + id);
        return studentService.getStudentById(id);
    }

    @PutMapping
    public Mono<Student> updateStudent(@RequestBody Student student){
        Server.log.info(" [Student Controller] Updating the student with the id " + student.getId());
        return studentService.updateStudent(student);
    }
    @GetMapping(value = "/delete/{id}")
    public Mono<Void> deleteStudent(@PathVariable int id){
        Server.log.info(" [Student Controller] Deleting the student with the id " + id);
        return studentService.deleteStudent(id);
    }


}
