package com.pppjpademo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class IdentityStrategyDemo {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            IdentityMember m = new IdentityMember("철수");
            em.persist(m);
            System.out.println("🌱 생성된 ID (IDENTITY): " + m.getId());
            tx.commit();
        } finally {
            em.close();
            emf.close();
        }
    }
}
