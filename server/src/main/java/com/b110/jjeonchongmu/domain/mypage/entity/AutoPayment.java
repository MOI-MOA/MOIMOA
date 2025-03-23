package com.b110.jjeonchongmu.domain.mypage.entity;

import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutoPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int amount;
    private LocalDateTime paymentDate;
    private boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id")
    private Gathering gathering;

    @Builder
    public AutoPayment(int amount, LocalDateTime paymentDate, boolean isActive, Gathering gathering) {
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.isActive = isActive;
        this.gathering = gathering;
    }
} 