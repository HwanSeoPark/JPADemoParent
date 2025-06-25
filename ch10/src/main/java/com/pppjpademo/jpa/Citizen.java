package com.pppjpademo.jpa;

import javax.persistence.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Citizen {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToOne(mappedBy = "citizen", cascade = CascadeType.ALL)
    private Passport passport; // 비주인
    
    public Citizen(String name) {
    	this.name = name;
    }

    public void setPassport(Passport passport) {
        this.passport = passport;
        if (passport.getCitizen() != this) {
            passport.setCitizen(this);
        }
    }
}