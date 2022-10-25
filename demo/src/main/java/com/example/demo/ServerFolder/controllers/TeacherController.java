package com.example.demo.ServerFolder.controllers;

import com.example.data.Teacher;
import com.example.demo.ServerFolder.services.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/teacher")
@RequiredArgsConstructor
@Slf4j
public class TeacherController {
    @Autowired
    TeacherService teacherService;

    @PostMapping
    public Mono<Teacher> createTeacher(@RequestBody Teacher teacher) {
        return teacherService.createTeacher(teacher);
    }

    @GetMapping(value = "/all")
    public Flux<Teacher> getAllTeachers(){
        return teacherService.getAllTeachers();
    }

    @GetMapping(value = "/{id}")
    public Mono<Teacher> getTeacherById(@PathVariable int id){
        return teacherService.getTeacherById(id);
    }

    @PutMapping
    public Mono<Teacher> updateTeacher(@RequestBody Teacher teacher){
        return teacherService.updateTeacher(teacher);
    }

    // Não sei se funciona, tem que se experimentar
    @GetMapping(value = "/delete/{id}")
    public Mono<Void> deleteTeacher(@PathVariable int id) {
        return teacherService.deleteTeacher(id);
    }

}
