package com.example.demo.core.services;

import com.example.demo.api.models.ParentBillingSummaryResponse;

import java.time.LocalDate;

public interface ParentBillingService {
    ParentBillingSummaryResponse getBilling(int parentId, LocalDate billingDate);
}
