package com.example.demo.core.dao.repository;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.core.dao.entity.BillingItemEntity;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AttendanceRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Test
    @Sql("/sql/add_test_data.sql")
    void findAttendanceShouldReturnItemsForAllParentKidsThatEnteredOrExitedSchoolInGivenMonthInAscendingOrder() {
        var response = attendanceRepository.findAttendance(1, 2, 2024);

        assertThat(response).hasSize(4);
        assertThat(response).extracting(BillingItemEntity::getExitDate)
                .containsOnly(
                        LocalDateTime.of(2024, 2, 15, 15, 0, 0),
                        LocalDateTime.of(2024, 2, 16, 15, 30, 0),
                        LocalDateTime.of(2024, 2, 1, 18, 0, 0),
                        LocalDateTime.of(2024, 3, 1, 18, 0, 0)
                );
        assertThat(response).extracting(BillingItemEntity::getEntryDate)
                .containsOnly(
                        LocalDateTime.of(2024, 2, 15, 8, 0, 0),
                        LocalDateTime.of(2024, 2, 16, 8, 30, 0),
                        LocalDateTime.of(2024, 1, 31, 8, 0, 0),
                        LocalDateTime.of(2024, 2, 29, 8, 0, 0)
                );
    }
}
