package com.example.demo.controllers;

import com.example.data.Teacher;
import com.example.demo.repositories.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/teacher")
@RequiredArgsConstructor
@Slf4j
public class TeacherController {
    private TeacherRepository teacherRepository;

    @PostMapping
    public Mono<Teacher> createTeacher(@RequestBody Teacher teacher) {
        return this.teacherRepository.save(teacher);
    }

    @GetMapping(value = "/all")
    public Flux<Teacher> getAllTeachers(){
        return this.teacherRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public Mono<Teacher> getTeacherById(@PathVariable int id){
        return this.teacherRepository.findById((long)id);
    }

    @PutMapping
    public Mono<Teacher> updateTeacher(@RequestBody Teacher teacher){
        return teacherRepository
                .findById((long) teacher.getId())
                .flatMap(studentResult -> teacherRepository.save(teacher));
    }

    // Não sei se funciona, tem que se experimentar
    @DeleteMapping(value = "/{id}")
    public Mono<Void> deleteTeacher(@PathVariable int id) {
        if(teacherRepository
                .findById((long) id) != null) {
            return teacherRepository.deleteById((long) id);
        }
        return null;
    }
}
