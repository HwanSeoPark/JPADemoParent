package com.pppjpademo.jpa;


import javax.persistence.*;

import java.time.LocalDate;
import java.util.*;

@Entity
public class Student {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    public Student() {}

    public Student(String name) {
        this.name = name;
    }

    public void enroll(Course course, LocalDate date, Double score) {
        Enrollment enrollment = new Enrollment(this, course, date, score);
        enrollments.add(enrollment);
        course.getEnrollments().add(enrollment);
    }

    public String getName() { return name; }
    public List<Enrollment> getEnrollments() { return enrollments; }
}
