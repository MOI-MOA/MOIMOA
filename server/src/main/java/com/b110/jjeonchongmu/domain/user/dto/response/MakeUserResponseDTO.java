package com.b110.jjeonchongmu.domain.user.dto.response;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MakeUserResponseDTO {
	private String userId; // 이게 email
	private String userName;
	private String institutionCode;
	private String userKey;
	private ZonedDateTime created;
	private ZonedDateTime modified;
}
