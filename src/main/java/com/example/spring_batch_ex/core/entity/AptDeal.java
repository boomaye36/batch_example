package com.example.spring_batch_ex.core.entity;

import com.example.spring_batch_ex.dto.AptDealDto;
import com.sun.istack.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "apt_deal")
@EntityListeners(AuditingEntityListener.class)
public class AptDeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aptDealId;

    @ManyToOne
    @JoinColumn(name = "apt_id")
    private Apt apt;

    @Column(nullable = false)
    private Double exclusiveArea;

    @Column(nullable = false)
    private LocalDate dealDate;

    @Column(nullable = false)
    private Long dealAmount;

    @Column(nullable = false)
    private int floor;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean dealCanceled;

    @Column
    private LocalDate dealCanceledDate;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static AptDeal of(AptDealDto aptDealDto, Apt apt){
        AptDeal aptDeal = new AptDeal();
        aptDeal.setApt(apt);
        aptDeal.setExclusiveArea(aptDealDto.getExclusiveArea());
        aptDeal.setDealDate(aptDealDto.getDealDate());
        aptDeal.setDealAmount(aptDealDto.getDealAmount());
        aptDeal.setFloor(aptDealDto.getFloor());
        aptDeal.setDealCanceled(aptDealDto.isDealCanceled());
        aptDeal.setDealCanceledDate(aptDealDto.getDealCanceledDate());
        return aptDeal;
    }
}
