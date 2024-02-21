package com.example.demo.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SchoolBillingSummaryResponse {
    private BigDecimal total;
    private List<ParentBillingSummaryResponse> parentsBilling;
}
