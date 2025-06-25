package com.pppjpademo.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class OneToOneUniDirectionalTest {
	
	public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // ✅ 1. 연관 엔티티 생성
            Passport passport = new Passport("P-123456789");
            Person person = new Person("홍길동");
            person.setPassport(passport); // 연관관계 설정

            // ✅ 2. 연관관계 Cascade 확인: Person만 persist 해도 Passport 저장됨
            em.persist(person); // CascadeType.ALL → passport 자동 저장

            tx.commit();

            // ✅ 3. 영속성 컨텍스트 초기화 후 다시 조회
            em.clear();
            System.out.println("=== 재조회 시작 ===");

            Person foundPerson = em.find(Person.class, person.getId());
            System.out.println("👤 이름: " + foundPerson.getName());
            System.out.println("🪪 여권번호: " + foundPerson.getPassport().getNumber());

            // ✅ 4. 삭제 테스트 (Cascade 적용 확인)
            tx.begin();
            em.remove(foundPerson); // person 제거 시 passport도 같이 제거됨
            tx.commit();

            System.out.println("✅ 삭제 완료");

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
