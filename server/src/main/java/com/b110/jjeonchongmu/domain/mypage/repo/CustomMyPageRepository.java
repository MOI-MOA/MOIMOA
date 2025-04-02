package com.b110.jjeonchongmu.domain.mypage.repo;

import com.b110.jjeonchongmu.domain.mypage.dto.auto.GatheringProjection;
import java.util.List;

public interface CustomMyPageRepository {
	List<GatheringProjection>  getAutoPaymentDtos(Long userId);
}
