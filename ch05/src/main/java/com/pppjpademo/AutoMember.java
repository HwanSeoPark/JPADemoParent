package com.pppjpademo;

import javax.persistence.*;

import lombok.*;

@Getter
@Setter
@Entity
public class AutoMember {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    public AutoMember() {}
    public AutoMember(String name) {
        this.name = name;
    }
}
