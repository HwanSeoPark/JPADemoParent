package com.pppjpademo.jpabook;

import javax.persistence.*;

public class HelloJpa {

    public static void main(String[] args) {
        // 1. EntityManagerFactory 생성 (persistence.xml에 정의된 이름 사용)
        EntityManagerFactory emf = 
        		Persistence.createEntityManagerFactory("hello");

        // 2. EntityManager 생성
        EntityManager em = emf.createEntityManager();
        // 매니저가 @Entity 적용된 애를 스캔함
        
        // 3. 트랜잭션 획득
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();  // 트랜잭션 시작

            // === 저장 === : transient(비영속 상태) -> 엔티티 메니저가 관리를 하고 있지 않음
            Member member = new Member();
            member.setId(1L); // 수동으로 직접 ID[프라이머리 키]의 값을 설정
            member.setName("John");
            
            // 엔티티 메니저의 persisit() 메서드를 통해,
            // 엔티티 매니저가
            // 엔티티 클래스 인스턴스로 관리하겠다 선언
            em.persist(member);  // INSERT 발생

            // === 조회 ===
            Member findMember = em.find(Member.class, 1L); // find()메서드 역할 : 1차 캐시에 저장된 인스턴스의 정보를 가져옴
            System.out.println("조회된 이름: " + findMember.getName());
            // 아직 commit를 하지 않아 DB에는 정보가 없다
            
            tx.commit(); // 트랜잭션 커밋
            			 // 커밋하는 순간 H2데이타베이스에 업데이트됨
        } catch (Exception e) {
            tx.rollback();  // 오류 발생 시 롤백
        } finally {
            em.close();  // EntityManager 종료
        }

        emf.close();  // EntityManagerFactory 종료
    }
}
