package com.pppjpademo.jpa;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Student {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<Course> courses = new HashSet<>();

    public void addCourse(Course course) {
        courses.add(course);
        course.getStudents().add(this); // 양방향 설정
    }

    // getter, setter
}