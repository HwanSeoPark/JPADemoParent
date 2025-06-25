package com.pppjpademo.jpabook;

import javax.persistence.*;

public class JpaLifecycleMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 1. 비영속(transient) 상태
            Member member = new Member(1L, "John");
            System.out.println("🟡 비영속: " + member);

            // 2. 영속 상태
            // 쓰기 지연 SQL 저장소 : 1st insert query 저장...
            em.persist(member); // 이제부터 관리됨
            System.out.println("🟢 영속: persist() 호출 후");

            // 3. flush 없이 조회 (1차 캐시)           
            Member find = em.find(Member.class, 1L);
            System.out.println("🔎 조회된 이름 = " + find.getName());

            // 쓰기 지연 SQL 저장소에 SQL 쿼리를 실행시킬 수 있음.
            em.flush(); 
            
            // 4. 준영속(Detached) 상태
            em.detach(member);  // 관리 중단: 1차 캐시에 있는 Member.class + 1L 삭제된다
            member.setName("변경된 이름"); // 변경해도 반영되지 않음
            System.out.println("🔴 준영속: 이름 수정됨 (DB 반영 X)");

            // 5. 삭제 상태
            // 쓰기 지연 SQL 저장소 : 동일한 2nd insert query 저장
            // em.persist(member); // persist를 호출하는 것은 전이 상태 위배
            // Detached 상태에서는 merge()만 호출 가능
            em.merge(member); // 다시 영속화(관리 상태) :
                              // 1차 캐시에 Member.class + 1L의 member instance를 저장    
            
            em.flush(); // flush를 하면 더 이상 1차 캐시에 Member.class + 1L의
            	        // member instance가 없음? 
            			// : 아님. flush를 해도 1차 캐시는 유지됨
            
            em.remove(member); // Detach된 instance만 remove할 수 있음
            					// EntityClass 인스턴스를 삭제 -> 데이터베이스에서도 삭제
            System.out.println("⚫ 삭제 상태: remove() 호출됨");

            tx.commit(); // 쓰기 지연 SQL 저장소에 SQL 쿼리가 실행됨...
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
 
        // ERROR: Unique index or primary key violation: 
        // "PRIMARY KEY ON PUBLIC.MEMBER(ID) ( /* key:1 */ CAST(1 AS BIGINT), 'John')"; SQL statement:
    }
}
