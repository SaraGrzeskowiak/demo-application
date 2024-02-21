package com.example.demo.core.services;

import com.example.demo.api.exception.NotFoundException;
import com.example.demo.api.models.ChildBilling;
import com.example.demo.api.models.ParentBillingSummaryResponse;
import com.example.demo.api.models.ParsonNames;
import com.example.demo.core.dao.entity.BillingItemEntity;
import com.example.demo.core.dao.entity.ChildEntity;
import com.example.demo.core.dao.repository.AttendanceRepository;
import com.example.demo.core.dao.repository.ChildRepository;
import com.example.demo.core.dao.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParentBillingServiceImpl implements ParentBillingService {
    private final AttendanceRepository attendanceRepository;
    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;
    private final FeeCalculationService feeCalculationService;

    @Override
    public ParentBillingSummaryResponse getBilling(int parentId, LocalDate billingDate) {
        var optionalParent = parentRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("parent"));

        var billingItems = attendanceRepository.findAttendance(parentId, billingDate.getMonthValue(), billingDate.getYear())
                .stream()
                .map(item -> clipToRelevantMonth(item, billingDate))
                .toList();

        var childrenSummaries = new ArrayList<ChildBilling>();
        var childrenNames = getChildrenNames(billingItems.stream().map(BillingItemEntity::getChildId).collect(Collectors.toSet()));

        childrenNames.keySet().forEach(childId -> {
            var childItems = billingItems.stream()
                    .filter(item -> item.getChildId() == childId)
                    .toList();

            var hourPrice = getHourPrice(childItems);
            var feeSummary = feeCalculationService.calculateFee(hourPrice, childItems.stream()
                    .map(item -> Pair.of(item.getEntryDate(), item.getExitDate()))
                    .toList());

            var names = childrenNames.get(childId);
            childrenSummaries.add(ChildBilling.builder()
                    .child(new ParsonNames(names.getFirst(), names.getSecond()))
                    .total(feeSummary.getTotalFee())
                    .timeAtSchool(feeSummary.getTimeAtSchool())
                    .build()
            );
        });

        var totalFee = childrenSummaries.stream().map(ChildBilling::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return ParentBillingSummaryResponse.builder()
                .parent(new ParsonNames(optionalParent.getFirstname(), optionalParent.getLastname()))
                .totalFee(totalFee)
                .childrenBillingSummary(childrenSummaries)
                .build();
    }

    private BillingItemEntity clipToRelevantMonth(BillingItemEntity item, LocalDate billingDate) {
        var year = billingDate.getYear();
        var month = billingDate.getMonthValue();

        if (item.getEntryDate().toLocalDate().isBefore(billingDate)) {
            item.setEntryDate(LocalDateTime.of(year, month, 1, 0, 0));
        }
        if (item.getExitDate().toLocalDate().isAfter(billingDate.withDayOfMonth(YearMonth.of(billingDate.getYear(), billingDate.getMonthValue()).lengthOfMonth()))) {
            item.setExitDate(LocalDateTime.of(year, month, YearMonth.of(year, month).lengthOfMonth(), 23, 59, 59));
        }

        return item;
    }

    private BigDecimal getHourPrice(List<BillingItemEntity> items) {
        return items.stream()
                .findFirst()
                .map(BillingItemEntity::getHourPrice)
                .orElse(BigDecimal.ZERO);
    }

    private Map<Integer, Pair<String, String>> getChildrenNames(Set<Integer> ids) {
        return childRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(
                        ChildEntity::getId,
                        child -> Pair.of(child.getFirstname(), child.getLastname())
                ));
    }
}
