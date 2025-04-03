package com.b110.jjeonchongmu.domain.gathering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberManageResponseDTO {
    private List<InviteMemberDTO> inviteList;
    private ManagerDTO manager;
    private List<MemberDTO> memberList;
    private Boolean isManager;
    private Long myDeposit;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InviteMemberDTO {
        private Long id;
        private String name;
        private String email;
        private LocalDateTime createdAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ManagerDTO {
        private String name;
        private String email;
        private LocalDateTime createdAt;
        private Long balance;
        private Boolean gatheringPaymentStatus;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberDTO {
        private String name;
        private String email;
        private LocalDateTime createdAt;
        private Long balance;
        private Boolean gatheringPaymentStatus;
    }
}
