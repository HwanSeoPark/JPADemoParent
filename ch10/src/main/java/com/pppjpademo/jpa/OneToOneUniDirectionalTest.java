package com.pppjpademo.jpa;

import javax.persistence.*;

public class OneToOneUniDirectionalTest {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

    public static void main(String[] args) {
        saveTest();                  // ✅ 저장 테스트
        clearAndFindTest();         // ✅ 재조회 테스트
        bidirectionalNavigationTest(); // ✅ 양방향 탐색 테스트
        deleteCascadeTest();        // ✅ 삭제 테스트

        emf.close();
    }

    // ✅ 1. 저장 테스트
    public static void saveTest() {
        System.out.println("\n🟢 saveTest 시작");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Citizen citizen = new Citizen("홍길동");
            Passport passport = new Passport("P-11223344");

            citizen.setPassport(passport); // ✅ 편의 메서드로 양방향 설정

            em.persist(citizen); // CascadeType.ALL → passport 자동 persist

            tx.commit();
            System.out.println("✅ 저장 완료");
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ✅ 2. 영속성 컨텍스트 초기화 후 조회 테스트
    public static void clearAndFindTest() {
        System.out.println("\n🟢 clearAndFindTest 시작");

        EntityManager em = emf.createEntityManager();

        try {
            Citizen found = em.createQuery("select c from Citizen c", Citizen.class)
                               .getSingleResult();

            System.out.println("👤 시민 이름: " + found.getName());
            System.out.println("🪪 여권 번호: " + found.getPassport().getNumber());
        } finally {
            em.close();
        }
    }

    // ✅ 3. 양방향 탐색 테스트
    public static void bidirectionalNavigationTest() {
        System.out.println("\n🟢 bidirectionalNavigationTest 시작");

        EntityManager em = emf.createEntityManager();

        try {
            Passport passport = em.createQuery("select p from Passport p", Passport.class)
                                  .getSingleResult();

            System.out.println("🪪 여권 번호: " + passport.getNumber());
            System.out.println("👤 소유 시민 이름: " + passport.getCitizen().getName());
        } finally {
            em.close();
        }
    }

    // ✅ 4. 삭제 테스트 (Cascade 확인)
    public static void deleteCascadeTest() {
        System.out.println("\n🟢 deleteCascadeTest 시작");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Citizen citizen = em.createQuery("select c from Citizen c", Citizen.class)
                                .getSingleResult();
            em.remove(citizen); // passport도 함께 삭제되어야 함

            tx.commit();
            System.out.println("🗑️ 삭제 완료");
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
