package com.b110.jjeonchongmu.domain.mypage.repo;

import com.b110.jjeonchongmu.domain.account.entity.QGatheringAccount;
import com.b110.jjeonchongmu.domain.gathering.entity.QGathering;
import com.b110.jjeonchongmu.domain.gathering.entity.QGatheringMember;
import com.b110.jjeonchongmu.domain.mypage.dto.auto.GatheringProjection;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class CustomMyPageRepositoryImpl implements CustomMyPageRepository {

	QGathering qGathering = QGathering.gathering;
	QGatheringAccount qGatheringAccount = QGatheringAccount.gatheringAccount;
	QGatheringMember qGatheringMember = QGatheringMember.gatheringMember;

	private final JPAQueryFactory jpaQueryFactory;

	public CustomMyPageRepositoryImpl(EntityManager em) {
		this.jpaQueryFactory = new JPAQueryFactory(em);
	}

	/*
		private Long gatheringId;
	private String gatheringName;
	private Long depositDate;
	private Long basicFee;
	private Long gatheringDeposit;
	 */
	@Override
	public List<GatheringProjection> getAutoPaymentDtos(Long userId) {
		return jpaQueryFactory.select(Projections.constructor(GatheringProjection.class,
								qGathering.gatheringId,
								qGathering.gatheringName,
								qGathering.depositDate,
								qGathering.basicFee,
								qGathering.gatheringDeposit,
								qGatheringAccount.accountNo,
								qGatheringMember.gatheringMemberAccountBalance,
								qGatheringMember.gatheringMemberAccountDeposit,
								qGatheringMember.gatheringPaymentStatus))
				.from(qGathering)
				.join(qGatheringMember).on(qGathering.gatheringId.eq(qGatheringMember.gathering.gatheringId)
					.and(qGatheringMember.gatheringMemberUser.userId.eq(userId)))
				.leftJoin(qGatheringAccount).on(qGathering.gatheringId.eq(qGatheringAccount.gathering.gatheringId))
//				.leftJoin(qGatheringMember).on(qGatheringMember.gathering.gatheringId.eq(qGathering.gatheringId))
				.fetch();
	}
}
