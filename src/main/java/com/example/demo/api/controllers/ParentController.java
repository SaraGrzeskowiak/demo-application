package com.example.demo.api.controllers;

import com.example.demo.api.models.ParentBillingSummaryResponse;
import com.example.demo.core.services.ParentBillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/v1/parent")
@RequiredArgsConstructor
public class ParentController {
    private final ParentBillingService billingService;

    @GetMapping(path = "/{parentId}/{billingDate}")
    public ParentBillingSummaryResponse getBillingSummary(
            @PathVariable int parentId,
            @PathVariable YearMonth billingDate
            ) {
        return billingService.getBilling(parentId, billingDate.atDay(1));
    }
}
