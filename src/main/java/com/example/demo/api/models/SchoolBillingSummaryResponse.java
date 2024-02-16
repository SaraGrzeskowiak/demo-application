package com.example.demo.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class SchoolBillingSummaryResponse {
    private BigDecimal total;
    private List<ParentBillingSummaryResponse> parentsBilling;
}
