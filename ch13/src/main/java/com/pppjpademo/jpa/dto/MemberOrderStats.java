package com.pppjpademo.jpa.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class MemberOrderStats {
    private String memberName;
    private long totalQuantity;

    public MemberOrderStats(String memberName, long totalQuantity) {
        this.memberName = memberName;
        this.totalQuantity = totalQuantity;
    }
    
    public static MemberOrderStats of(String name, int totalQuantity) {
        MemberOrderStats dto = new MemberOrderStats();
        dto.setMemberName(name);
        dto.setTotalQuantity(totalQuantity);
        return dto;
    }


    public String toString() {
        return memberName + " 총 주문수량: " + totalQuantity;
    }
}