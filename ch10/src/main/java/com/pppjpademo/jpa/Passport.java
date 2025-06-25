package com.pppjpademo.jpa;

import javax.persistence.*;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Passport {

    @Id @GeneratedValue
    private Long id;

    private String number;

    @OneToOne
    @JoinColumn(name = "citizen_id")
    private Citizen citizen; // 연관관계의 주인 (외래 키 소유)
    
    public Passport(String number) {
    	this.number = number;
    }

    // getter/setter
}