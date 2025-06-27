package com.pppjpademo.jpa;


import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_city", columnList = "city"),
        @Index(name = "idx_birth_date", columnList = "birthDate")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 100)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    private String phone;

    private String city;

    private String jobTitle;

    private LocalDate birthDate;

    public User() {}

    public User(String email, String name, String phone, String city, String jobTitle, LocalDate birthDate) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.city = city;
        this.jobTitle = jobTitle;
        this.birthDate = birthDate;
    }

    // Getter/Setter 생략
}
