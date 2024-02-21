package com.example.demo.core.dao.repository;

import com.example.demo.core.dao.entity.AttendanceEntity;
import com.example.demo.core.dao.entity.billing.ParentBillingItemEntity;
import com.example.demo.core.dao.entity.billing.SchoolBillingItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Integer> {
    @Query(name = "findParentBillingItems", nativeQuery = true)
    List<ParentBillingItemEntity> findAttendanceByParentId(@Param("parentId") int parentId, @Param("month") int month, @Param("year") int year);

    @Query(name = "findSchoolBillingItems", nativeQuery = true)
    List<SchoolBillingItemEntity> findAttendanceBySchoolId(@Param("schoolId") int schoolId, @Param("month") int month, @Param("year") int year);
}
