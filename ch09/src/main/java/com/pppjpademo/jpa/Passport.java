package com.pppjpademo.jpa;

import javax.persistence.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "passports")
public class Passport {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;

    // Constructors
    
    public Passport(String number) {
        this.number = number;
    }    
}