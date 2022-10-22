package com.example.data;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Teacher_student {
    @Id
    private Integer id;

    private Integer student_id;
    private Integer teacher_id;

    public Teacher_student() {
    }

    public Teacher_student(Integer student_id, Integer teacher_id) {
        this.student_id = student_id;
        this.teacher_id = teacher_id;
    }

    public Teacher_student(Integer id, Integer student_id, Integer teacher_id) {
        this.id = id;
        this.student_id = student_id;
        this.teacher_id = teacher_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStudent_id() {
        return student_id;
    }

    public void setStudent_id(Integer student_id) {
        this.student_id = student_id;
    }

    public Integer getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(Integer teacher_id) {
        this.teacher_id = teacher_id;
    }

    @Override
    public String toString() {
        return "StudentTeacherRelationship{" +
                "id=" + id +
                ", student_id=" + student_id +
                ", teacher_id=" + teacher_id +
                '}';
    }

}
