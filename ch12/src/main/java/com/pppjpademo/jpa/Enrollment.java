package com.pppjpademo.jpa;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Enrollment {

    @Id @GeneratedValue
    private Long id;

    // @ManyToOne 애노테이션만 사용해도 JPA는 디폴트로 외래키 컬럼을 생성.
    // @JoinColumn은 선택사항이며, 외래키 컬럼의 이름 등을 커스터마이징할 때 사용.
    @ManyToOne(fetch = FetchType.LAZY)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;

    private LocalDate enrolledDate;
    private Double score;

    public Enrollment() {}

    public Enrollment(Student student, Course course, LocalDate enrolledDate, Double score) {
        this.student = student;
        this.course = course;
        this.enrolledDate = enrolledDate;
        this.score = score;
    }

    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public LocalDate getEnrolledDate() { return enrolledDate; }
    public Double getScore() { return score; }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setEnrolledDate(LocalDate enrolledDate) {
        this.enrolledDate = enrolledDate;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
