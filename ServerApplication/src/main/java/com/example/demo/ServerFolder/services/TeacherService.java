package com.example.demo.ServerFolder.services;

import com.example.data.Teacher;
import com.example.data.Teacher_student;
import com.example.demo.ServerFolder.repositories.StudentTeacherRepository;
import com.example.demo.ServerFolder.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentTeacherRepository relationshipRepository;


    public Mono<Teacher> createTeacher(Teacher teacher) {
        return this.teacherRepository.save(teacher);
    }

    public Flux<Teacher> getAllTeachers(){
        return this.teacherRepository.findAll();
    }

    public Mono<Teacher> getTeacherById(int id){
        return this.teacherRepository.findById((long)id);
    }

    public Mono<Teacher> updateTeacher(Teacher teacher){
        return teacherRepository
                .findById((long) teacher.getId())
                .flatMap(teacherResult -> teacherRepository.save(teacher));
    }

    // Não sei se funciona, tem que se experimentar
    public Mono<Void> deleteTeacher(int id) {
        Flux<Teacher_student> relations = relationshipRepository.findAll().filter(s -> s.getTeacher_id().equals(id));
        relations.map(s -> relationshipRepository.deleteById((long) s.getId())).publish();
        return this.teacherRepository.deleteById((long) id);
    }
}
