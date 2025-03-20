package com.b110.jjeonchongmu.domain.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auto_payment")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AutoPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auto_payment_id", nullable = false)
    private Long autoPaymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_account_id", nullable = false)
    private PersonalAccount personalAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gahtering_account_id", nullable = false)
    private GatheringAccount gatheringAccount;

    @Column(name = "auto_payment_amount", nullable = true)
    private Integer autoPaymentAmount;

    @Column(name = "auto_payment_date", nullable = true)
    private LocalDateTime autoPaymentDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;
}