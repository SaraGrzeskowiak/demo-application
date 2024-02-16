package com.example.demo.api.controllers;

import com.example.demo.api.models.SchoolBillingSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/school")
@RequiredArgsConstructor
public class SchoolController {

    @GetMapping(path = "/{schoolId}/{billingDate}")
    public SchoolBillingSummaryResponse getBillingSummary(
            @PathVariable int schoolId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM") LocalDate billingDate
    ) {
        throw new NotImplementedException();
    }
}
