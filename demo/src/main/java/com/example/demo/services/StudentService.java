package com.example.demo.services;

import com.example.data.Student;
import com.example.demo.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StudentService {
    @Autowired
    private StudentRepository repository;

    //Create Student
    public Mono<Student> createStudent(final Student student){
        return this.repository.save(student);
    }

    //Falta "Create Relationship"

    //Read all Students
    public Flux<Student> getAllStudents(){
        return this.repository.findAll();
    }

    //Read specific Student
    public Mono<Student> getStudentById(int studentId){
        return this.repository.findById((long)studentId);
    }

    //Update specific Student
    public Mono<Student> updateStudent(int studentId, final Mono<Student> studentMono){
        return this.repository.findById((long) studentId)
                .flatMap(s -> studentMono.map(u -> {
                    s.setName(u.getName());
                    s.setBirth_date(u.getBirth_date());
                    s.setCompleted_credits(u.getCompleted_credits());
                    s.setAverage_grade(u.getAverage_grade());
                    return s;
                }))
                .flatMap(s -> this.repository.save(s));
    }

    //Delete Student
    public Mono<Void> deleteStudent(final int id){
        return this.repository.deleteById((long)id);
    }

    // Falta "Delete relationship"

    // Falta "Read relationship" - .	This service	can	only	return	the	identifiers	of	some	student's
    //professors,	not	the	entire	professor data. I.e.,	students	should	not	create	a	service
    //that	 immediately	 provides,	 say,	 a	 student	 with	 all	 data	 of	 all	 the	 student’s
    //professors.
}
