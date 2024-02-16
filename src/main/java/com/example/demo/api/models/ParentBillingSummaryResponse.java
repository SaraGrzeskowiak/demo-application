package com.example.demo.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class ParentBillingSummaryResponse {
    private BigDecimal total;
    private ParsonNames parent;
    private List<ChildBilling> childrenBillingSummary;
}
