package com.pppjpademo.jpa;

import javax.persistence.*;

import lombok.*;

@Getter
//@Setter
@NoArgsConstructor
@SequenceGenerator(
	    name = "userprofile_seq_generator",
	    sequenceName = "userprofile_seq", // DB 시퀀스 이름
	    initialValue = 1,
	    allocationSize = 100
	)
@Entity
public class UserProfile {
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,
			generator="userprofile_seq_generator")
	private Long id;

	
    private String bio;
    
    @OneToOne
	@JoinColumn(name = "user_id", unique = true)
    private User user;
    
    public UserProfile(String bio) {
    	this.bio = bio;
    }
    
    public void setUser(User user) {
    	this.user = user;
    }

}