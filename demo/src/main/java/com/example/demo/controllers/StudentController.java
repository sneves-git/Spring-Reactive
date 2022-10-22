package com.example.demo.controllers;


import com.example.data.Student;
import com.example.data.Teacher_student;
import com.example.demo.repositories.StudentRepository;
import com.example.demo.repositories.StudentTeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.lang.Thread.sleep;


@RestController
@RequestMapping(value = "/student")
@RequiredArgsConstructor
@Slf4j
public class StudentController {
    private final StudentRepository studentRepository;
    private final StudentTeacherRepository relationshipRepository;
    private final StudentTeacherController StudentTeacherController;

    @PostMapping
    public Mono<Student> createStudent(@RequestBody Student student) {
        return this.studentRepository.save(student);
    }

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
        return this.studentRepository
                .findById((long) student.getId())
                .flatMap(studentResult -> studentRepository.save(student));
    }

    // Não sei se funciona, ainda n experimentei
    @GetMapping(value = "/delete/{id}")
    public Mono<Void> deleteStudent(@PathVariable int id){

        return this.studentRepository.deleteById((long) id);
    }


}
