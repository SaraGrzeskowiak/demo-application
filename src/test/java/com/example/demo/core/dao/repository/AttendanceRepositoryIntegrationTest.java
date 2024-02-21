package com.example.demo.core.dao.repository;

import com.example.demo.core.dao.entity.billing.ParentBillingItemEntity;
import com.example.demo.core.dao.entity.billing.SchoolBillingItemEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(value = "/sql/add_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class AttendanceRepositoryIntegrationTest {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Test
    void findAttendanceByParentIdShouldReturnItemsForAllParentKidsThatEnteredOrExitedSchoolInGivenMonth() {
        var response = attendanceRepository.findAttendanceByParentId(1, 2, 2024);

        assertThat(response).hasSize(4);
        assertThat(response).extracting(ParentBillingItemEntity::getExitDate)
                .containsOnly(
                        LocalDateTime.of(2024, 2, 15, 15, 0, 0),
                        LocalDateTime.of(2024, 2, 16, 15, 30, 0),
                        LocalDateTime.of(2024, 2, 1, 18, 0, 0),
                        LocalDateTime.of(2024, 3, 1, 18, 0, 0)
                );
        assertThat(response).extracting(ParentBillingItemEntity::getEntryDate)
                .containsOnly(
                        LocalDateTime.of(2024, 2, 15, 8, 0, 0),
                        LocalDateTime.of(2024, 2, 16, 8, 30, 0),
                        LocalDateTime.of(2024, 1, 31, 8, 0, 0),
                        LocalDateTime.of(2024, 2, 29, 8, 0, 0)
                );
    }

    @Test
    void findAttendanceBySchoolIdShouldReturnItemsForAllKidsAttendingGivenSchoolThatEnteredOrExitedSchoolInGivenMonth() {
        var response = attendanceRepository.findAttendanceBySchoolId(2, 2, 2024);

        assertThat(response).hasSize(3);
        assertThat(response).extracting(SchoolBillingItemEntity::getEntryDate)
                .containsOnly(
                        LocalDateTime.of(2024, 1, 31, 8, 0, 0),
                        LocalDateTime.of(2024, 2, 29, 8, 0, 0),
                        LocalDateTime.of(2024, 2, 15, 8, 15, 0)
                );
        assertThat(response).extracting(SchoolBillingItemEntity::getExitDate)
                .containsOnly(
                        LocalDateTime.of(2024, 2, 1, 18, 0, 0),
                        LocalDateTime.of(2024, 3, 1, 18, 0, 0),
                        LocalDateTime.of(2024, 2, 15, 15, 15, 0)
                );
    }
}
