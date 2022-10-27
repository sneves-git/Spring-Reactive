DROP TABLE teacher_student;
DROP TABLE student;
DROP TABLE teacher;


CREATE TABLE student (
                         id         SERIAL,
                         name         VARCHAR(512) NOT NULL,
                         birth_date     VARCHAR(512) NOT NULL,
                         completed_credits INTEGER NOT NULL,
                         average_grade     FLOAT(8),
                         PRIMARY KEY(id)
);

CREATE TABLE teacher (
                         id     SERIAL,
                         name VARCHAR(512) NOT NULL,
                         PRIMARY KEY(id)
);

CREATE TABLE teacher_student (
                                 id SERIAL,
                                 student_id BIGINT,
                                 teacher_id BIGINT,
                                 PRIMARY KEY(student_id,teacher_id)
);

ALTER TABLE teacher_student ADD CONSTRAINT teacher_student_fk1 FOREIGN KEY (teacher_id) REFERENCES teacher(id);
ALTER TABLE teacher_student ADD CONSTRAINT teacher_student_fk2 FOREIGN KEY (student_id) REFERENCES student(id);