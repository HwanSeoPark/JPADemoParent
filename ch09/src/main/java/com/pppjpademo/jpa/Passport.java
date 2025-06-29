package com.pppjpademo.jpa;

import javax.persistence.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@SequenceGenerator(
	    name = "passport_seq_generator",
	    sequenceName = "passport_seq", // DB 시퀀스 이름
	    initialValue = 1,
	    allocationSize = 100
	)
@Table(name = "passports")
public class Passport {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE,
    		generator="passport_seq_generator")
    private Long id;

    private String number;

    // Constructors    
    public Passport(String number) {
        this.number = number;
    }    
}