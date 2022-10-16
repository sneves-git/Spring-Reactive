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

@RestController
@RequestMapping(value = "/student/create")
@RequiredArgsConstructor
@Slf4j
public class StudentController {
    @Autowired
    private final StudentRepository studentRepository;
    /*
    @GetMapping
    public Flux<Student> getAll() {
        return studentRepository.findAll();
    }
*/
    //Create Student
    @PostMapping
    public Mono<Student> createStudent(@RequestBody Student student) {
        return this.studentRepository.save(student);
    }/*
    public Mono<Student> createStudent(final Student student){
        return this.studentRepository.save(student);
    }*/

    //Falta "Create Relationship"

    //Read all Students
    public Flux<Student> getAllStudents(){
        return this.studentRepository.findAll();
    }

    //Read specific Student
    public Mono<Student> getStudentById(int studentId){
        return this.studentRepository.findById((long)studentId);
    }

    //Update specific Student
    public Mono<Student> updateStudent(int studentId, final Mono<Student> studentMono){
        return this.studentRepository.findById((long) studentId)
                .flatMap(s -> studentMono.map(u -> {
                    s.setName(u.getName());
                    s.setBirth_date(u.getBirth_date());
                    s.setCompleted_credits(u.getCompleted_credits());
                    s.setAverage_grade(u.getAverage_grade());
                    return s;
                }))
                .flatMap(s -> this.studentRepository.save(s));
    }

    //Delete Student
    public Mono<Void> deleteStudent(final int id){
        return this.studentRepository.deleteById((long)id);
    }


}
