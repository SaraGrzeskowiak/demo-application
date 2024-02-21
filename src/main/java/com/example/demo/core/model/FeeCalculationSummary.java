package com.example.demo.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeCalculationSummary {
    private Duration timeAtSchool;
    private BigDecimal totalFee;
}
