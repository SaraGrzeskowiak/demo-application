package com.example.demo.core.services;

import com.example.demo.api.exception.NotFoundException;
import com.example.demo.api.models.ParentBillingSummaryResponse;
import com.example.demo.api.models.SchoolBillingSummaryResponse;
import com.example.demo.core.dao.entity.ParentEntity;
import com.example.demo.core.dao.entity.billing.SchoolBillingItemEntity;
import com.example.demo.core.dao.repository.AttendanceRepository;
import com.example.demo.core.dao.repository.ChildRepository;
import com.example.demo.core.dao.repository.ParentRepository;
import com.example.demo.core.dao.repository.SchoolRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SchoolBillingServiceImpl extends AbstractBillingService implements SchoolBillingService {
    private final AttendanceRepository attendanceRepository;
    private final SchoolRepository schoolRepository;
    private final ParentRepository parentRepository;

    public SchoolBillingServiceImpl(
            AttendanceRepository attendanceRepository,
            SchoolRepository schoolRepository,
            ChildRepository childRepository,
            ParentRepository parentRepository,
            FeeCalculationService feeCalculationService
    ) {
        super(childRepository, feeCalculationService);
        this.attendanceRepository = attendanceRepository;
        this.schoolRepository = schoolRepository;
        this.parentRepository = parentRepository;
    }

    @Override
    public SchoolBillingSummaryResponse getBilling(int schoolId, LocalDate billingDate) {
        if (!schoolRepository.existsById(schoolId)) {
            throw new NotFoundException("school");
        }

        var billingItems = attendanceRepository.findAttendanceBySchoolId(schoolId, billingDate.getMonthValue(), billingDate.getYear())
                .stream()
                .map(item -> clipToRelevantMonth(item, billingDate))
                .toList();

        var childrenNames = getChildrenNames(billingItems);
        var parents = getParents(billingItems);

        var parentSummaries = new ArrayList<ParentBillingSummaryResponse>();
        parents.forEach(parent -> {
            var childrenIdsOfParentIds = getChildrenIdsOfParentIds(parent, billingItems);
            var childrenNamesOfParent = getChildrenNamesOfParent(childrenNames, childrenIdsOfParentIds);

            parentSummaries.add(getParentSummaryResponse(childrenNamesOfParent, billingItems, parent));
        });

        return SchoolBillingSummaryResponse.builder()
                .total(getTotalFee(parentSummaries))
                .parentsBilling(parentSummaries)
                .build();
    }

    private static BigDecimal getTotalFee(ArrayList<ParentBillingSummaryResponse> parentSummaries) {
        return parentSummaries.stream()
                .map(ParentBillingSummaryResponse::getParentFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static Map<Integer, Pair<String, String>> getChildrenNamesOfParent(Map<Integer, Pair<String, String>> childrenNames, Set<Integer> childrenIdsOfParentIds) {
        return childrenNames.entrySet().stream()
                .filter(pair -> childrenIdsOfParentIds.contains(pair.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Set<Integer> getChildrenIdsOfParentIds(ParentEntity parent, List<SchoolBillingItemEntity> billingItems) {
        return billingItems.stream()
                .filter(item -> item.getParentId() == parent.getId())
                .map(SchoolBillingItemEntity::getChildId)
                .collect(Collectors.toSet());
    }

    private List<ParentEntity> getParents(List<SchoolBillingItemEntity> billingItems) {
        return parentRepository.findAllById(billingItems.stream()
                .map(SchoolBillingItemEntity::getParentId)
                .collect(Collectors.toSet()));
    }

    private Map<Integer, Pair<String, String>> getChildrenNames(List<SchoolBillingItemEntity> billingItems) {
        return getChildrenNames(billingItems.stream()
                .map(SchoolBillingItemEntity::getChildId)
                .collect(Collectors.toSet()));
    }
}
