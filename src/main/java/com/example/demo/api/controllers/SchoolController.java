package com.example.demo.api.controllers;

import com.example.demo.api.models.SchoolBillingSummaryResponse;
import com.example.demo.core.services.SchoolBillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/v1/school")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolBillingService schoolBillingService;

    @GetMapping(path = "/{schoolId}/{billingDate}")
    public SchoolBillingSummaryResponse getBillingSummary(
            @PathVariable int schoolId,
            @PathVariable YearMonth billingDate
    ) {
        return schoolBillingService.getBilling(schoolId, billingDate.atDay(1));
    }
}
