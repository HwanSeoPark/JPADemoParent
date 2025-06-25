package com.pppjpademo.jpa;

import javax.persistence.*;
import java.util.List;

// 양방향
public class ManyToManyBiDirectionalTest {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

    public static void main(String[] args) {
        saveTest();             // 연관관계 저장 테스트
        queryTest();            // 양방향 탐색 테스트
        deleteRelationTest();   // 관계 제거 및 삭제 테스트

        emf.close();
    }

    // ✅ 1. 저장 테스트
    public static void saveTest() {
        System.out.println("\n🟢 saveTest 시작");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Student student1 = new Student();
            student1.setName("홍길동");

            Student student2 = new Student();
            student2.setName("김철수");

            Course course1 = new Course();
            course1.setName("JPA 입문");

            Course course2 = new Course();
            course2.setName("Spring Boot 심화");

            // 연관관계 설정 (양방향)
            student1.addCourse(course1);
            student1.addCourse(course2);

            student2.addCourse(course1);

            // 저장 (양방향 ManyToMany는 주인 엔티티만 저장해도 관계가 저장됨)
            em.persist(student1);
            em.persist(student2);
            em.persist(course1);
            em.persist(course2);

            tx.commit();
            System.out.println("✅ 저장 완료");
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ✅ 2. 재조회 및 양방향 탐색 테스트
    public static void queryTest() {
        System.out.println("\n🟢 queryTest 시작");

        EntityManager em = emf.createEntityManager();

        try {
            List<Student> students = em.createQuery("select s from Student s", Student.class).getResultList();

            for (Student s : students) {
                System.out.println("👨‍🎓 학생: " + s.getName());
                for (Course c : s.getCourses()) {
                    System.out.println("   📘 수강과목: " + c.getName());
                }
            }

            List<Course> courses = em.createQuery("select c from Course c", Course.class).getResultList();

            for (Course c : courses) {
                System.out.println("📘 과목: " + c.getName());
                for (Student s : c.getStudents()) {
                    System.out.println("   👨‍🎓 수강생: " + s.getName());
                }
            }

        } finally {
            em.close();
        }
    }

    // ✅ 3. 관계 삭제 테스트
    public static void deleteRelationTest() {
        System.out.println("\n🟢 deleteRelationTest 시작");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Student student = em.createQuery("select s from Student s where s.name = '홍길동'", Student.class)
                                .getSingleResult();

            // 수강 과목 하나 제거
            Course toRemove = student.getCourses().iterator().next();

            // 양방향 연관관계 해제
            student.getCourses().remove(toRemove);
            toRemove.getStudents().remove(student);

            tx.commit();
            System.out.println("🗑️ 연관관계 해제 완료: " + toRemove.getName());
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
