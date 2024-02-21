package com.example.demo.core.dao.repository;

import com.example.demo.core.dao.entity.AttendanceEntity;
import com.example.demo.core.dao.entity.BillingItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Integer> {
    @Query(name = "findBillingItems", nativeQuery = true)
    List<BillingItemEntity> findAttendance(@Param("parentId") int parentId, @Param("month") int month, @Param("year") int year);
}
