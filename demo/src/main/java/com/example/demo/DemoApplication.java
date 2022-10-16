package com.example.demo;

import com.example.data.Student;
import com.example.data.Teacher;
import com.example.demo.repositories.StudentRepository;
import com.example.demo.services.StudentService;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.util.List;


@SpringBootApplication
public class DemoApplication {

	//private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	/*
	public static void createData(){
		Student student1 = new Student( "Tatiana", new Date(2001,05,05), 120, 20.0F);
		Student student2 = new Student("Sofia", new Date(2001,05,28), 150, 19.0F);
		Student student3 = new Student("Joao", new Date(1999,04,05), 190, 12.0F);
		Student student4 = new Student( "Manuel", new Date(2001,05,05), 120, 20.0F);
		Student student5 = new Student("Augusto", new Date(2001,05,28), 150, 19.0F);
		Student student6 = new Student("Andre", new Date(1999,04,05), 190, 12.0F);

		List<Student> list = List.of(student1, student2, student3, student4, student5, student6);
		Flux<Long> flux = Mono.fromFuture(list).flatMapMany(Flux::fromIterable);

		StudentRepository studentRepository;
		studentRepository.saveAll(flux);
		Teacher teacher1 = new Teacher("Filipe");
		Teacher teacher2 = new Teacher("Mario");
		Teacher teacher3 = new Teacher("Clara");
		Teacher teacher4 = new Teacher("Monica");





	}*/
}