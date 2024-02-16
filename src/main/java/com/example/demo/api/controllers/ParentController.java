package com.example.demo.api.controllers;

import com.example.demo.api.models.ParentBillingSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/parent")
@RequiredArgsConstructor
public class ParentController {

    @GetMapping(path = "/{parentId}/{billingDate}")
    public ParentBillingSummaryResponse getBillingSummary(
            @PathVariable int parentId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM") LocalDate billingDate
    ) {
        throw new NotImplementedException();
    }
}
