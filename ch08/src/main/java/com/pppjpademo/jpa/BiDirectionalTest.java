package com.pppjpademo.jpa;


import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

public class BiDirectionalTest {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("hello");

    public static void main(String[] args) {
//      persistParentWithChildBiDirection(); // 초기 데이터 저장
//    	persistChildWhileParentIsTransient();
//    	persistEntitiesBeforeSettingRelationship();
//    	persistWithoutConvenienceMethod();
//    	initWithoutSettingInverseSide();
//        checkOrderLazyFetching();
//    	List<Long> listIds = prepareOrderItemsForPrint();
//    	printTeamNamesForMemberIds(listIds);
    	
//        testOrphanRemoval();      // 고아 객체 삭제 확인
//        testChangeRelation();     // 연관관계 변경 확인
//        testNPlusOne();           // N+1 문제 확인
//        testFetchJoinSolution();  // N+1 문제 해결
        
//        createOrderWithItems();
//        updateOrderItems();
//        removeAndReAdd();
    	
//    	initTestData();
//    	printItem();
//    	selectiveUpdateItem();
//    	conditionalDeleteItem();
//    	appendNewItems();
    	
//    	List<Long> listIds = prepareOrderItemsForPrint();
//    	addMoreOrderItemsToExistingOrders();
//    	verifyOrderItemsInDatabase();
    	
//    	deleteOrderAndCascadeItems("디자인팀");
        emf.close();
    }

    // 정상적인 부모 자식 관계 설정과 cascade 시연
    private static void persistParentWithChildBiDirection() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 부모 자식 추가하는 순서는 상관없다
//            OrderItem item1 = new OrderItem("모니터", 2);
//            OrderItem item2 = new OrderItem("마우스", 1)
            
            Order order = new Order("이순신");
            
            order.addItem(new OrderItem("모니터", 2)); // 편의 메서드
            order.addItem(new OrderItem("마우스", 1)); // 편의 메서드
            
            // 다음 설정으로 인해 insert 쿼리가 즉시 db에게 전송됨
            // : db의 indentity로부터 id를 얻기 위해서...
            // @GeneratedValue(strategy = GenerationType.IDENTITY)
            // 또한 cascade = CascadeType.ALL 설정으로 인해
            // 부모 엔티티 클래스 인스턴스가 영속화 되면 자식 클래스 인스턴스도 영속화됨
            // 그래서 부코 엔티티 클래스를 persist하니까 자동으로 자식 엔티티 클래스도 persist됨
            em.persist(order); // cascade에 의해 item도 함께 저장됨

            tx.commit();
        } finally {
            em.close();
        }
    }
    
    // 부모 엔티티가 아직 영속화되지 않았는데 자식 엔티티를 먼저 persist()하면 문제가 발생함
    private static List<Long> persistChildWhileParentIsTransient() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        List<Long> retOrderItemIds = null;
        try {
            tx.begin();
            
            OrderItem item1 = new OrderItem("모니터", 2);

            // Order는 아직 persist()되지 않은 transient 상태
            Order order = new Order("이순신");
            
            item1.setOrder(order);
            order.addItem(item1);
            
            // em.persist(order); <-- 부모를 해야함
            
            // 부모가 영속 상태가 아닌데, 자식을 영속 상태로 지정하면서 예외 발생!!
            // 이렇게 작성하면 안된다
            em.persist(item1); // order가 아직 transient 상태이므로 외래키를 참조할 수 없음.           
            				   // 외래키가 null 값으로 채워짐
            tx.commit();
        } finally {
            em.close();
        }
        
        return retOrderItemIds;
    }
    
    // 연관관계를 설정하기 전에 먼저 저장하는 잘못된 코드
    private static List<Long> persistEntitiesBeforeSettingRelationship() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        List<Long> retOrderItemIds = null;
        try {
            tx.begin();
            
            OrderItem item1 = new OrderItem("모니터", 2);
            OrderItem item2 = new OrderItem("마우스", 1);

            Order order = new Order("이순신"); 
            em.persist(order); 
            
            em.persist(item1); 
            em.persist(item2); 
            
            item1.setOrder(order);
            item2.setOrder(order);
            
            //////////////////////////////////////////////////////
            
            System.out.println(item1.toString());
            System.out.println(item2.toString());            
            
            List<Long> orderItemIds = new ArrayList<>();
            orderItemIds.add(item1.getId());
            
            orderItemIds.add(item2.getId());
            
            retOrderItemIds = orderItemIds;

            em.flush();
            em.clear();

            OrderItem gotOrderItem = em.find(OrderItem.class, orderItemIds.get(0));
            System.out.println(gotOrderItem.toString());            

            tx.commit();
        } finally {
            em.close();
        }
        
        return retOrderItemIds;
    }
    
    // FK 미설정 문제 발생
    private static void persistWithoutConvenienceMethod() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Order order = new Order("김지영");

            OrderItem item1 = new OrderItem("SSD", 1);
            OrderItem item2 = new OrderItem("RAM", 2);

            // 부모의 편의 메서드
//            order.addItem(item1); 
//            order.addItem(item2);
            order.getOrderItems().add(item1); 
            order.getOrderItems().add(item2);

            // 부모-자식 관계 미설정
            // item1.setOrder(order); 
            // item2.setOrder(order);
            // 데이터 베이스로 보면 primary key가 null 값이 됨

            em.persist(order); // 자식 테이블의 row가 생성될 때 
            				   // 부모의 프라이머리 키인 외래키가 설정되지 않음
            tx.commit();

            System.out.println("# 편의 메서드 미사용 시: " + order.getOrderItems());
            System.out.println("# 각 OrderItem의 order 필드 확인: " + item1.getOrder() + ", " + item2.getOrder());

        } finally {
            em.close();
        }
    }
    
    // DB은 참조 관계가 설정되었지만, 객체 그래프가 불완전하게 구성됨
    private static void initWithoutSettingInverseSide() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Order order = new Order("김지영");

            OrderItem item = new OrderItem("진라면", 1);

            // 자식에게 부모가 누구인지를 설정 
            item.setOrder(order);
                      
            // ❌ order.getOrderItems().add(item); 부모한테 자식추가를 안하고 있음
            // 그래서 이렇게 편의 메서드를 작성해야한다!!!
            // order.addItem(item);
            
            
            em.persist(order); // 자식이 없기 때문에 Cascade를 적용할 대상이 없음  
            em.persist(item); // 그래서 자식 엔티티 클래스를 따로 persist를 함
            				  // 이러면 객체간의 그래프가 불완전함
            em.flush();

            // 🎯 객체 그래프 출력: 비어있음 확인
            System.out.println("📦 Order 객체 내의 orderItems 크기: " + order.getOrderItems().size()); // 0
            for (OrderItem oi : order.getOrderItems()) {
                System.out.println("   ↳ 자식 아이템: " + oi);
            }

            tx.commit();
        } finally {
            em.close();
        }
    }


    private static void checkOrderLazyFetching() {
        EntityManager em = emf.createEntityManager();

        try {
            System.out.println("\n=== 1️⃣ OrderItem 객체만 조회 ===");
            OrderItem item = em.find(OrderItem.class, 1L); // Order는 아직 로딩되지 않음
            System.out.println("🛒 item.getProduct() = " + item.getProduct());

            System.out.println("\n=== 2️⃣ item.getOrder() 호출 전 LAZY 상태 확인 ===");
            PersistenceUnitUtil util = emf.getPersistenceUnitUtil();
            boolean isOrderLoaded = util.isLoaded(item, "order"); // 1차 캐시에 엔티티 클래스 인스턴스가 있는지 확인하는 메서드
            System.out.println("👀 isLoaded(item, \"order\"): " + isOrderLoaded); // false expected

            System.out.println("\n=== 3️⃣ item.getOrder() 호출 (지연 로딩 발생 지점) ===");
            Order order = item.getOrder(); // 여기서 SELECT 발생
            System.out.println("📦 order.getCustomer() = " + order.getCustomer());

            System.out.println("\n=== 4️⃣ 호출 후 LAZY 초기화 여부 재확인 ===");
            boolean isOrderLoadedAfter = util.isLoaded(item, "order");
            System.out.println("✅ isLoaded(item, \"order\") after access: " + isOrderLoadedAfter); // true expected

        } finally {
            em.close();
        }
    }

    public static List<Long> prepareOrderItemsForPrint() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        List<Long> orderItemIds = new ArrayList<>();
        
        try {
            tx.begin();

            Order order1 = new Order("개발팀");
            Order order2 = new Order("디자인팀");

            OrderItem item1 = new OrderItem("노트북", 1);
            OrderItem item2 = new OrderItem("모니터", 2);
            OrderItem item3 = new OrderItem("키보드", 3);
            OrderItem item4 = new OrderItem("태블릿", 1);

            // 연관관계 설정 (편의 메서드 사용)
            order1.addItem(item1);
            order1.addItem(item2);
           
            order2.addItem(item3);
            order2.addItem(item4);

            // Cascade 설정 덕분에 Order만 persist해도 OrderItem들도 자동 저장됨
            em.persist(order1);
            em.persist(order2);

            tx.commit();

            // 트랜잭션 종료 후 ID를 추출
            orderItemIds.add(item1.getId());
            orderItemIds.add(item2.getId());
            orderItemIds.add(item3.getId());
            orderItemIds.add(item4.getId());

        } finally {
            em.close();
        }

        return orderItemIds;
    }

    // JPA는 양방향 연관관계를 자동으로 역추적하지 않음
    public static void printTeamNamesForMemberIds(List<Long> orderItemIds) {

    	System.out.println("printTeamNamesForMemberIds");
    	EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
	
//            Order gOrder = em.find(Order.class, 1L);
//            gOrder.getId();
            
	        List<OrderItem> orderItems = new ArrayList<>();
	        List<Order> orders = new ArrayList<>();
	        
	        for (Long orderItemId : orderItemIds) {
	        	// 자식[OrderItem]측에서 부모[Order]를 찾는 방법
	        	OrderItem orderItem = em.find(OrderItem.class, orderItemId.longValue()); // LEFT Outer Join!!!
	            System.out.println("got Member Entity Class Object");
	            orderItems.add(orderItem);
	            /////////////////////////////////////
	            Long orderId = orderItem.getOrder().getId(); 
	            
	            
	            // orderItem은 4개인데 중복된 애들이 호출되서 추가될수있는데
	            // 중복을 방지하기 위한 메서드
	            boolean isAlready = false;
	            int isAlreadyId = -1;
	            for (Order got : orders) {
	            	if (orderId == got.getId()) {
	            		isAlready = true;
	            		isAlreadyId = orders.indexOf(got);
	            		break;
	            	} 
	            }
	            
	            if (!isAlready) {
	            	Order order = em.find(Order.class, orderId.longValue());
		            //order.addItem(orderItem); // 이미 order에는 orderItems에 있음
		            
		            orders.add(order);
	            } else {
	            	Order order = orders.get(isAlreadyId);
	            	//order.addItem(orderItem);
	            }
	        }
	
	        System.out.println("***************************************************");
	
	        
	        for (Order order : orders) {
	            System.out.printf("팀 이름:%s", order.getCustomer());
	            System.out.println("\n");
	
	            ////////////////////////////////////////////////////////
	            // 이미 1차 캐시에 OrderItem 엔티티 클래스 인스턴스가 있는데도 Select 쿼리 나감.
	            // :1차 캐시는 엔티티 단위로만 존재할 뿐, 객체 그래프까지 자동 연결하지는 않음
	            //  즉, JPA는 양방향 연관관계를 자동으로 역추적하지 않습니다
	            // ! 그러므로 다음과 같은 코드 사용하지 마시요 !
	            for (OrderItem orderItem : order.getOrderItems()) {
	                System.out.printf("          멤버 ID:%d, 멤버 이름:%S", orderItem.getId(), orderItem.getProduct());
	                System.out.println("\n");
	            }
	            /////////////////////////////////////////////////
	        }
	
	        tx.commit();
        } catch (Exception e) {
        	
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

            /*
            select
		        orderitems0_.order_id as order_id4_0_0_,
		        orderitems0_.id as id1_0_0_,
		        orderitems0_.id as id1_0_1_,
		        orderitems0_.order_id as order_id4_0_1_,
		        orderitems0_.product as product2_0_1_,
		        orderitems0_.quantity as quantity3_0_1_ 
		    from
		        OrderItem orderitems0_ 
		    where
 	  			orderitems0_.order_id=? // 사실 order id가 하나(1L) 밖에 없음
             */   
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

            System.out.println("★ OrderItem : " + item.toString());
            // 기존 연관관계 제거
            item.getOrder().removeItem(item);
            
            OrderItem newItem = 
            		new OrderItem(item.getProduct(), item.getQuantity());

            // 새 연관관계 설정
            newOrder.addItem(newItem);  // 연관관계 편의 메서드로 양방향 유지

            em.persist(newOrder);
            
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
    
    private static void createOrderWithItems() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Order order = new Order("김지철");
            order.addItem(new OrderItem("키보드", 1));
            order.addItem(new OrderItem("마우스", 2));
            order.addItem(new OrderItem("모니터", 1));

            em.persist(order);  // cascade: OrderItem도 함께 저장

            tx.commit();
        } finally {
            em.close();
        }
    }
    
    // 변경 감지 테스트 (수량 변경)
    private static void updateOrderItems() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Order order = em.find(Order.class, 1L);
            for (OrderItem item : order.getOrderItems()) {
                if (item.getProduct().equals("마우스")) {
                    item.setQuantity(5); // 변경 감지, update함
                }
            }

            // 엔티티 메니저가 스냅샷으로 엔티티클래스 인스턴스의 변경을 확인하고
            // update함
            
            tx.commit();
        } finally {
            em.close();
        }
    }

    
    // 리스트 초기화 후 재등록 테스트
    private static void removeAndReAdd() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            System.out.println("+removeAndReAdd");

            Order order = em.find(Order.class, 1L);

            // 기존 아이템 전부 제거 (orphanRemoval)
            for (OrderItem item : new ArrayList<>(order.getOrderItems())) {
                order.removeItem(item);
            }

            // 새로운 아이템 추가
            order.addItem(new OrderItem("SSD", 1));
            order.addItem(new OrderItem("RAM", 2));

            tx.commit();
            System.out.println("-removeAndReAdd");
        } finally {
            em.close();
        }
    }
    
    public static void initTestData() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Order order = new Order("김지철");
            order.addItem(new OrderItem("RAM", 2));
            order.addItem(new OrderItem("SSD", 1));
            order.addItem(new OrderItem("HDD", 3));
            order.addItem(new OrderItem("Mouse", 1));

            em.persist(order); // CascadeType.ALL + 편의 메서드로 아이템까지 자동 저장

            tx.commit();

        } finally {
            em.close();
        }
    }
    
    private static void printItem() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Order order = em.find(Order.class, 1L);
            for (OrderItem item : order.getOrderItems()) {
                System.out.println("Item:" + item);

            }
            tx.commit();
        } finally {
            em.close();
        }
    }
    
    private static void selectiveUpdateItem() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Order order = em.find(Order.class, 1L);
            // orderItems 컬렉션
            order.getOrderItems().stream()
            		// 필터링 : OrderItem의 필드 product의 이름이 'HDD'인 엘리먼트만,
                    .filter(item -> item.getProduct().equals("HDD"))
                    // 필터링 결과중에, 가장 작은 인덱스 값을 가지는 엘리먼트만
                    .findFirst()
                    // 만약 이러한 조건에 부합하는 엘리먼트가 있다면
                    // 다음 람다식을 적용함
                    .ifPresent(item -> item.setQuantity(item.getQuantity() + 1)); // HDD가 있으면 이 메서드 실행, 없으면 실행 안함
            		
            
            tx.commit();
            System.out.println("'HDD' 수량 +1 증가 완료");
        } finally {
            em.close();
        }
    }

    // 수량이 1개 이하인 상품 제거
    private static void conditionalDeleteItem() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Order order = em.find(Order.class, 1L);
            List<OrderItem> toRemove = order.getOrderItems().stream()
                    .filter(item -> item.getQuantity() <= 1)
                    // filter 연산 결과에 부합하는 스트림 엘리먼트들을 
                    // 리스트 컬렉션으로 제공함.
                    .toList();

            toRemove.forEach(order::removeItem); // orphanRemoval로 DB에서도 삭제됨

            tx.commit();
            System.out.println("수량 1 이하인 상품 삭제 완료");
        } finally {
            em.close();
        }
    }

    // 이미 등록된 아이템을 유지하고 새로 추가
    private static void appendNewItems() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Order order = em.find(Order.class, 1L);

            // 중복 검사 없이 새 상품 추가
            order.addItem(new OrderItem("그래픽카드", 1));
            order.addItem(new OrderItem("CPU", 1));

            tx.commit();
            System.out.println(" '그래픽카드'와 'CPU' 추가 완료");
        } finally {
            em.close();
        }
    }
    
    public static void addMoreOrderItemsToExistingOrders() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 기존 Order 엔티티 조회 (이름으로 조회 또는 ID 기반으로 해도 됨)
            TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o WHERE o.customer = :name", Order.class);
           
            Order devTeam = query.setParameter("name", "개발팀").getSingleResult();
            
            Order designTeam = em.createQuery("SELECT o FROM Order o WHERE o.customer = :name", Order.class)
                                  .setParameter("name", "디자인팀")
                                  .getSingleResult();

            // 새로운 OrderItem 추가
            OrderItem item5 = new OrderItem("외장하드", 1);
            OrderItem item6 = new OrderItem("HDMI 케이블", 5);
            OrderItem item7 = new OrderItem("그래픽 태블릿", 1);
            OrderItem item8 = new OrderItem("펜촉 세트", 10);

            // 연관관계 설정 (편의 메서드 사용)
            devTeam.addItem(item5);
            devTeam.addItem(item6);
            designTeam.addItem(item7);
            designTeam.addItem(item8);

            // cascade 설정으로 인해 Order만 merge/persist하면 자식도 반영됨
            em.persist(devTeam);
            em.persist(designTeam);

            tx.commit();
            System.out.println("✅ 기존 Order에 새로운 OrderItem 추가 완료");

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    
    public static void verifyOrderItemsInDatabase() {
        EntityManager em = emf.createEntityManager();
        
        try {
            List<Order> orders = em.createQuery("SELECT o FROM Order o", Order.class)
                                   .getResultList();

            System.out.println("📦 전체 Order 및 그에 속한 OrderItem 목록 출력");
            for (Order order : orders) {
                System.out.println("--------------------------------------------------");
                System.out.printf("🧑 팀 이름: %s (Order ID: %d)%n", order.getCustomer(), order.getId());

                List<OrderItem> items = order.getOrderItems(); // LAZY일 경우 이 시점에 SELECT 발생
                System.out.printf("📦 포함된 OrderItem 수: %d%n", items.size());

                for (OrderItem item : items) {
                    System.out.printf("   - ID: %d, 상품명: %s, 수량: %d%n",
                            item.getId(), item.getProduct(), item.getQuantity());
                }
            }

        } finally {
            em.close();
        }
    }

    public static void deleteOrderAndCascadeItems(String customerName) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 삭제 대상 Order 조회
            Order order = em.createQuery("SELECT o FROM Order o WHERE o.customer = :name", Order.class)
                            .setParameter("name", customerName)
                            .getSingleResult();

            Long orderId = order.getId();

            // 로그: 삭제 전 상태 출력
            System.out.printf("🗑 삭제할 Order: %s (ID: %d), 자식 개수: %d%n", order.getCustomer(), orderId, order.getOrderItems().size());

            // 부모 삭제 → 자식도 cascade + orphanRemoval에 의해 자동 삭제됨
            em.remove(order);

            tx.commit();
            System.out.println("✅ Order 및 관련된 OrderItem 삭제 완료");

            // 삭제 후 확인
            em = emf.createEntityManager(); // 새 EntityManager로 쿼리 실행
            Long count = em.createQuery("SELECT COUNT(o) FROM OrderItem o WHERE o.order.id = :orderId", Long.class)
                           .setParameter("orderId", orderId)
                           .getSingleResult();

            System.out.printf("🔍 삭제 이후, 해당 Order에 속한 OrderItem 개수: %d%n", count);

        } catch (NoResultException e) {
            System.out.println("해당 이름의 Order가 존재하지 않습니다.");
        } finally {
            em.close();
        }
    }



}
