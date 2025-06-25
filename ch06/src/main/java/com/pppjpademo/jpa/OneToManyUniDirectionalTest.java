package com.pppjpademo.jpa;

import java.util.List;

import javax.persistence.*;


public class OneToManyUniDirectionalTest {
	private static final EntityManagerFactory emf = 
	        Persistence.createEntityManagerFactory("hello");

	private static EntityManager em;
	private static EntityTransaction tx;
	
	// 🔸 공통 초기화
    public static void init() {
        em = emf.createEntityManager();
        tx = em.getTransaction();
        tx.begin();
    }

    // 🔸 트랜잭션 종료 및 자원 정리
    public static void close() {
        try {
            if (tx != null && tx.isActive()) {
                tx.commit();
            }
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }
    
    // 🔸 테이블 초기화 (delete all)
    public static void clearTables() {
        System.out.println("\n🧹 clearTables: 테스트를 위한 테이블 초기화 수행 -----------------------------");

        em.createQuery("delete from OrderItem").executeUpdate();
        em.createQuery("delete from Order").executeUpdate();

        em.flush();  // 실제 SQL 전송
    }

    
    // 1. CascadeType.ALL + 즉시 flush 확인
    public static void testCascadeAndFlush() {
    	
        System.out.println("\n🧪 testCascadeAndFlush 시작 -----------------------------");

        Order order = new Order("고객A");
        order.addItem(new OrderItem("맥북", 1));
        order.addItem(new OrderItem("아이폰", 2));

        em.persist(order);  // cascade + @JoinColumn → 즉시 flush
        /*
        Hibernate: 
        insert into ORDERS (id, customerName) 
             values(default, ?)
        insert into OrderItem (id, productName, quantity) 
             values(default, ?, ?)
        insert into OrderItem (id, productName, quantity) 
             values(default, ?, ?)
        */
        // flush 메서드 호출하기 전에 이미 위 쿼리들이 실행됨:
        // -영속성 전이(CascadeType.ALL) 에 의해 OrderItem까지 persist 전파됨
        // -단방향 @OneToMany + @JoinColumn 매핑이기 때문에,
        //  JPA는 flush 타이밍을 em.persist()에 즉시 수행함
        // 즉시 insert가 필요한 이유:
        // -자식의 외래키(order_id)에 부모의 PK가 필요하기 때문에
        //  먼저 Order를 insert -> PK 확보 -> 자식 insert 시 FK 사용
        
        em.flush();         // 이 시점 전에 이미 insert 완료됨
    }
    
    // 2. FetchType.LAZY 로딩 시점 확인
    public static void testLazyLoading() {
        System.out.println("\n🧪 testLazyLoading 시작 -----------------------------");

        // 🔸 1. 테스트 데이터 저장
        Order orderToSave = new Order("고객A");
        orderToSave.addItem(new OrderItem("맥북", 1));
        orderToSave.addItem(new OrderItem("아이폰", 2));
        em.persist(orderToSave); // CascadeType.ALL로 자식도 persist됨
        em.flush();
        em.clear(); // 영속성 컨텍스트 초기화 -> 지연 로딩 실험 준비

        // 🔸 2. 지연 로딩 확인
        Order order = em.createQuery("select o from Order o", Order.class)
                        .setMaxResults(1)
                        .getSingleResult();

        System.out.println("📌 주문자: " + order.getCustomerName());

        // STS4 왜 좋아진거야?!!!!!!!!!!!
        List<OrderItem> list = order.getOrderItems();
        System.out.println("📦 지연 로딩 전 → 실제 SQL 아직 발생 X");

        for (OrderItem item : order.getOrderItems()) {
            System.out.println(" - " + item.getProductName() + " x " + item.getQuantity());
        }
    }

    
    // 3. orphanRemoval 기능 확인
    public static void testOrphanRemoval() {
        System.out.println("\n🧪 testOrphanRemoval 시작 -----------------------------");

        // 🔹 1. 테스트 데이터 저장
        Order order = new Order("고객B");
        order.addItem(new OrderItem("갤럭시북", 1));
        order.addItem(new OrderItem("갤럭시폰", 2));

        em.persist(order);  // CascadeType.ALL + orphanRemoval 설정
        em.flush();         // INSERT 쿼리 전송
        em.clear();         // 지연 로딩 확인을 위해 영속성 컨텍스트 초기화

        // 🔹 2. 엔티티 다시 조회 후 고아 객체 테스트
        Order foundOrder = em.find(Order.class, order.getId());
        System.out.println("📌 기존 항목 수: " + foundOrder.getOrderItems().size());

        OrderItem toRemove = foundOrder.getOrderItems().get(0);
        foundOrder.removeItem(toRemove);  // 고아 객체 → 자동 DELETE 대상

        em.flush();  // 여기서 DELETE 쿼리 발생
        System.out.println("🗑️ 첫 번째 항목 제거 완료 (flush 후 DELETE SQL 확인 가능)");
    }


    public static void main(String[] args) {
    	
    	try {
    		init();
    		
            testCascadeAndFlush();     
            clearTables();
            
            testLazyLoading();
            clearTables();

            testOrphanRemoval();
            clearTables();
            
            close();

        } finally {
            emf.close();
        }
    }
}
