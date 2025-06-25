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
            AutoMember m = new AutoMember("도윤");
            em.persist(m);
            System.out.println("🌱 생성된 ID (AUTO): " + m.getId());
            tx.commit();
        } finally {
            em.close();
            emf.close();
        }
    }
}
