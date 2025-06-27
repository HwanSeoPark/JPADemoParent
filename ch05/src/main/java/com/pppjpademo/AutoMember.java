package com.pppjpademo;

import javax.persistence.*;

import lombok.*;

@Getter
@Setter
@Entity
public class AutoMember {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) 
    // AUTO를 보고 하이버네이트가 시퀀스를 자동으로 만듦
    // allocation size 1이라서 매니저가 엔티티 클래스 한개를 만들 때마다 DB에 next를 계속 물어봄
    // 그래서 AUTO는 사용하지 않는다
    private Long id;

    private String name;

    public AutoMember() {}
    public AutoMember(String name) {
        this.name = name;
    }
}
