package com.b110.jjeonchongmu.domain.gathering.entity;

//추가
public enum GatheringMemberStatus {
    PENDING("대기중"),    // 초대 신청 상태
    ACTIVE("활성"),      // 정식 멤버
    REJECTED("거절");    // 거절된 상태

    private final String description;

    GatheringMemberStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 