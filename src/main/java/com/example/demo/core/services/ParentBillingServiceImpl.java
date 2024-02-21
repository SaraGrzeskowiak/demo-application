package com.example.demo.core.services;

import com.example.demo.api.exception.NotFoundException;
import com.example.demo.api.models.ParentBillingSummaryResponse;
import com.example.demo.core.dao.entity.billing.ParentBillingItemEntity;
import com.example.demo.core.dao.repository.AttendanceRepository;
import com.example.demo.core.dao.repository.ChildRepository;
import com.example.demo.core.dao.repository.ParentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
public class ParentBillingServiceImpl extends AbstractBillingService implements ParentBillingService {
    private final AttendanceRepository attendanceRepository;
    private final ParentRepository parentRepository;

    public ParentBillingServiceImpl(AttendanceRepository attendanceRepository, ParentRepository parentRepository, ChildRepository childRepository, FeeCalculationService feeCalculationService) {
        super(childRepository, feeCalculationService);
        this.attendanceRepository = attendanceRepository;
        this.parentRepository = parentRepository;
    }


    @Override
    public ParentBillingSummaryResponse getBilling(int parentId, LocalDate billingDate) {
        var parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("parent"));

        var billingItems = attendanceRepository.findAttendanceByParentId(parentId, billingDate.getMonthValue(), billingDate.getYear())
                .stream()
                .map(item -> clipToRelevantMonth(item, billingDate))
                .toList();

        var childrenIds = billingItems.stream()
                .map(ParentBillingItemEntity::getChildId)
                .collect(Collectors.toSet());
        var childrenNames = getChildrenNames(childrenIds);

        return getParentSummaryResponse(childrenNames, billingItems, parent);
    }
}
