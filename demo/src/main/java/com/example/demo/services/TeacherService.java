package com.example.demo.services;

import com.example.data.Teacher;
import com.example.demo.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository repository;

    //Create Teacher
    public Mono<Teacher> createTeacher(final Teacher teacher){
        return this.repository.save(teacher);
    }

    //Read all Teachers
    public Flux<Teacher> getAllTeachers(){
        return this.repository.findAll();
    }

    //Read specific Teacher
    public Mono<Teacher> getTeacherById(Integer TeacherId){
        return this.repository.findById(Long.valueOf(TeacherId));
    }

    //Update specific Teacher
    public Mono<Teacher> updateTeacher(int teacherId, final Mono<Teacher> teacherMono){
        return this.repository.findById((long) teacherId)
                .flatMap(t -> teacherMono.map(u -> {
                    t.setName(u.getName());
                    return t;
                }))
                .flatMap(t -> this.repository.save(t));
    }

    //Delete Teacher
    public Mono<Void> deleteTeacher(final int id){
        return this.repository.deleteById((long) id);
    }
}
