package com.pppjpademo.jpa;

import java.util.List;
import javax.persistence.*;

public class ManyToOneUniDirectionalTest {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("hello");

    public static void main(String[] args) {
        initData();
        testLazyLoading();
        testNPlusOneProblem();
        testNPlusOneProblemSolvedWithFetchJoin();
        testForeignKeyConstraint();
        testChangeProduct();
        emf.close();
    }

    // 🔹 초기 데이터 등록
    private static void initData() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            for (int i = 1; i <= 100; i++) {
                Product product = new Product("상품" + i, 10000 + i);
                em.persist(product);

                OrderItem item = new OrderItem(product, i);
                em.persist(item);
            }

            // new OrderItem(product, i) 대입만 했을 뿐인데
            // @ManyToOne, @JoinColumn 어노테이션으로 인해 Hibernate가 이걸 보고
            // 포리진 키를 만들어버림
            
            tx.commit();
        } finally {
            em.close();
        }
    }

    // 🔹 지연 로딩 테스트
    private static void testLazyLoading() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            System.out.println("\n🧪 Lazy Loading 테스트");

            OrderItem item = em.createQuery("select i from OrderItem i", OrderItem.class)
                    .setMaxResults(1)
                    .getSingleResult();
            // Lazy 이기 때문에 OrderItem만 select 정보를 가져옴

            // Product를 null로 두지 않고 HibernateProxy로 만들어서 씀
            System.out.println("수량: " + item.getQuantity());
            System.out.println("🕐 상품명 조회 전 - SQL 없음");
            System.out.println("상품명: " + item.getProduct().getName());
            // 처음으로 Product를 사용 여기서 SQL 발생
            // Lazy로 인해 이때 Product 생성

            tx.commit();
        } finally {
            em.close();
        }
    }

    // 🔹 N+1 문제 유도 테스트 (Lazy Fetch로 인해 나는 문제)
    // fm 으로는 1+N이라 한다!!
    // 1 : select * from orderItem
    // 100(N,횟수) : select * 
    //		from product
    //		where orderItem.product_id = product.id
    private static void testNPlusOneProblem() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            System.out.println("\n🧪 N+1 문제 유도");

            List<OrderItem> items = em.createQuery("select i from OrderItem i", OrderItem.class)
                    .getResultList();

            // OrderItem을 100개 만드는데 100개의 쿼리를 하는데
            // Lazy fetch로 인해 Product도 100번의 쿼리를 날림
            // 이게 N + 1 문제
            int count = 0;
            for (OrderItem item : items) {
                count++;
                System.out.println("[" + count + "] 상품명: " + item.getProduct().getName()); // 여기서 N번 SQL
            }

            tx.commit();
        } finally {
            em.close();
        }
    }

    // 🔹 N+1 문제 해결 : Fetch Join
    private static void testNPlusOneProblemSolvedWithFetchJoin() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            System.out.println("\n✅ N+1 문제 해결 - Fetch Join 사용");

            // 🔹 Product까지 한 번에 조인하여 가져옴
            // 이때의 Product는 프록시가 아님!! 진짜를 가져옴
            List<OrderItem> items = em.createQuery(
                "select i from OrderItem i join fetch i.product", OrderItem.class)
                .getResultList();
            // 실제 fetch join은 표준 sql이 아님
            // : jpa에서 정의한 join임...단지 inner join 또는 left outer join을 사용함
            //   team을 즉시[eager] 로딩함!!!
            /*
            select
            	orderitem0_.id as id1_0_0_,
            	product1_.id as id1_1_1_,
            	orderitem0_.product_id as product_3_0_0_,
            	orderitem0_.quantity as quantity2_0_0_,
            	product1_.name as name2_1_1_,
            	product1_.price as price3_1_1_ 
        	from
            	OrderItem orderitem0_ 
        	inner join
            	Product product1_ 
                	on orderitem0_.product_id=product1_.id 
             */

            int count = 0;
            for (OrderItem item : items) {
                count++;
                System.out.println("[" + count + "] 상품명: " + item.getProduct().getName());  // SQL 발생 없음
            }

            tx.commit();
        } finally {
            em.close();
        }
    }

    // 🔹 외래 키 제약 조건 확인
    private static void testForeignKeyConstraint() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            System.out.println("\n🧪 외래 키 제약 테스트");

            Product product = em.createQuery("select p from Product p", Product.class)
                    .setMaxResults(1)
                    .getSingleResult();
            em.remove(product); // 참조 중이므로 삭제 불가 → 예외 발생

            tx.commit();
        } catch (Exception e) {
            System.err.println("🚫 외래키 제약 조건 위반으로 삭제 실패: " + e.getMessage());
            tx.rollback();
        } finally {
            em.close();
        }
    }

    // 🔹 연관관계 수정 테스트
    private static void testChangeProduct() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            System.out.println("\n🧪 연관관계 변경 테스트");

            Product newProduct = new Product("갤럭시", 1500000);
            em.persist(newProduct);

            OrderItem item = em.createQuery("select i from OrderItem i", OrderItem.class)
                    .setMaxResults(1)
                    .getSingleResult();

            System.out.println("🛒 변경 전 상품: " + item.getProduct().getName());

            item.changeProduct(newProduct); // 연관관계 변경

            em.flush();
            em.clear();

            OrderItem changed = em.find(OrderItem.class, item.getId());
            System.out.println("🔄 변경 후 상품: " + changed.getProduct().getName());

            tx.commit();
        } finally {
            em.close();
        }
    }
}
