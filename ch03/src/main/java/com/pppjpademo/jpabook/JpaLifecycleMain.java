package com.pppjpademo.jpabook;

import javax.persistence.*;

public class JpaLifecycleMain {
	
	public static void afterFirstEntityManagerClose(EntityManager em) {

		 EntityTransaction tx = em.getTransaction();
	     
	     try {
	            tx.begin();
	            
	            // 현재 ID가 1L인 Member 엔티티 클래스 인스턴스는 detach 상태.
	            // find 메서드는 우선 1차 캐시에서 해당 엔티티 클래스 인스턴스 정보를 찾는데,	            
	            // 현재 1차 캐시에는 그 어떤 엔티티 클래스 인스턴스 정보를 유지하고 있지 않음
	            // 그래서 데이터베이스로부터 id가 1d인 member 테이블의 row를 가지고 와서
	            // 엔티티 클래스 인스턴스를 만들고, 이 인스턴스를 1차 캐시에 저장하고 난 후,
	            // 이 인스턴스의 참조값을 리턴함
	            Member member = em.find(Member.class, 1L);
	            Long id = member.getId();
	            
	            tx.commit();
	            
	     	} catch (Exception e) {
	            tx.rollback();
	            e.printStackTrace();
	        } finally {
	            em.close();

	        }	   		
	}

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 1. 비영속 상태 (Transient)
            Member member = new Member(1L, "지한");
            System.out.println("🟡 비영속 상태: " + member);

            // 2. 영속 상태 (Managed)
            em.persist(member);
            System.out.println("🟢 영속 상태: persist() 호출");

            // 2-1. Flush 전 → DB에는 INSERT 안 됨
            System.out.println("🧪 flush 전: 아직 DB에 INSERT되지 않음");

            // 2-2. flush 강제 호출
            em.flush();
            System.out.println("🚀 flush() 호출 → DB 반영됨 (INSERT)");

            // 3. Dirty Checking (변경 감지) : EntityClass가 변경사항 발생
            // Dirty -> 변경사항이 발생하였다
            member.setName("변경된 지한");  // 변경 감지 대상
            System.out.println("✏️ 엔티티 값 변경: Dirty Checking 대상 설정");

            // 4. JPQL 실행 → flush 자동 발생
            System.out.println("🧪 JPQL 실행 → flush() 자동 호출 + SQL 동기화");
            // JPQL(Java Persistence query language) statement 실행
            // QueryDSL : JPQL Builder
            em.createQuery("select m from Member m", Member.class).getResultList();
            // JPQL을 가지고 엔티티 메니저가 SQL 쿼리를 만들어서 즉시 실행           
            // 3번에서 이름이 변경된걸 확인하고 update 쿼리한후 select 쿼리를 수행함
            
            // 5. 준영속 상태 (Detached)
            em.detach(member);  // 관리 중단 : 1차 캐시에서 삭제됨.
            member.setName("무시될 이름"); // 디테치 상태에서 이름 변경은 무시된다
            							// 관리 상태에서만 가능
            System.out.println("🔴 준영속 상태 → 이름 변경 무시됨");

            // 6. 삭제 상태
            Member member2 = new Member(2L, "삭제될 유저");
            em.persist(member2); // insert 쿼리 저장함
            em.remove(member2);
            System.out.println("⚫ 삭제 상태 → remove() 호출");

            tx.commit(); // flush + commit
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
//            em.close();
//            emf.close();
        }
        
        afterFirstEntityManagerClose(em);

        // 1차 캐시에 id가 1인 Member 엔티티 클래스 인스턴스가 있음
        // 그런데 이런 상태일때 merge를 해버리면 동일한 id가 2개인 상태가 됨
//        em.merge(member);
        
        
    }
}
