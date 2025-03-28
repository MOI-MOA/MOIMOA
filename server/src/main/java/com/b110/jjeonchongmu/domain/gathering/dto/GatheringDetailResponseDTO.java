package com.b110.jjeonchongmu.domain.gathering.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GatheringDetailResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Integer totalMembers;
    private Long monthlyFee;
    private Boolean isManager;
    private GatheringDetailManagerDTO manager;
    private GatheringDetailAccountDTO accounts;
    private List<GatheringDetailSchedules> schedules;
}
