package com.example.demo;

import com.example.data.Student;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static java.lang.Math.round;

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
                    .filter(v -> v.getCompleted_credits()<180)
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
    /*
    // 6. Average and standard deviations of all student grades.
    @Bean
    CommandLineRunner GetAverageAndStandardDeviationFromGrades(WebClient client) {
        System.out.println("===== Average and Standard Deviation From Students Grades =====");
        Integer mean = 0;
        return args -> {
            client.get().uri("/student/all")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(Student.class)
                    .reduce( (a,b) -> a.getAverage_grade()+ b.getAverage_grade())
                    .subscribe();
        };
    }*/


}







