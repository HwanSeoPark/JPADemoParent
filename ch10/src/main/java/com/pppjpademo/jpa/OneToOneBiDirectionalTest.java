package com.pppjpademo.jpa;

import javax.persistence.*;

public class OneToOneBiDirectionalTest {

    private static final EntityManagerFactory emf = 
    		Persistence.createEntityManagerFactory("hello");

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

            User user = new User("홍길동");
            UserProfile userprofile = new UserProfile("P-11223344");

            user.setUserProfile(userprofile);// ✅ 편의 메서드로 양방향 설정
            
            em.persist(user); // CascadeType.ALL → userprofile 자동 persist

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
            User found = em.createQuery("select c from User c", User.class)
                               .getSingleResult();

            System.out.println("👤 유저 이름: " + found.getUserName());
            System.out.println("🪪 바이오: " + found.getUserProfile().getBio());
        } finally {
            em.close();
        }
    }

    // ✅ 3. 양방향 탐색 테스트
    public static void bidirectionalNavigationTest() {
        System.out.println("\n🟢 bidirectionalNavigationTest 시작");

        EntityManager em = emf.createEntityManager();

        try {
            UserProfile userprofile = em.createQuery("select p from UserProfile p", UserProfile.class)
                                  .getSingleResult();

            System.out.println("🪪 바이오: " + userprofile.getBio());
            System.out.println("👤 유저 이름: " + userprofile.getUser().getUserName());
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

            User user = em.createQuery("select c from User c", User.class)
                                .getSingleResult();
            em.remove(user); // userprofile도 함께 삭제되어야 함

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
