package com.pppjpademo.jpa;

import javax.persistence.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "persons")
public class Person {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id") // FK
    private Passport passport;

    // Constructors
    public Person(String name) {
        this.name = name;        
    }   
    
}