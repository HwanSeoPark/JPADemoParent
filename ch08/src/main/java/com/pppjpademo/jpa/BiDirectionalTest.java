package com.pppjpademo.jpa;


import javax.persistence.*;
import java.util.List;

public class BiDirectionalTest {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("hello");

    public static void main(String[] args) {
        initData();                // 초기 데이터 저장
        testLazyLoading();        // 지연 로딩 확인
        testOrphanRemoval();      // 고아 객체 삭제 확인
        testChangeRelation();     // 연관관계 변경 확인
        testNPlusOne();           // N+1 문제 확인
        testFetchJoinSolution();  // N+1 문제 해결
        emf.close();
    }

    // 🔸 초기 데이터 저장
    private static void initData() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Order order = new Order("이순신");
            order.addItem(new OrderItem("모니터", 2));
            order.addItem(new OrderItem("마우스", 1));
            em.persist(order);

            tx.commit();
        } finally {
            em.close();
        }
    }

    // 🔸 지연 로딩 확인
    private static void testLazyLoading() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            System.out.println("\n🧪 [지연 로딩 테스트]");

            Order order = em.createQuery("select o from Order o", Order.class)
                            .setMaxResults(1)
                            .getSingleResult();

            System.out.println("📦 주문자: " + order.getCustomer());
            System.out.println("📦 주문 항목 조회 전 (SQL X)");

            for (OrderItem item : order.getOrderItems()) {
                System.out.println(" - " + item.getProduct() + " x " + item.getQuantity());
            }

            tx.commit();
        } finally {
            em.close();
        }
    }

    // 🔸 고아 객체 제거 확인
    private static void testOrphanRemoval() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            System.out.println("\n🧪 [고아 객체 제거 테스트]");

            Order order = em.createQuery("select o from Order o", Order.class)
                            .setMaxResults(1)
                            .getSingleResult();

            OrderItem removed = order.getOrderItems().get(0);
            order.removeItem(removed); // orphanRemoval = true → 자동 삭제

            tx.commit();
        } finally {
            em.close();
        }
    }

    // 🔸 연관관계 변경 테스트
    private static void testChangeRelation() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            System.out.println("\n🧪 [연관관계 변경 테스트]");

            Order newOrder = new Order("장보고");
            em.persist(newOrder);

            OrderItem item = em.createQuery("select i from OrderItem i", OrderItem.class)
                               .setMaxResults(1)
                               .getSingleResult();

            // 기존 연관관계 제거
            item.getOrder().removeItem(item);

            // 새 연관관계 설정
            newOrder.addItem(item);  // 연관관계 편의 메서드로 양방향 유지

            tx.commit();
        } finally {
            em.close();
        }
    }

    // 🔸 N+1 문제 유도
    private static void testNPlusOne() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            System.out.println("\n🧪 [N+1 문제 유도]");

            List<Order> orders = em.createQuery("select o from Order o", Order.class)
                                   .getResultList();

            for (Order order : orders) {
                for (OrderItem item : order.getOrderItems()) {
                    System.out.println(" - " + item.getProduct());
                }
            }

            tx.commit();
        } finally {
            em.close();
        }
    }
    
 // 🔸 Fetch Join을 이용한 N+1 해결
    private static void testFetchJoinSolution() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            System.out.println("\n🧪 [N+1 문제 해결 - Fetch Join 사용]");

            List<Order> orders = em.createQuery(
                    "select o from Order o join fetch o.orderItems", Order.class)
                    .getResultList();

            for (Order order : orders) {
                System.out.println("🧾 고객: " + order.getCustomer());
                for (OrderItem item : order.getOrderItems()) {
                    System.out.println(" - " + item.getProduct() + " x " + item.getQuantity());
                }
            }

            tx.commit();
        } finally {
            em.close();
        }
    }

}
