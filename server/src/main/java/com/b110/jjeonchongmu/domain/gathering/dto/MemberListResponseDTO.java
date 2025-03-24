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
public class MemberListResponseDTO {
    private Integer memberCount;
    private ManagerDTO manager;
    private List<MemberDTO> members;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ManagerDTO {
        private String name;
        private String email;
        private LocalDateTime createdAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberDTO {
        private String name;
        private String email;
        private LocalDateTime createdAt;
    }
}
