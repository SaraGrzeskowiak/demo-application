package com.example.demo.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;

@Data
@NoArgsConstructor
public class ChildBilling {
    private BigDecimal total;
    private ParsonNames child;
    private Duration timeAtSchool;
}
