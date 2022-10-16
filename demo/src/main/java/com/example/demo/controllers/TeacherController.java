package com.example.demo.controllers;

import com.example.data.Teacher;
import com.example.demo.repositories.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/teacher/create")
@RequiredArgsConstructor
@Slf4j
public class TeacherController {

    @Autowired
    private TeacherRepository teacherRepository;

    //Create Teacher
    @PostMapping
    public Mono<Teacher> createTeacher(@RequestBody Teacher teacher){
        return this.teacherRepository.save(teacher);
    }
}
