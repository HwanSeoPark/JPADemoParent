// DataRunner.java
package com.pppjpademo.jpa.repository;

import java.util.List;
import javax.persistence.*;

import com.pppjpademo.jpa.dto.MemberOrderStats;
import com.pppjpademo.jpa.entity.*;

public class DataRunner {
    private EntityManager em;

    public void run() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        em = emf.createEntityManager();

        insertSampleData();

        caseWhenExample();
        subqueryExample();
        groupByHavingExample();
        joinExample();
        fetchJoinExample();
        inClauseExample();
        likeExample();
        betweenExample();
        orderByExample();
        distinctExample();
        isNullExample();
        aggregateFunctionsExample();
        leftJoinOnExample();
        rightJoinOnExample();
        complexDtoExample();
        caseWhenGroupByExample();
        subselectFetchingExample();
        parameterBindingExample();

        em.close();
        emf.close();
    }

    private void insertSampleData() {
        em.getTransaction().begin();

        Team dev = new Team("개발팀");
        Team sales = new Team("영업팀");
        em.persist(dev);
        em.persist(sales);

        Member kim = new Member("김철수", 29, dev);
        Member lee = new Member("이영희", 34, dev);
        Member park = new Member("박민수", 41, sales);
        em.persist(kim);
        em.persist(lee);
        em.persist(park);

        Product p1 = new Product("노트북", 1200000);
        Product p2 = new Product("모니터", 300000);
        Product p3 = new Product("키보드", 80000);
        em.persist(p1);
        em.persist(p2);
        em.persist(p3);

        em.persist(new Order(kim, p1, 2));
        em.persist(new Order(kim, p3, 5));
        em.persist(new Order(lee, p2, 1));
        em.persist(new Order(park, p1, 1));

        em.getTransaction().commit();
    }

    private void caseWhenExample() {
        System.out.println("\n✅ [조건식 - CASE] 결과:");
        String jpql = "SELECT CASE WHEN m.age >= 40 THEN '시니어' WHEN m.age >= 30 THEN '미들' ELSE '주니어' END FROM Member m";
        List<String> result = em.createQuery(jpql, String.class).getResultList();
        result.forEach(System.out::println);
    }

    private void subqueryExample() {
        System.out.println("\n✅ [서브쿼리 - 평균 이상 나이]:");
        String jpql = "SELECT m FROM Member m WHERE m.age >= (SELECT AVG(m2.age) FROM Member m2)";
        List<Member> result = em.createQuery(jpql, Member.class).getResultList();
        result.forEach(System.out::println);
    }

    private void groupByHavingExample() {
        System.out.println("\n✅ [GROUP BY + HAVING] 회원별 총 수량 >= 3:");
        String jpql = "SELECT new com.intheeast.jpa.dto.MemberOrderStats(m.name, SUM(o.quantity)) FROM Order o JOIN o.member m GROUP BY m.name HAVING SUM(o.quantity) >= 3";
        List<MemberOrderStats> result = em.createQuery(jpql, MemberOrderStats.class).getResultList();
        result.forEach(System.out::println);
    }

    private void joinExample() {
        System.out.println("\n✅ [조인 - 팀별 회원 조회]:");
        String jpql = "SELECT m.name, t.name FROM Member m JOIN m.team t";
        List<Object[]> result = em.createQuery(jpql, Object[].class).getResultList();
        result.forEach(arr -> System.out.println("회원: " + arr[0] + ", 소속팀: " + arr[1]));
    }

    private void fetchJoinExample() {
        System.out.println("\n✅ [FETCH JOIN] 회원과 팀 함께 조회:");
        String jpql = "SELECT m FROM Member m JOIN FETCH m.team";
        List<Member> result = em.createQuery(jpql, Member.class).getResultList();
        result.forEach(System.out::println);
    }

    private void inClauseExample() {
        System.out.println("\n✅ [IN 절] 특정 이름 포함 회원:");
        String jpql = "SELECT m FROM Member m WHERE m.name IN ('김철수', '이영희')";
        List<Member> result = em.createQuery(jpql, Member.class).getResultList();
        result.forEach(System.out::println);
    }

    private void likeExample() {
        System.out.println("\n✅ [LIKE 절] 이름에 '희' 포함:");
        String jpql = "SELECT m FROM Member m WHERE m.name LIKE '%희%'";
        List<Member> result = em.createQuery(jpql, Member.class).getResultList();
        result.forEach(System.out::println);
    }

    private void betweenExample() {
        System.out.println("\n✅ [BETWEEN] 나이 30~40:");
        String jpql = "SELECT m FROM Member m WHERE m.age BETWEEN 30 AND 40";
        List<Member> result = em.createQuery(jpql, Member.class).getResultList();
        result.forEach(System.out::println);
    }

    private void orderByExample() {
        System.out.println("\n✅ [ORDER BY] 나이 내림차순:");
        String jpql = "SELECT m FROM Member m ORDER BY m.age DESC";
        List<Member> result = em.createQuery(jpql, Member.class).getResultList();
        result.forEach(System.out::println);
    }

    private void distinctExample() {
        System.out.println("\n✅ [DISTINCT] 중복 제거된 팀 이름:");
        String jpql = "SELECT DISTINCT t.name FROM Member m JOIN m.team t";
        List<String> result = em.createQuery(jpql, String.class).getResultList();
        result.forEach(System.out::println);
    }

    private void isNullExample() {
        System.out.println("\n✅ [IS NULL] 팀이 없는 회원:");
        String jpql = "SELECT m FROM Member m WHERE m.team IS NULL";
        List<Member> result = em.createQuery(jpql, Member.class).getResultList();
        result.forEach(System.out::println);
    }

    private void aggregateFunctionsExample() {
        System.out.println("\n✅ [집계 함수] COUNT, MAX, MIN:");
        Object[] result = em.createQuery("SELECT COUNT(m), MAX(m.age), MIN(m.age) FROM Member m", Object[].class).getSingleResult();
        System.out.println("회원 수: " + result[0] + ", 최고 나이: " + result[1] + ", 최저 나이: " + result[2]);
    }

    private void leftJoinOnExample() {
        System.out.println("\n✅ [LEFT JOIN ON] 주문 없는 회원도 포함:");
        String jpql = "SELECT m.name, o.quantity FROM Member m LEFT JOIN Order o ON o.member = m";
        List<Object[]> result = em.createQuery(jpql, Object[].class).getResultList();
        result.forEach(r -> System.out.println("회원: " + r[0] + ", 수량: " + r[1]));
    }

    private void rightJoinOnExample() {
        System.out.println("\n✅ [RIGHT JOIN ON] 모든 주문과 회원 이름:");
        String jpql = "SELECT o.quantity, m.name FROM Order o RIGHT JOIN o.member m";
        List<Object[]> result = em.createQuery(jpql, Object[].class).getResultList();
        result.forEach(r -> System.out.println("수량: " + r[0] + ", 회원: " + r[1]));
    }

    private void complexDtoExample() {
        System.out.println("\n✅ [복합 DTO] 회원 이름별 총 주문 수량:");

        String jpql = 
            "SELECT new com.pppjpademo.jpa.dto.MemberOrderStats(m.name, SUM(o.quantity)) " +
            "FROM Order o JOIN o.member m " +
            "GROUP BY m.name";

        List<MemberOrderStats> result = em.createQuery(jpql, MemberOrderStats.class).getResultList();
        result.forEach(System.out::println);
    }
    
    private void caseWhenGroupByExample() {
        System.out.println("\n✅ [CASE + GROUP BY 혼합] 나이 구간별 회원 수:");
        String jpql = 
        	    "SELECT m.name, " +
        	    "  CASE " +
        	    "    WHEN m.age < 30 THEN '20대 이하' " +
        	    "    WHEN m.age BETWEEN 30 AND 39 THEN '30대' " +
        	    "    ELSE '40대 이상' " +
        	    "  END, " +
        	    "  COUNT(m) " +
        	    "FROM Member m " +
        	    "GROUP BY m.name, " +
        	    "  CASE " +
        	    "    WHEN m.age < 30 THEN '20대 이하' " +
        	    "    WHEN m.age BETWEEN 30 AND 39 THEN '30대' " +
        	    "    ELSE '40대 이상' " +
        	    "  END";


        List<Object[]> result = em.createQuery(jpql, Object[].class).getResultList();
        result.forEach(r -> System.out.println("연령대: " + r[0] + ", 수: " + r[1]));
    }

    private void subselectFetchingExample() {
        System.out.println("\n✅ [SUBSELECT FETCHING] 주문이 있는 회원 조회:");
        String jpql = "SELECT m FROM Member m WHERE m IN (SELECT o.member FROM Order o)";
        List<Member> result = em.createQuery(jpql, Member.class).getResultList();
        result.forEach(System.out::println);
    }

    private void parameterBindingExample() {
        System.out.println("\n✅ [Set Parameter 다양한 방식] 회원 나이 조건 조회:");
        String jpql = "SELECT m FROM Member m WHERE m.age > :age AND m.name = :name";
        TypedQuery<Member> query = em.createQuery(jpql, Member.class);
        query.setParameter("age", 30);
        query.setParameter("name", "이영희");
        List<Member> result = query.getResultList();
        result.forEach(System.out::println);
    }
}