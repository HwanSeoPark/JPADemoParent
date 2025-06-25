package com.pppjpademo.jpabook;

import javax.persistence.Entity;
import javax.persistence.Id;

// Entity 매니저가 마들어지면서 
// 이 어노테이션이 적용된 클래스를 스캔함
@Entity
public class Member {

    @Id // Primary key
    private Long id;
    private String name;

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
