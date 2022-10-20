package com.example.demo;

import com.example.data.Student;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import static java.lang.Math.*;

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

    /*
    @Bean
    CommandLineRunner demo(WebClient client) {
        return args -> {
            client.get().uri("/student/all")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(Student.class)
                    .subscribe(System.out::println);
        };
    }*/

    // 1. Names and birthdates of all students.
    @Bean
    CommandLineRunner GetNamesAndBirthdaysFromAllStudents(WebClient client) {
        System.out.println("===== Names And Birthdays From All Students =====");
        return args -> {
            client.get().uri("/student/all")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(Student.class)
                    .subscribe(cr -> System.out.println("Name: " + cr.getName() + " Birthday:" + cr.getBirth_date()));
        };
    }

    // 2. Total number of students.
    @Bean
    CommandLineRunner GetNumberOfStudents(WebClient client) {
        System.out.println("===== Number Of Students =====");
        return args -> {
            client.get().uri("/student/all")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(Student.class)
                    .count()
                    .subscribe(System.out::println);
        };
    }

    // 3. Total number of students that are active (i.e., that have less than 180 credits)
    @Bean
    CommandLineRunner GetNumberOfActiveStudents(WebClient client) {
        System.out.println("===== Number Of Active Students (credits < 180) =====");
        return args -> {
            client.get().uri("/student/all")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(Student.class)
                    .filter(v -> v.getCompleted_credits() < 180)
                    .count()
                    .subscribe(System.out::println);
        };
    }

    // 4. Total number of courses completed for all students, knowing that each course is
    //worth 6 credits.
    @Bean
    CommandLineRunner GetNumberOfCompletedCoursesFromEachStudent(WebClient client) {
        System.out.println("===== Number Of Completed Courses From Each Student =====");
        return args -> {
            client.get().uri("/student/all")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(Student.class)
                    .subscribe(cr -> System.out.println("Name: "+ cr.getName() + " || Completed Courses:" + cr.getCompleted_credits()/6));
        };
    }

    // 5. Data of students that are in the last year of their graduation (i.e., whose credits
    //are at least 120 and less than 180). This list should be sorted, such that students
    //closer to completion should come first. These data do not need to include the
    //professors.
    @Bean
    CommandLineRunner GetDataFromStudentsInTheLastYear(WebClient client) {
        System.out.println("===== Students that are in the last year =====");
        return args -> {
            client.get().uri("/student/all")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(Student.class)
                    .filter(s -> (s.getCompleted_credits() >=120 && s.getCompleted_credits() <=180))
                    .sort((a,b) -> b.getCompleted_credits().compareTo(a.getCompleted_credits()))
                    .subscribe(cr -> System.out.println("-> Name: "+ cr.getName() + " || Completed Courses:" + cr.getCompleted_credits()/6));
        };
    }

    // 6. Average and standard deviations of all student grades.
    @Bean
    CommandLineRunner GetAverageAndStandardDeviationFromGrades(WebClient client) {
        System.out.println("===== Average and Standard Deviation From Students Grades =====");
        Flux<Student> fluxStream =  client.get().uri("/student/all")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Student.class);

        Mono<Long> count = fluxStream.count();

        Mono<Float> sum = fluxStream.map(Student::getAverage_grade)
                                        .reduce(Float::sum);
        float mean = sum.block() / count.block();


        Mono<Double> sd_sum = fluxStream.map(a -> pow(abs(a.getAverage_grade() - mean), 2))
                                    .reduce(Double::sum);
        Double sd = sqrt(sd_sum.block() / count.block());


        return args -> {
            System.out.println("MEAN: "+ mean + "  Standard Deviation: " + sd);
        };
    }

    // 7. Average and standard deviations of students who have finished their graduation
    //(with 180 credits).
    @Bean
    CommandLineRunner GetAverageAndStandardDeviationForFinishedGraduation(WebClient client) {
        System.out.println("===== Average and Standard Deviation of Students Who Have Finished Their Graduation =====");
        Flux<Student> fluxStream =  client.get().uri("/student/all")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Student.class)
                .filter(s -> s.getCompleted_credits() >= 180);

        Mono<Long> count = fluxStream.count();

        Mono<Float> sum = fluxStream.map(Student::getAverage_grade)
                .reduce(Float::sum);
        float mean = sum.block() / count.block();


        Mono<Double> sd_sum = fluxStream.map(a -> pow(abs(a.getAverage_grade() - mean), 2))
                .reduce(Double::sum);

        Double sd = sqrt(sd_sum.block() / count.block());


        return args -> {
            System.out.println("-> MEAN: "+ mean + "  Standard Deviation: " + sd);
        };
    }
    // 8. The name of the eldest student.
    @Bean
    CommandLineRunner GetTheEldestStudent(WebClient client) {
        System.out.println("===== Name Of the Eldest Student =====");
        return args -> {
            client.get().uri("/student/all")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(Student.class)
                    .reduce((a,b) -> {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                                Date date_a = null;
                                Date date_b = null;
                                try {
                                    date_a = sf.parse(a.getBirth_date());
                                    System.out.println("1.Nome: "+ a.getName() + "data: "+ date_a);
                                    date_b = sf.parse(b.getBirth_date());
                                    System.out.println("2.Nome: "+ b.getName() + "data: "+date_b);

                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                        return date_a.compareTo(date_b) > 0 ? b : a;
                    }
                    )
                    .subscribe(s -> System.out.println("-...... Name:" + s.getName()));
        };
    }


}







