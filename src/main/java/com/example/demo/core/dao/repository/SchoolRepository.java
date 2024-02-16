package com.example.demo.core.dao.repository;

import com.example.demo.core.dao.entity.SchoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<SchoolEntity, Integer> {
}
