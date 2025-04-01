package com.b110.jjeonchongmu.domain.gathering.dto;

import com.b110.jjeonchongmu.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GatheringDetailManagerDTO {
    private String name;
    private String avatar = "/placeholder.svg?height=40&width=40";

    public GatheringDetailManagerDTO(User user) {
        this.name = user.getName();
    }
}
