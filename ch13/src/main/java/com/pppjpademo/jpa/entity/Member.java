package com.pppjpademo.jpa.entity;

//import jakarta.persistence.*;
import java.util.*;

import javax.persistence.*;

@Entity
public class Member {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    public Member() {}
    public Member(String name, int age, Team team) {
        this.name = name;
        this.age = age;
        this.team = team;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public Team getTeam() { return team; }
    public List<Order> getOrders() { return orders; }
    public String toString() {
        return "Member{" + name + ", age=" + age + ", team=" + team.getName() + "}";
    }
}