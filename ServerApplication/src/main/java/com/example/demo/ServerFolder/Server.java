package com.example.demo.ServerFolder;

import com.example.data.Student;
import com.example.data.Teacher;
import com.example.data.Teacher_student;
import com.example.demo.ServerFolder.repositories.StudentRepository;
import com.example.demo.ServerFolder.repositories.StudentTeacherRepository;
import com.example.demo.ServerFolder.repositories.TeacherRepository;
import io.r2dbc.spi.ConnectionFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;

import static java.lang.Thread.sleep;

@SpringBootApplication
@EnableR2dbcAuditing
public class Server {

    public static Logger log = LogManager.getLogger(Server.class.getName());

    public static void main(String[] args)  {
        String log4jConfigFile = System.getProperty("user.dir")
                + File.separator + "log4j.properties";
        PropertyConfigurator.configure(log4jConfigFile);

        SpringApplication.run(Server.class, args);



    }
    @Bean
    ConnectionFactoryInitializer initializer(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        ResourceDatabasePopulator resource =
                new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
        initializer.setDatabasePopulator(resource);
        return initializer;
    }

    @Bean
    public CommandLineRunner createStudents(StudentRepository repository) {
        return args -> {
            repository.saveAll(Arrays.asList(new Student("Tatiana Almeida", "2001-05-05", 180, 20.0F),
                            new Student("Sofia Neves", "2001-05-28", 100, 13.0F),
                            new Student("Edgar Duarte", "2001-02-27", 170, 19.0F),
                            new Student("Alexy Almeida", "1996-11-2", 120, 16.0F),
                            new Student("Andre Carvalho", "2001-11-21", 200, 19.0F),
                            new Student("Paulo Cortesao", "2001-04-17", 160, 20.0F)))
                    .blockLast(Duration.ofSeconds(10));
        };
    }


    @Bean
    public CommandLineRunner createTeachers(TeacherRepository repository) {
        return args -> {
            repository.saveAll(Arrays.asList(new Teacher("Antonio Jesus"),
                            new Teacher("Paulo Costa"),

                            new Teacher("Catarina Sousa"),
                            new Teacher("Deolinda Marques")))
                    .blockLast(Duration.ofSeconds(10));
        };
    }

    @Bean
    public CommandLineRunner createRelationships(StudentTeacherRepository repository) {
        return args -> {
            repository.saveAll(Arrays.asList(new Teacher_student(1, 1),
                            new Teacher_student(1, 2),
                            new Teacher_student(1, 3),
                            new Teacher_student(2, 1),
                            new Teacher_student(2, 2),
                            new Teacher_student(3, 2),
                            new Teacher_student(4, 2),
                            new Teacher_student(5, 1),
                            new Teacher_student(6, 3)))
                    .blockLast(Duration.ofSeconds(10));
        };
    }

}
