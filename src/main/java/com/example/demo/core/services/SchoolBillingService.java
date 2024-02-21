package com.example.demo.core.services;

import com.example.demo.api.models.SchoolBillingSummaryResponse;

import java.time.LocalDate;

public interface SchoolBillingService {
    SchoolBillingSummaryResponse getBilling(int schoolId, LocalDate billingDate);
}
