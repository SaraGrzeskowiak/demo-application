package com.example.demo.core.services;

import com.example.demo.core.domain.FeeCalculationSummary;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface FeeCalculationService {
    FeeCalculationSummary calculateFee(BigDecimal hourPrice, List<Pair<LocalDateTime, LocalDateTime>> attendances);
}
