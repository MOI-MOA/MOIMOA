package com.b110.jjeonchongmu.domain.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "group_account")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_account_id", nullable = false)
    private Long groupAccountId;

    @ManyToOne
    @JoinColumn(name = "group_account_name_id", nullable = false)
    private Group group;

    @Column(name = "group_account_no", nullable = true)
    private Long groupAccountNo;

    @Column(name = "group_account_balance", nullable = false)
    private Integer groupAccountBalance = 0;

    @Column(name = "group_account_pw", nullable = false)
    private Integer groupAccountPw;
}