package com.pppjpademo.jpa;

import javax.persistence.*;
import java.time.LocalDate;

public class EnrollmentAssociationTest {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

    public static void main(String[] args) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // Student, Course 생성
            Student s1 = new Student("홍길동");
            Student s2 = new Student("이몽룡");

            Course c1 = new Course("JPA");
            Course c2 = new Course("Spring");

            // 수강 등록
            s1.enroll(c1, LocalDate.of(2024, 3, 1), 95.0);
            s1.enroll(c2, LocalDate.of(2024, 3, 5), 88.0);
            s2.enroll(c1, LocalDate.of(2024, 3, 10), 78.0);

            // 저장
            em.persist(s1);
            em.persist(s2);
            em.persist(c1);
            em.persist(c2);

            tx.commit();
        } finally {
            em.close();
            emf.close();
        }
    }
}
