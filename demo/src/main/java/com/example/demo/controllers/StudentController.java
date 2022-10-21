package com.example.demo.controllers;


import com.example.data.Student;
import com.example.demo.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(value = "/student")
@RequiredArgsConstructor
@Slf4j
public class StudentController {
    private final StudentRepository studentRepository;

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
        return studentRepository
                .findById((long) student.getId())
                .flatMap(studentResult -> studentRepository.save(student));
    }

    // Não sei se funciona, ainda n experimentei
    @DeleteMapping(value = "{id}")
    public Mono<Void> deleteStudent(@PathVariable int id) {
        if(studentRepository
                .findById((long) id) != null) {
            return studentRepository.deleteById((long) id);
        }
        return null;
    }
}
