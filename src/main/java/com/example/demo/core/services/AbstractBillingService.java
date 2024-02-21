package com.example.demo.core.services;

import com.example.demo.api.models.ChildBilling;
import com.example.demo.api.models.ParentBillingSummaryResponse;
import com.example.demo.api.models.ParsonNames;
import com.example.demo.core.dao.entity.ChildEntity;
import com.example.demo.core.dao.entity.ParentEntity;
import com.example.demo.core.dao.entity.billing.BaseBillingItem;
import com.example.demo.core.dao.repository.ChildRepository;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractBillingService {
    protected final ChildRepository childRepository;
    protected final FeeCalculationService feeCalculationService;

    protected AbstractBillingService(ChildRepository childRepository, FeeCalculationService feeCalculationService) {
        this.childRepository = childRepository;
        this.feeCalculationService = feeCalculationService;
    }

    protected <T extends BaseBillingItem> T clipToRelevantMonth(T item, LocalDate billingDate) {
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

    protected <T extends BaseBillingItem> ParentBillingSummaryResponse getParentSummaryResponse(
            Map<Integer, Pair<String, String>> childrenNames, List<T> billingItems, ParentEntity parent) {

        var childrenSummaries = getChildrenSummary(childrenNames, billingItems);

        var totalFee = childrenSummaries.stream().map(ChildBilling::getChildFee).reduce(BigDecimal.ZERO, BigDecimal::add);
        return ParentBillingSummaryResponse.builder()
                .parent(new ParsonNames(parent.getFirstname(), parent.getLastname()))
                .parentFee(totalFee)
                .childrenBillingSummary(childrenSummaries)
                .build();
    }

    protected  <T extends BaseBillingItem> List<ChildBilling> getChildrenSummary(Map<Integer, Pair<String, String>> childrenNames, List<T> billingItems) {
        var childrenSummaries = new ArrayList<ChildBilling>();
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
                    .childFee(feeSummary.getTotalFee())
                    .timeAtSchool(feeSummary.getTimeAtSchool())
                    .build()
            );
        });

        return childrenSummaries;
    }

    protected Map<Integer, Pair<String, String>> getChildrenNames(Set<Integer> ids) {
        return childRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(
                        ChildEntity::getId,
                        child -> Pair.of(child.getFirstname(), child.getLastname())
                ));
    }

    protected <T extends BaseBillingItem> BigDecimal getHourPrice(List<T> items) {
        return items.stream()
                .findFirst()
                .map(BaseBillingItem::getHourPrice)
                .orElse(BigDecimal.ZERO);
    }
}
