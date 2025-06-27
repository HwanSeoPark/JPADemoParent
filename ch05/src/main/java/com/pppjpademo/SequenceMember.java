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
@SequenceGenerator( // 시퀀스 생성기
    name = "member_seq_generator", // 이름 지정
    sequenceName = "member_seq", // DB 시퀀스 이름
    initialValue = 1,
    allocationSize = 100
)
public class SequenceMember {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, 
                    generator = "member_seq_generator")
    private Long id;

    private String name;

    public SequenceMember() {}
    public SequenceMember(String name) {
        this.name = name;
    }
}
