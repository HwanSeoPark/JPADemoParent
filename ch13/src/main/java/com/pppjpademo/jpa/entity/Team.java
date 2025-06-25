package com.pppjpademo.jpa.entity;

import java.util.*;

import javax.persistence.*;

@Entity
public class Team {
    @Id @GeneratedValue
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    public Team() {}
    public Team(String name) { this.name = name; }
    public String getName() { return name; }
    public List<Member> getMembers() { return members; }
}