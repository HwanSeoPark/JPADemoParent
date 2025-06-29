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
            em.clear(); // 1차 캐시를 비운다...
            System.out.println("=== 재조회 시작 ===");

            // eager 모드는 대부분 left outer join으로 owner 인스턴스에 해당하는 테이블의 row와
            // owner 인스턴스의 자식 테이블의 row의 정보를 동시에 가져옴
            /*
             select
        		person0_.id as id1_1_0_,
        		person0_.name as name2_1_0_,
        		person0_.address_id as address_3_1_0_,
        		passport1_.id as id1_0_1_,
        		passport1_.number as number2_0_1_ 
    		from
        		persons person0_ 
    		left outer join
        		passports passport1_ 
            	on person0_.address_id=passport1_.id 
    		where
        		person0_.id=?
             */
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
