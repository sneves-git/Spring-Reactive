CREATE TABLE student (
     id         SERIAL,
     name         VARCHAR(512) NOT NULL,
     birth_Date     DATE NOT NULL,
     completed_Credits INTEGER NOT NULL,
     average_Grade     FLOAT(8),
     PRIMARY KEY(id)
);

CREATE TABLE teacher (
     id     SERIAL,
     name VARCHAR(512) NOT NULL,
     PRIMARY KEY(id)
);

CREATE TABLE teacher_student (
     id SERIAL,
     teacher_id BIGINT,
     student_id BIGINT,
     PRIMARY KEY(id)
);

ALTER TABLE teacher_student ADD CONSTRAINT teacher_student_fk1 FOREIGN KEY (teacher_id) REFERENCES teacher(id);
ALTER TABLE teacher_student ADD CONSTRAINT teacher_student_fk2 FOREIGN KEY (student_id) REFERENCES student(id);