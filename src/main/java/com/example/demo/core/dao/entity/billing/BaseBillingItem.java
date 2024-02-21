package com.example.demo.core.dao.entity.billing;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseBillingItem {
    @Id
    protected int id;
    protected BigDecimal hourPrice;
    protected int childId;
    protected LocalDateTime entryDate;
    protected LocalDateTime exitDate;
}
