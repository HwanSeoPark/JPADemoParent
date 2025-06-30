package com.pppjpademo.jpa;

import java.time.LocalDate;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main {
	
	static final String[] cities = {"Seoul", "Busan", "Incheon", "Daegu", "Daejeon"};
    static final String[] jobs = {"Developer", "Designer", "Manager", "Analyst", "Writer"};
    static final Random random = new Random();

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        for (int i = 0; i < 100_000; i++) {
            String email = "user" + i + "@example.com";
            String name = "User" + i;
            String phone = "010-" + (1000 + random.nextInt(9000)) + "-" + (1000 + random.nextInt(9000));
            String city = cities[random.nextInt(cities.length)];
            String job = jobs[random.nextInt(jobs.length)];
            LocalDate birth = LocalDate.of(1970 + random.nextInt(30), 1 + random.nextInt(12), 1 + random.nextInt(28));
            em.persist(new User(email, name, phone, city, job, birth));

            if (i % 1000 == 0) {
                em.flush(); // 데이타베이스 테이블에 인설트하기 위해서 플러시
                em.clear(); // 1차 캐시는 사이즈가 정해져 있어서 한번씩 비워주는 역할
            }
        }

        em.getTransaction().commit();

        // 인덱스를 사용하는 쿼리
        long start1 = System.currentTimeMillis();
        em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", "user99999@example.com")
                .getResultList();
        long end1 = System.currentTimeMillis();
        System.out.println("🔍 [With Index] 검색 시간: " + (end1 - start1) + "ms");

        // 인덱스를 사용하지 않는 쿼리 (예: 이름으로 full scan)
        long start2 = System.currentTimeMillis();
        em.createQuery("SELECT u FROM User u WHERE u.name = :name", User.class)
                .setParameter("name", "User99999")
                .getResultList();
        long end2 = System.currentTimeMillis();
        System.out.println("❌ [Without Index] 검색 시간: " + (end2 - start2) + "ms");

        em.close();
        emf.close();
    }

}
