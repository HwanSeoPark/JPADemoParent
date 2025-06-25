package com.pppjpademo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import lombok.*;

@Getter
@Setter
@Entity
@SequenceGenerator(
    name = "member_seq_generator",
    sequenceName = "member_seq", // DB 시퀀스 이름
    initialValue = 100,
    allocationSize = 1
)
public class SequenceMember {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_generator")
    private Long id;

    private String name;

    public SequenceMember() {}
    public SequenceMember(String name) {
        this.name = name;
    }
}
