package com.b110.jjeonchongmu.domain.group.entity;
import com.b110.jjeonchongmu.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "group_member")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_member_id", nullable = false)
    private Long groupMemberId;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "group_attend_count")
    private Integer groupAttendCount;

    @Column(name = "group_member_account_balance")
    private Integer groupMemberAccountBalance;

    @Column(name = "group_member_account_deposit")
    private Integer groupMemberAccountDeposit;

    @Column(name = "group_payment_status")
    private Boolean groupPaymentStatus;
}