package com.pppjpademo.jpa;

import javax.persistence.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@SequenceGenerator(
	    name = "person_seq_generator",
	    sequenceName = "person_seq", // DB 시퀀스 이름
	    initialValue = 1,
	    allocationSize = 100
	)
@Table(name = "persons")
public class Person {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE,
    		generator="person_seq_generator")
    private Long id;

    private String name;

    // unique key : 중복은 허용하지 않지만, null 값은 허용  
    // 외래키 : 외래키는 원래 중복을 허용하지만, 개인과 여권이라는 특수성으로 중복을 허용하지 않겠다는 것임.
    // 가정 : 여기서는 이중 국적을 허용하지 않음(그러므로 개인은 단 하나의 여권만 가진다)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", unique=true) // FK
    private Passport passport;

    // Constructors
    public Person(String name) {
        this.name = name;        
    }   
    
}