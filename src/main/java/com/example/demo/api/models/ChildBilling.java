package com.example.demo.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChildBilling {
    private BigDecimal childFee;
    private ParsonNames child;
    private Duration timeAtSchool;
}
