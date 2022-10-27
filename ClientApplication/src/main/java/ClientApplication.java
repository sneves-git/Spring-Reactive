
import data.Student;
import data.Teacher;
import data.Teacher_student;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Thread.sleep;

public class ClientApplication {

    public static void main(String[] args)  {

      WebClient client = getWebClient();


    // 1. Names and birthdates of all students.
    System.out.println("\n===== Names And Birthdays From All Students =====");
    client.get().uri("localhost:8080/student/all")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(Student.class)
            .doOnNext(cr -> System.out.println("-> Name: " + cr.getName() + " Birthday: " + cr.getBirth_date()))
            .blockLast();



    // 2. Total number of students.
    client.get().uri("localhost:8080/student/all")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(Student.class)
            .count()
            .doOnNext(s -> System.out.println("\n===== Number Of Students =====\n-> " + s))
            .block();


    // 3. Total number of students that are active (i.e., that have less than 180 credits)
    System.out.println("\n===== Number Of Active Students (credits < 180) =====");
    client.get().uri("localhost:8080/student/all")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(Student.class)
            .filter(v -> v.getCompleted_credits() < 180)
            .count()
            .doOnNext(s -> System.out.println("-> " + s))
            .block();


    // 4. Total number of courses completed for all students, knowing that each course is
    //worth 6 credits.
    System.out.println("\n===== Number Of Completed Courses From Each Student =====");
    client.get().uri("localhost:8080/student/all")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(Student.class)
            .doOnNext(cr -> System.out.println("Name: " + cr.getName() + " || Completed Courses:" + cr.getCompleted_credits() / 6))
            .blockLast();


    // 5. Data of students that are in the last year of their graduation (i.e., whose credits
    //are at least 120 and less than 180). This list should be sorted, such that students
    //closer to completion should come first. These data do not need to include the
    //professors.
    System.out.println("\n===== Students that are in the last year =====");
    client.get().uri("localhost:8080/student/all")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(Student.class)
            .filter(s -> (s.getCompleted_credits() >= 120 && s.getCompleted_credits() < 180))
            .sort((a, b) -> b.getCompleted_credits().compareTo(a.getCompleted_credits()))
            .doOnNext(cr -> System.out.println("-> Name: " + cr.getName() + " || Completed Courses:" + cr.getCompleted_credits() / 6))
            .blockLast();



    // 6. Average and standard deviations of all student grades.
    Flux<Student> fluxStream = client.get().uri("localhost:8080/student/all")
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



    System.out.println("\n===== Average and Standard Deviation From Students Grades ===== \n-> Mean: " + mean + "  Standard Deviation: " + sd);



    // 7. Average and standard deviations of students who have finished their graduation
    //(with 180 credits).

    Flux<Student> fluxStreamm = client.get().uri("localhost:8080/student/all")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(Student.class)
            .filter(s -> s.getCompleted_credits() >= 180);

    Mono<Long> count1 = fluxStreamm.count();

    Mono<Float> sum1 = fluxStream.map(Student::getAverage_grade)
            .reduce(Float::sum);
    float mean1 = sum1.block() / count1.block();


    Mono<Double> sd_sum1 = fluxStream.map(a -> pow(a.getAverage_grade() - mean1, 2))
            .reduce(Double::sum);

    Double sd1 = sqrt(sd_sum1.block() / count1.block());



    System.out.println("\n===== Average and Standard Deviation of Students Who Have Finished Their Graduation ===== \n -> Mean: " + mean1 + "  Standard Deviation: " + sd1);


    // 8. The name of the eldest student.
    client.get().uri("localhost:8080/student/all")
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


    //9. Average number of professors per student. Note that some professors may not
    //have students and vice-versa.
    Long studentsNumber = client.get().uri("localhost:8080/student/all")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(Student.class)
            .count().block();


    Long relationshipsNumber = client.get().uri("localhost:8080/relationship/all")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(Teacher_student.class)
            .count().block();

    float average = (float) relationshipsNumber / studentsNumber;

    System.out.println("\n===== Average number of professors per student =====\n" + "-> " + average);


    //10.Name and number of students per professor, sorted in descending order.
    Flux<Student> students = client.get().uri("localhost:8080/student/all")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(Student.class);

    Flux<Student> teachers = client.get().uri("localhost:8080/teacher/all")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(Student.class);

    Flux<Teacher_student> relationships = client.get().uri("localhost:8080/relationship/all")
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



    // 11. Complete	data of all	students, by adding	the	names of their professors.
    long begin = System.currentTimeMillis();

    System.out.println("\n===== Complete data of all students =====");
    client.get().uri("localhost:8080/student/all")
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
    System.out.println("tempo NORMAL: " + (System.currentTimeMillis() - begin));


    // 11 Extra. Complete	data of all	students, by adding	the	names of their professors.
    begin = System.currentTimeMillis();

    System.out.println("\n===== Complete data of all students EXTRA =====");
    client.get().uri("localhost:8080/student/all")
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
                                try {
                                    sleep(100);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                                client.get().uri("localhost:8080/teacher/" + relationship.getTeacher_id())
                                        .accept(MediaType.TEXT_EVENT_STREAM)
                                        .retrieve()
                                        .bodyToFlux(Teacher.class)
                                        .doOnNext(t ->  System.out.println(" - " + t.getName()))
                                        .blockLast();
                            }
                        })
                        .blockLast();
            })
            .blockLast();
    System.out.println("tempo EXTRA: " + (System.currentTimeMillis() - begin));

    }


    public static WebClient getWebClient() {
        WebClient.Builder webClientBuilder = WebClient.builder();
        return webClientBuilder.build();

    }
}
