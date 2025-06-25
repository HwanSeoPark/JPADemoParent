package com.pppjpademo.jpabook;

import javax.persistence.*;

public class ClearVsDetachTest {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 1. 엔티티 생성 및 영속
            Member m1 = new Member(1L, "철수");
            Member m2 = new Member(2L, "영희");
            Member m3 = new Member(3L, "동영");

            em.persist(m1);
            em.persist(m2);
            em.persist(m3);
            em.flush(); // INSERT SQL 실행

            // 2. detach - 개별 엔티티만 준영속화
            em.detach(m1); // m1은 준영속 상태
            m1.setName("변경된 철수 (준영속 상태에서 변경)"); // DB 반영 안 됨

            // 3. clear - 전체 영속성 컨텍스트 초기화
            em.clear(); // m2, m3도 준영속 상태로 전환됨
            m2.setName("변경된 영희 (준영속 상태에서 변경)"); // DB 반영 안 됨
            m3.setName("변경된 동영 (준영속 상태에서 변경)"); // DB 반영 안 됨

            System.out.println("###############################################S");
            // 4. merge - 준영속 상태를 다시 영속화 (새로운 영속 인스턴스 반환)
            Member mergedM1 = em.merge(m1); // 변경사항이 복사되어 영속 상태로 전환됨
            Member mergedM2 = em.merge(m2);
            Member mergedM3 = em.merge(m3);

            System.out.println("🟢 mergedM1 = " + mergedM1);
            System.out.println("🟢 mergedM2 = " + mergedM2);
            System.out.println("🟢 mergedM3 = " + mergedM3);

            tx.commit(); // UPDATE SQL 실행됨
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
