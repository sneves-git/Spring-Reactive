
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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Thread.sleep;


public class ClientApplication {

    public static void main(String[] args) {

        WebClient client = getWebClient();
        long beginTime = System.currentTimeMillis();
        Flux<Student> allStudents = client.get().uri("localhost:8080/student/all")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Student.class);
        // 1. Names and birthdates of all students.
        System.out.println("\n===== Names And Birthdays From All Students =====");
        allStudents.doOnNext(cr -> System.out.println("-> Name: " + cr.getName() + " Birthday: " + cr.getBirth_date()))
                .blockLast();


        // 2. Total number of students.
        allStudents.count()
                .doOnNext(s -> System.out.println("\n===== Number Of Students =====\n-> " + s))
                .block();


        // 3. Total number of students that are active (i.e., that have less than 180 credits)
        System.out.println("\n===== Number Of Active Students (credits < 180) =====");
        allStudents.filter(v -> v.getCompleted_credits() < 180)
                .count()
                .doOnNext(s -> System.out.println("-> " + s))
                .block();


        // 4. Total number of courses completed for all students, knowing that each course is
        //worth 6 credits.
        System.out.println("\n===== Number Of Completed Courses From Each Student =====");
        allStudents.doOnNext(cr -> System.out.println("Name: " + cr.getName() + " || Completed Courses:" + cr.getCompleted_credits() / 6))
                .blockLast();


        // 5. Data of students that are in the last year of their graduation (i.e., whose credits
        //are at least 120 and less than 180). This list should be sorted, such that students
        //closer to completion should come first. These data do not need to include the
        //professors.
        System.out.println("\n===== Students that are in the last year =====");
        allStudents.filter(s -> (s.getCompleted_credits() >= 120 && s.getCompleted_credits() < 180))
                .sort((a, b) -> b.getCompleted_credits().compareTo(a.getCompleted_credits()))
                .doOnNext(cr -> System.out.println("-> Name: " + cr.getName() + " || Completed Courses:" + cr.getCompleted_credits() / 6))
                .blockLast();


        // 6. Average and standard deviations of all student grades.

        Mono<Long> count = allStudents.count();
        Mono<Float> sum = allStudents.map(Student::getAverage_grade)
                .reduce(Float::sum);
        float mean = 0f;
        if (sum.block() != null && count.block() != null) {
            mean = sum.block() / count.block();
        }

        float finalMean = mean;
        Mono<Double> sd_sum = allStudents.map(a -> pow(a.getAverage_grade() - finalMean, 2))
                .reduce(Double::sum);
        double sd = 0;
        if (sd_sum.block() != null && count.block() != null) {
            sd = sqrt(sd_sum.block() / count.block());
        }
        System.out.println("\n===== Average and Standard Deviation From Students Grades ===== \n-> Mean: " + mean + "  Standard Deviation: " + sd);


        // 7. Average and standard deviations of students who have finished their graduation
        //(with 180 credits).

        Flux<Student> fluxStreamm = allStudents
                .filter(s -> s.getCompleted_credits() >= 180);

        Mono<Long> count1 = fluxStreamm.count();

        Mono<Float> sum1 = allStudents.map(Student::getAverage_grade)
                .reduce(Float::sum);
        float mean1 = 0f;
        if (sum1.block() != null && count1.block() != null) {
            mean1 = sum1.block() / count1.block();

        }

        float finalMean1 = mean1;
        Mono<Double> sd_sum1 = allStudents.map(a -> pow(a.getAverage_grade() - finalMean1, 2))
                .reduce(Double::sum);

        double sd1 = 0;
        if (sd_sum.block() != null && count1.block() != null) {
            sd1 = sqrt(sd_sum1.block() / count1.block());
        }


        System.out.println("\n===== Average and Standard Deviation of Students Who Have Finished Their Graduation ===== \n -> Mean: " + mean1 + "  Standard Deviation: " + sd1);


        // 8. The name of the eldest student.
        allStudents.reduce((a, b) -> {
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


        Long studentsNumber = allStudents.count().block();
        Flux<Teacher_student> allRelationships = client.get().uri("localhost:8080/relationship/all")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Teacher_student.class);

        Long relationshipsNumber = allRelationships.count().block();

        float average = 0;
        if (relationshipsNumber != null && studentsNumber != null) {
            average = (float) relationshipsNumber / studentsNumber;
        }
        System.out.println("\n===== Average number of professors per student =====\n" + "-> " + average);


        //10.Name and number of students per professor, sorted in descending order.
        Flux<Teacher> teachers = client.get().uri("localhost:8080/teacher/all")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Teacher.class);

        teachers.publishOn(Schedulers.boundedElastic()).map(teacher -> {
                    Flux<Teacher_student> TeacherRelationship = client.get().uri("localhost:8080/relationship/teacher/" + teacher.getId())
                            .accept(MediaType.TEXT_EVENT_STREAM)
                            .retrieve()
                            .bodyToFlux(Teacher_student.class);
                    AtomicReference<Flux<Integer>> ids = new AtomicReference<>(Flux.just(teacher.getId()));
                    TeacherRelationship.doOnNext(relationship -> {
                        ids.set(Flux.merge(ids.get(), Flux.just(relationship.getStudent_id())));

                    }).blockLast();
                    return ids;
                }).sort((a, b) -> Objects.requireNonNull(b.get().count().block()).compareTo(Objects.requireNonNull(a.get().count().block())))
                .doOnNext(cr -> {
                    System.out.print("\nProfessor: ");
                    client.get().uri("localhost:8080/teacher/" + cr.get().take(1).blockLast())
                            .accept(MediaType.TEXT_EVENT_STREAM)
                            .retrieve()
                            .bodyToFlux(Teacher.class)
                            .doOnNext(prof -> {
                                System.out.println(prof.getName());
                            }).blockLast();
                    AtomicInteger counter = new AtomicInteger();
                    if (cr.get().count().block() != null) {
                        System.out.println("Number Of Students: " + (cr.get().count().block() - 1));
                        if (cr.get().count().block() - 1 != 0) {
                            cr.get().publishOn(Schedulers.boundedElastic()).doOnNext(id -> {
                                        if (counter.get() != 0) {
                                            client.get().uri("localhost:8080/student/" + id)
                                                    .accept(MediaType.TEXT_EVENT_STREAM)
                                                    .retrieve()
                                                    .bodyToFlux(Student.class)
                                                    .doOnNext(student -> {
                                                        System.out.println(" -> " + student.getName());
                                                    }).blockLast();
                                        } else {
                                            System.out.println("Students");
                                        }
                                        counter.set(1);
                                    }
                            ).blockLast();
                        }
                    }


                }).blockLast();


        // 11. Complete	data of all	students, by adding	the	names of their professors.
        long begin = System.currentTimeMillis();

        System.out.println("\n===== Complete data of all students =====");
        allStudents
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(s -> {
                    System.out.println("\nName: " + s.getName() +
                            " || Birthday: " + s.getBirth_date() +
                            " || Completed credits: " + s.getCompleted_credits() +
                            " || Average grade: " + s.getAverage_grade() +
                            "\nTeachers:");
                    allRelationships.publishOn(Schedulers.boundedElastic()).doOnNext(relationship -> {
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
        System.out.println("Tempo do programa inteiro: " + (System.currentTimeMillis() - beginTime));
        
        // 11 Extra. Complete	data of all	students, by adding	the	names of their professors.
        begin = System.currentTimeMillis();

        System.out.println("\n===== Complete data of all students EXTRA =====");
        allStudents.publishOn(Schedulers.boundedElastic())
                .doOnNext(s -> {
                    System.out.println("\nName: " + s.getName() +
                            " || Birthday: " + s.getBirth_date() +
                            " || Completed credits: " + s.getCompleted_credits() +
                            " || Average grade: " + s.getAverage_grade() +
                            "\nTeachers:");

                    Flux<Teacher_student> relationship = client.get().uri("localhost:8080/relationship/student/" + s.getId())
                            .accept(MediaType.TEXT_EVENT_STREAM)
                            .retrieve()
                            .bodyToFlux(Teacher_student.class);

                    relationship.hasElements()
                            .publishOn(Schedulers.boundedElastic())
                            .doOnNext(it -> {
                                if (it) {
                                    relationship.publishOn(Schedulers.boundedElastic()).doOnNext(relation -> {
                                        client.get().uri("localhost:8080/teacher/" + relation.getTeacher_id())
                                                .accept(MediaType.TEXT_EVENT_STREAM)
                                                .retrieve()
                                                .bodyToFlux(Teacher.class)
                                                .doOnNext(t -> System.out.println(" - " + t.getName()))
                                                .blockLast();
                                    }).blockLast();
                                } else {
                                    System.out.println("No professors");

                                }
                            }).block();
                })
                .blockLast();
        System.out.println("tempo EXTRA1: " + (System.currentTimeMillis() - begin));


        // 11 Extra. Complete	data of all	students, by adding	the	names of their professors.
        begin = System.currentTimeMillis();

        System.out.println("\n===== Complete data of all students EXTRA =====");
       allStudents.publishOn(Schedulers.boundedElastic())
                .doOnNext(s -> {
                    System.out.println("\nName: " + s.getName() +
                            " || Birthday: " + s.getBirth_date() +
                            " || Completed credits: " + s.getCompleted_credits() +
                            " || Average grade: " + s.getAverage_grade() +
                            "\nTeachers:");
                    allRelationships.publishOn(Schedulers.boundedElastic()).doOnNext(relationship -> {
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
                                            .doOnNext(t -> System.out.println(" - " + t.getName()))
                                            .blockLast();
                                }
                            })
                            .blockLast();
                })
                .blockLast();
        System.out.println("tempo EXTRA2: " + (System.currentTimeMillis() - begin));

    }


    public static WebClient getWebClient() {
        WebClient.Builder webClientBuilder = WebClient.builder();
        return webClientBuilder.build();

    }
}
