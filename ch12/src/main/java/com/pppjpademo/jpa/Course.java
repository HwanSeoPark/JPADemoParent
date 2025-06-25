package com.pppjpademo.jpa;


import javax.persistence.*;
import java.util.*;

@Entity
public class Course {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    public Course() {}

    public Course(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public List<Enrollment> getEnrollments() { return enrollments; }
}
