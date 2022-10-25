package com.example.demo.ServerFolder.services;

import com.example.data.Student;
import com.example.demo.ServerFolder.controllers.StudentTeacherController;
import com.example.demo.ServerFolder.repositories.StudentRepository;
import com.example.demo.ServerFolder.repositories.StudentTeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    public Mono<Student> createStudent(Student student) {
        return this.studentRepository.save(student);
    }

    public Flux<Student> getAllStudents(){
        return this.studentRepository.findAll();
    }

    public Mono<Student> getStudentById(int id){
        return this.studentRepository.findById((long)id);
    }

    public Mono<Student> updateStudent(Student student){
        return this.studentRepository
                .findById((long) student.getId())
                .flatMap(studentResult -> studentRepository.save(student));
    }


    public Mono<Void> deleteStudent(int id){
        return this.studentRepository.deleteById((long) id);
    }
}
