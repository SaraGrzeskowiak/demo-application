package com.example.demo.core.services;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.core.model.FeeCalculationSummary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FeeCalculationServiceImplTest extends AbstractIntegrationTest {

    @Autowired
    private FeeCalculationService service;

    @Test
    void shouldBeBilledForEveryStartedHourNotInFreeHours() {
        // Arrange
        var attendance = Pair.of(
                LocalDateTime.of(2024, 2, 1, 6, 59, 0, 0),
                LocalDateTime.of(2024, 2, 1, 12, 1, 0, 0)
        );

        // Act
        var fee = service.calculateFee(BigDecimal.TEN, List.of(attendance));

        // Assert
        var expected = new FeeCalculationSummary(Duration.between(attendance.getFirst(), attendance.getSecond()), BigDecimal.valueOf(20));
        assertThat(fee).isEqualTo(expected);
    }

    @Test
    void shouldNotBeBilledTwiceForSameHour() {
        // Arrange
        var attendance1 = Pair.of(
                LocalDateTime.of(2024, 2, 1, 12, 0, 0, 0),
                LocalDateTime.of(2024, 2, 1, 12, 10, 0, 0)
        );

        var attendance2 = Pair.of(
                LocalDateTime.of(2024, 2, 1, 12, 15, 0, 0),
                LocalDateTime.of(2024, 2, 1, 12, 30, 0, 0)
        );

        // Act
        var fee = service.calculateFee(BigDecimal.TEN, List.of(attendance1, attendance2));

        // Assert
        var expected = new FeeCalculationSummary(Duration.ofMinutes(25), BigDecimal.TEN);
        assertThat(fee).isEqualTo(expected);
    }

    @Test
    void shouldCalculateCorrectlyIfEntryAndExitAreNotInTheSameDay() {
        // Arrange
        var attendance = Pair.of(
                LocalDateTime.of(2024, 2, 1, 6, 59, 0, 0),
                LocalDateTime.of(2024, 2, 2, 12, 1, 0, 0)
        );

        // Act
        var fee = service.calculateFee(BigDecimal.TEN, List.of(attendance));

        // Assert
        var expected = new FeeCalculationSummary(Duration.between(attendance.getFirst(), attendance.getSecond()), BigDecimal.valueOf(210));
        assertThat(fee).isEqualTo(expected);
    }

    @Test
    void shouldCalculateCorrectlyWhenAttendanceIsEmpty() {
        // Act
        var fee = service.calculateFee(BigDecimal.TEN, List.of());

        assertThat(fee).isEqualTo(new FeeCalculationSummary(Duration.ZERO, BigDecimal.ZERO));
    }
}