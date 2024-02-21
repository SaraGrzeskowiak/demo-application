package com.example.demo.core.services;

import com.example.demo.api.models.ParentBillingSummaryResponse;
import com.example.demo.api.models.SchoolBillingSummaryResponse;
import com.example.demo.core.dao.entity.ChildEntity;
import com.example.demo.core.dao.entity.ParentEntity;
import com.example.demo.core.dao.entity.billing.ParentBillingItemEntity;
import com.example.demo.core.dao.entity.billing.SchoolBillingItemEntity;
import com.example.demo.core.dao.repository.AttendanceRepository;
import com.example.demo.core.dao.repository.ChildRepository;
import com.example.demo.core.dao.repository.ParentRepository;
import com.example.demo.core.dao.repository.SchoolRepository;
import com.example.demo.core.domain.FeeCalculationSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchoolBillingServiceImplTest {
    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private SchoolRepository schoolRepository;

    @Mock
    private ParentRepository parentRepository;

    @Mock
    private ChildRepository childRepository;

    @Mock
    private FeeCalculationService feeCalculationService;

    @InjectMocks
    private SchoolBillingServiceImpl schoolBillingService;

    @Captor
    ArgumentCaptor<List<Pair<LocalDateTime, LocalDateTime>>> billingItemsCaptor;

    @Test
    void getBillingShouldSetAllResponseFields() {
        // Arrange
        var schoolId = 1;
        var billingDate = LocalDate.of(2022, 1, 1);
        var parentFee = BigDecimal.valueOf(100);
        var parentId = 1;
        BigDecimal hourPrice = BigDecimal.TEN;
        String parentFirstname = "John";
        String parentLastname = "Doe";
        String childFirstname = "Jane";
        String childLastname = "Doe";
        LocalDateTime entryDate = LocalDateTime.of(2024, 1, 31, 8, 0);
        LocalDateTime exitDate = LocalDateTime.of(2024, 3, 1, 15, 0);

        var schoolBillingItemEntity = new SchoolBillingItemEntity();
        schoolBillingItemEntity.setChildId(1);
        schoolBillingItemEntity.setParentId(1);
        schoolBillingItemEntity.setHourPrice(hourPrice);
        schoolBillingItemEntity.setId(1);
        schoolBillingItemEntity.setEntryDate(entryDate);
        schoolBillingItemEntity.setExitDate(exitDate);
        List<SchoolBillingItemEntity> billingItems = Collections.singletonList(schoolBillingItemEntity);

        var parent = ParentEntity.builder()
                .id(parentId)
                .firstname(parentFirstname)
                .lastname(parentLastname)
                .build();

        var child = ChildEntity.builder()
                .id(1)
                .parent(parent)
                .firstname(childFirstname)
                .lastname(childLastname)
                .build();

        when(schoolRepository.existsById(schoolId)).thenReturn(true);
        when(attendanceRepository.findAttendanceBySchoolId(schoolId, billingDate.getMonthValue(), billingDate.getYear())).thenReturn(billingItems);
        when(parentRepository.findAllById(anySet())).thenReturn(Collections.singletonList(parent));
        when(childRepository.findAllById(anySet())).thenReturn(List.of(child));
        when(feeCalculationService.calculateFee(any(), any())).thenReturn(new FeeCalculationSummary(Duration.ofHours(7), parentFee));

        // Act
        SchoolBillingSummaryResponse response = schoolBillingService.getBilling(schoolId, billingDate);

        // Arrange
        assertEquals(parentFee, response.getTotal());
        assertEquals(1, response.getParentsBilling().size());
        ParentBillingSummaryResponse parentSummary = response.getParentsBilling().get(0);
        assertEquals(parentFirstname, parentSummary.getParent().getFirstname());
        assertEquals(parentLastname, parentSummary.getParent().getLastname());
        assertEquals(parentFee, parentSummary.getParentFee());
    }

    @Test
    void getBillingShouldCalculateBillingForEveryParent() {
        // Arrange
        var schoolId = 1;
        var billingDate = LocalDate.of(2024, 2, 1);
        var parentFee = BigDecimal.valueOf(100);

        List<SchoolBillingItemEntity> billingItems = getBillingEntities();

        var parent1 = ParentEntity.builder()
                .id(1)
                .firstname("parentFirsname 1")
                .lastname("parentLastname 1")
                .build();
        var parent2 = ParentEntity.builder()
                .id(2)
                .firstname("parentFirsname 2")
                .lastname("parentLastname 2")
                .build();

        var child1 = ChildEntity.builder()
                .id(1)
                .parent(parent1)
                .firstname("child firsname 1")
                .lastname("child lastname 2")
                .build();
        var child2 = ChildEntity.builder()
                .id(2)
                .parent(parent2)
                .firstname("child firstname 2")
                .lastname("child lastname 2")
                .build();

        when(schoolRepository.existsById(schoolId)).thenReturn(true);
        when(attendanceRepository.findAttendanceBySchoolId(schoolId, billingDate.getMonthValue(), billingDate.getYear())).thenReturn(billingItems);
        when(parentRepository.findAllById(anySet())).thenReturn(List.of(parent1, parent2));
        when(childRepository.findAllById(anySet())).thenReturn(List.of(child1, child2));
        when(feeCalculationService.calculateFee(any(), any())).thenReturn(new FeeCalculationSummary(Duration.ofHours(7), parentFee));

        // Act
        schoolBillingService.getBilling(schoolId, billingDate);

        // Assert
        verify(feeCalculationService, times(2)).calculateFee(any(), billingItemsCaptor.capture());
        var capturedValues = billingItemsCaptor.getAllValues();
        assertThat(capturedValues).hasSize(2);
        var expectedPairs = List.of(
                Pair.of(
                        LocalDateTime.of(2024, 2, 1, 0, 0),
                        LocalDateTime.of(2024, 2, 29, 23, 59, 59)
                ),
                Pair.of(
                        LocalDateTime.of(2024, 2, 15, 8, 0),
                        LocalDateTime.of(2024, 2, 15, 15, 0)
                )
        );
        assertThat(capturedValues.get(0).get(0)).isIn(expectedPairs);
        assertThat(capturedValues.get(1).get(0)).isIn(expectedPairs);

    }

    private List<SchoolBillingItemEntity> getBillingEntities() {
        BigDecimal hourPrice = BigDecimal.TEN;

        LocalDateTime entryDate1 = LocalDateTime.of(2024, 1, 31, 8, 0);
        LocalDateTime exitDate1 = LocalDateTime.of(2024, 3, 1, 15, 0);

        var schoolBillingItemEntity1 = new SchoolBillingItemEntity();
        schoolBillingItemEntity1.setChildId(1);
        schoolBillingItemEntity1.setParentId(1);
        schoolBillingItemEntity1.setHourPrice(hourPrice);
        schoolBillingItemEntity1.setId(1);
        schoolBillingItemEntity1.setEntryDate(entryDate1);
        schoolBillingItemEntity1.setExitDate(exitDate1);

        LocalDateTime entryDate2 = LocalDateTime.of(2024, 2, 15, 8, 0);
        LocalDateTime exitDate2 = LocalDateTime.of(2024, 2, 15, 15, 0);

        var schoolBillingItemEntity2 = new SchoolBillingItemEntity();
        schoolBillingItemEntity2.setChildId(2);
        schoolBillingItemEntity2.setParentId(2);
        schoolBillingItemEntity2.setHourPrice(hourPrice);
        schoolBillingItemEntity2.setId(2);
        schoolBillingItemEntity2.setEntryDate(entryDate2);
        schoolBillingItemEntity2.setExitDate(exitDate2);

        return List.of(schoolBillingItemEntity1, schoolBillingItemEntity2);
    }
}