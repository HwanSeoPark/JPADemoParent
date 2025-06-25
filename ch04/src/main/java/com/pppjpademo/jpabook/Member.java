package com.pppjpademo.jpabook;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Member {

    @Id
    private Long id;

    private String name;

    // ========== 📌 JPA 생명주기 콜백 ==========
    @PrePersist
    public void beforePersist() {
        System.out.println("📌 @PrePersist 호출됨 - INSERT 전: " + this.name);
    }

    @PostPersist
    public void afterPersist() {
        System.out.println("📌 @PostPersist 호출됨 - INSERT 후: " + this.name);
    }

    @PreUpdate
    public void beforeUpdate() {
        System.out.println("📌 @PreUpdate 호출됨 - UPDATE 전: " + this.name);
    }

    @PostUpdate
    public void afterUpdate() {
        System.out.println("📌 @PostUpdate 호출됨 - UPDATE 후: " + this.name);
    }

    @PreRemove
    public void beforeRemove() {
        System.out.println("📌 @PreRemove 호출됨 - DELETE 전: " + this.name);
    }

    @PostRemove
    public void afterRemove() {
        System.out.println("📌 @PostRemove 호출됨 - DELETE 후: " + this.name);
    }

    @PostLoad // Select 직후
    public void afterLoad() {
        System.out.println("📌 @PostLoad 호출됨 - 조회 직후: " + this.name);
    }
}
