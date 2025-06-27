package com.pppjpademo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class AutoStrategyDemo {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            AutoMember m1 = new AutoMember("도윤");
            AutoMember m2 = new AutoMember("지윤");
            em.persist(m1); // Hibernate:
                            //call next value for hibernate_sequence
            em.persist(m2);
            
            System.out.println("🌱 m1 : 생성된 ID (AUTO): " + m1.getId());
            System.out.println("🌱 m2 : 생성된 ID (AUTO): " + m2.getId());
            
            tx.commit();
        } finally {
            em.close();
            emf.close();
        }
    }
}
// AUTO로 설정 돼 있어서 만들어진 하이버네이트 시퀀스 
// Hibernate: create sequence hibernate_sequence 
//            start with 1 increment by 1 