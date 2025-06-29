package com.pppjpademo.jpa;

import javax.persistence.*;

import lombok.*;

//@Getter
//@Setter
//@NoArgsConstructor
@Entity
@SequenceGenerator(
	    name = "user_seq_generator",
	    sequenceName = "user_seq", // DB 시퀀스 이름
	    initialValue = 1,
	    allocationSize = 100
	)
@Table(name="USERD") // h2에서 User는 예약어!!! 그러므로 테이블 이름을 User라하면 안됨!!!
public class User {
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,
		generator="user_seq_generator")
	private Long id;

    private String username;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile profile;
    
    public User() {}
    
    public User(String username) {
    	this.username = username;
    }
    
    public void setUserProfile(UserProfile profile) {
    	this.profile = profile;
        if (profile.getUser() != this) {
            profile.setUser(this);
        }
    }
    
    public String getUserName() {
    	return this.username;
    }
    
    public UserProfile getUserProfile() {
    	return this.profile;
    }
    
     

}