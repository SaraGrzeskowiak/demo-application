package com.example.demo.core.services;

import com.example.demo.api.models.ChildBilling;
import com.example.demo.api.models.ParentBillingSummaryResponse;
import com.example.demo.core.dao.entity.BillingItemEntity;
import com.example.demo.core.dao.entity.ChildEntity;
import com.example.demo.core.dao.entity.ParentEntity;
import com.example.demo.core.dao.repository.AttendanceRepository;
import com.example.demo.core.dao.repository.ChildRepository;
import com.example.demo.core.dao.repository.ParentRepository;
import com.example.demo.core.model.FeeCalculationSummary;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParentBillingServiceImplTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private ParentRepository parentRepository;

    @Mock
    private ChildRepository childRepository;

    @Mock
    private FeeCalculationService feeCalculationService;

    @InjectMocks
    private ParentBillingServiceImpl parentBillingService;

    @Captor
    ArgumentCaptor<List<Pair<LocalDateTime, LocalDateTime>>> billingItemsCaptor;

    @Test
    void getBillingShouldSetAllResponseFields() {
        // Arrange
        int parentId = 1;
        int childId = 1;
        LocalDate billingDate = LocalDate.of(2024, 2, 1);
        String parentFirstname = "John";
        String parentLastname = "Doe";
        String childFirstname = "Jane";
        String childLastname = "Doe";
        BigDecimal hourPrice = BigDecimal.TEN;
        BigDecimal totalFee = BigDecimal.valueOf(100);
        LocalDateTime entryDate = LocalDateTime.of(2024, 2, 1, 8, 0);
        LocalDateTime exitDate = LocalDateTime.of(2024, 2, 1, 15, 0);
        List<BillingItemEntity> billingItems = Collections.singletonList(
                new BillingItemEntity(1, hourPrice, 1, entryDate, exitDate)
        );

        var parentEntity = ParentEntity.builder()
                .id(parentId)
                .firstname(parentFirstname)
                .lastname(parentLastname)
                .build();
        var childEntity = ChildEntity.builder()
                .id(childId)
                .parent(parentEntity)
                .firstname(childFirstname)
                .lastname(childLastname)
                .build();
        when(childRepository.findAllById(any())).thenReturn(Collections.singletonList(childEntity));
        when(parentRepository.findById(parentId)).thenReturn(Optional.of(parentEntity));
        when(attendanceRepository.findAttendance(parentId, billingDate.getMonthValue(), billingDate.getYear())).thenReturn(billingItems);
        when(feeCalculationService.calculateFee(any(), any())).thenReturn(new FeeCalculationSummary(Duration.ofHours(7), totalFee));

        // Act
        ParentBillingSummaryResponse response = parentBillingService.getBilling(parentId, billingDate);

        // Verify
        assertEquals(parentFirstname, response.getParent().getFirstname());
        assertEquals(parentLastname, response.getParent().getLastname());
        assertEquals(totalFee, response.getTotalFee());
        assertEquals(1, response.getChildrenBillingSummary().size());
        ChildBilling childBilling = response.getChildrenBillingSummary().get(0);
        assertEquals(childFirstname, childBilling.getChild().getFirstname());
        assertEquals(childLastname, childBilling.getChild().getLastname());
        assertEquals(totalFee, childBilling.getTotal());
    }

    @Test
    void getBillingShouldCorrectlyClipEntryAndExitDate() {
        // Arrange
        int parentId = 1;
        int childId = 1;
        LocalDate billingDate = LocalDate.of(2024, 2, 1);
        String parentFirstname = "John";
        String parentLastname = "Doe";
        String childFirstname = "Jane";
        String childLastname = "Doe";
        BigDecimal hourPrice = BigDecimal.TEN;
        BigDecimal totalFee = BigDecimal.valueOf(100);
        LocalDateTime entryDate = LocalDateTime.of(2024, 1, 31, 8, 0);
        LocalDateTime exitDate = LocalDateTime.of(2024, 3, 1, 15, 0);
        List<BillingItemEntity> billingItems = Collections.singletonList(
                new BillingItemEntity(1, hourPrice, 1, entryDate, exitDate)
        );

        var parentEntity = ParentEntity.builder()
                .id(parentId)
                .firstname(parentFirstname)
                .lastname(parentLastname)
                .build();
        var childEntity = ChildEntity.builder()
                .id(childId)
                .parent(parentEntity)
                .firstname(childFirstname)
                .lastname(childLastname)
                .build();
        when(childRepository.findAllById(any())).thenReturn(Collections.singletonList(childEntity));
        when(parentRepository.findById(parentId)).thenReturn(Optional.of(parentEntity));
        when(attendanceRepository.findAttendance(parentId, billingDate.getMonthValue(), billingDate.getYear())).thenReturn(billingItems);
        when(feeCalculationService.calculateFee(any(), any())).thenReturn(new FeeCalculationSummary(Duration.ofHours(700), totalFee));

        // Act
        parentBillingService.getBilling(parentId, billingDate);

        // Verify
        verify(feeCalculationService, times(1)).calculateFee(any(), billingItemsCaptor.capture());
        var capturedValue =  billingItemsCaptor.getValue();
        assertThat(capturedValue).hasSize(1);
        assertThat(capturedValue.get(0))
                .isEqualTo(Pair.of(
                        LocalDateTime.of(2024, 2, 1, 0, 0, 0),
                        LocalDateTime.of(2024, 2, 29, 23, 59, 59)
                ));
    }
}