package com.example.demo.Client;

import com.example.data.Student;
import com.example.data.Teacher;
import com.example.data.Teacher_student;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ClientApplication.class)
                .properties(Collections.singletonMap("server.port", "8081"))
                .run(args);
    }

    @Bean
    WebClient client() {
        return WebClient.create("http://localhost:8080");
    }

    // 1. Names and birthdates of all students.
    @Bean
    CommandLineRunner GetNamesAndBirthdaysFromAllStudents(WebClient client) {
        return args -> {
            System.out.println("\n===== Names And Birthdays From All Students =====");
            client.get().uri("/student/all")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(Student.class)
                    .doOnNext(cr -> System.out.println("-> Name: " + cr.getName() + " Birthday: " + cr.getBirth_date()))
                    .blockLast();
        };
    }

    // 2. Total number of students.
    @Bean
    CommandLineRunner GetNumberOfStudents(WebClient client) {
        return args -> {
            client.get().uri("/student/all")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(Student.class)
                    .count()
                    .doOnNext(s -> System.out.println("\n===== Number Of Students =====\n-> " + s))
                    .block();
        };
    }

    // 3. Total number of students that are active (i.e., that have less than 180 credits)
    @Bean
    CommandLineRunner GetNumberOfActiveStudents(WebClient client) {
        return args -> {
            System.out.println("\n===== Number Of Active Students (credits < 180) =====");
            client.get().uri("/student/all")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(Student.class)
                    .filter(v -> v.getCompleted_credits() < 180)
                    .count()
                    .doOnNext(s -> System.out.println("-> " + s))
                    .block();
        };
    }

    // 4. Total number of courses completed for all students, knowing that each course is
    //worth 6 credits.
    @Bean
    CommandLineRunner GetNumberOfCompletedCoursesFromEachStudent(WebClient client) {
        return args -> {
            System.out.println("\n===== Number Of Completed Courses From Each Student =====");
            client.get().uri("/student/all")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(Student.class)
                    .doOnNext(cr -> System.out.println("Name: " + cr.getName() + " || Completed Courses:" + cr.getCompleted_credits() / 6))
                    .blockLast();
        };
    }

    // 5. Data of students that are in the last year of their graduation (i.e., whose credits
    //are at least 120 and less than 180). This list should be sorted, such that students
    //closer to completion should come first. These data do not need to include the
    //professors.
    @Bean
    CommandLineRunner GetDataFromStudentsInTheLastYear(WebClient client) {
        return args -> {
            System.out.println("\n===== Students that are in the last year =====");
            client.get().uri("/student/all")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(Student.class)
                    .filter(s -> (s.getCompleted_credits() >= 120 && s.getCompleted_credits() < 180))
                    .sort((a, b) -> b.getCompleted_credits().compareTo(a.getCompleted_credits()))
                    .doOnNext(cr -> System.out.println("-> Name: " + cr.getName() + " || Completed Courses:" + cr.getCompleted_credits() / 6))
                    .blockLast();
        };
    }


    // 6. Average and standard deviations of all student grades.
    @Bean
    CommandLineRunner GetAverageAndStandardDeviationFromGrades(WebClient client) {
        Flux<Student> fluxStream = client.get().uri("/student/all")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Student.class);

        Mono<Long> count = fluxStream.count();

        Mono<Float> sum = fluxStream.map(Student::getAverage_grade)
                .reduce(Float::sum);
        float mean = sum.block() / count.block();


        Mono<Double> sd_sum = fluxStream.map(a -> pow(a.getAverage_grade() - mean, 2))
                .reduce(Double::sum);
        Double sd = sqrt(sd_sum.block() / count.block());


        return args -> {
            System.out.println("\n===== Average and Standard Deviation From Students Grades ===== \n-> Mean: " + mean + "  Standard Deviation: " + sd);
        };
    }


    // 7. Average and standard deviations of students who have finished their graduation
    //(with 180 credits).
    @Bean
    CommandLineRunner GetAverageAndStandardDeviationForFinishedGraduation(WebClient client) {
        Flux<Student> fluxStream = client.get().uri("/student/all")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Student.class)
                .filter(s -> s.getCompleted_credits() >= 180);

        Mono<Long> count = fluxStream.count();

        Mono<Float> sum = fluxStream.map(Student::getAverage_grade)
                .reduce(Float::sum);
        float mean = sum.block() / count.block();


        Mono<Double> sd_sum = fluxStream.map(a -> pow(a.getAverage_grade() - mean, 2))
                .reduce(Double::sum);

        Double sd = sqrt(sd_sum.block() / count.block());


        return args -> {
            System.out.println("\n===== Average and Standard Deviation of Students Who Have Finished Their Graduation ===== \n -> Mean: " + mean + "  Standard Deviation: " + sd);
        };
    }

    // 8. The name of the eldest student.
    @Bean
    CommandLineRunner GetTheEldestStudent(WebClient client) {
        return args -> {
            client.get().uri("/student/all")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(Student.class)
                    .reduce((a, b) -> {
                                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                                Date date_a = null;
                                Date date_b = null;
                                try {
                                    date_a = sf.parse(a.getBirth_date());
                                    date_b = sf.parse(b.getBirth_date());

                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                return date_a.compareTo(date_b) > 0 ? b : a;
                            }
                    )
                    .doOnNext(s -> System.out.println("\n===== Name Of the Eldest Student =====\n" + "-> " + s.getName()))
                    .block();
        };
    }

    //9. Average number of professors per student. Note that some professors may not
    //have students and vice-versa.
    @Bean
    CommandLineRunner RelationshipAverage(WebClient client) {
        Long studentsNumber = client.get().uri("/student/all")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Student.class)
                .count().block();


        Long relationshipsNumber = client.get().uri("/relationship/all")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Teacher_student.class)
                .count().block();

        float average = (float) relationshipsNumber / studentsNumber;
        return args -> {
            System.out.println("\n===== Average number of professors per student =====\n" + "-> " + average);
        };
    }


    //10.Name and number of students per professor, sorted in descending order.
    @Bean
    CommandLineRunner Relationships(WebClient client) {

        return args -> {

            Flux<Student> students = client.get().uri("/student/all")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(Student.class);

            Flux<Student> teachers = client.get().uri("/teacher/all")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(Student.class);

            Flux<Teacher_student> relationships = client.get().uri("/relationship/all")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(Teacher_student.class);
            System.out.println("\n===== Name and number of students per professor =====");
            teachers.publishOn(Schedulers.boundedElastic()).publishOn(Schedulers.boundedElastic()).doOnNext(teacher -> {
                        AtomicReference<Flux<Student>> studentsPerTeacher = new AtomicReference<>(Flux.just());
                        System.out.println("Teacher: " + teacher.getName());
                        relationships.publishOn(Schedulers.boundedElastic()).doOnNext(relationship -> {
                                    if (relationship.getTeacher_id() == teacher.getId()) {
                                        students.doOnNext(student -> {
                                                    if (student.getId() == relationship.getStudent_id()) {
                                                        //Flux.concat(studentsPerTeacher, Flux.just(student));
                                                        studentsPerTeacher.set(Flux.merge(studentsPerTeacher.get(), Flux.just(student)));
                                                        //System.out.println("->Student: "+ student.getName());
                                                    }

                                                }
                                        ).blockLast();
                                    }

                                }
                        ).blockLast();
                        studentsPerTeacher.get().count().subscribe(s -> System.out.println("  Number of students: " + s));
                        studentsPerTeacher.get().sort((a, b) -> b.getName().compareTo(a.getName())).doOnNext(s -> System.out.println("-> Student: " + s.getName())).blockLast();

                    }
            ).blockLast();
        };

    }


    // 11. Complete	data of all	students, by adding	the	names of their professors.
    @Bean
    CommandLineRunner CompleteDataOfStudents(WebClient client) {
        Flux<Teacher_student> relationships = client.get().uri("/relationship/all")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Teacher_student.class);

        Flux<Teacher> teachers = client.get().uri("/teacher/all")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Teacher.class);

        return args -> {
            System.out.println("\n===== Complete data of all students =====");
            client.get().uri("/student/all")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(Student.class)
                    .publishOn(Schedulers.boundedElastic())
                    .doOnNext(s -> {
                        System.out.println("\nName: " + s.getName() +
                                "\nBirthday: " + s.getBirth_date() +
                                "\nCompleted credits: " + s.getCompleted_credits() +
                                "\nAverage grade: " + s.getAverage_grade() +
                                "\nTeachers:");
                        relationships.publishOn(Schedulers.boundedElastic()).doOnNext(relationship -> {
                                    if (s.getId() == relationship.getStudent_id()) {
                                        teachers.doOnNext(t -> {
                                                    if (t.getId() == relationship.getTeacher_id()) {
                                                        System.out.println(" - " + t.getName());
                                                    }
                                                })
                                                .blockLast();
                                    }
                                })
                                .blockLast();
                    })
                    .blockLast();

        };
    }

}







