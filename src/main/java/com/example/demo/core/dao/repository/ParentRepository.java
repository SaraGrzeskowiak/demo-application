package com.example.demo.core.dao.repository;

import com.example.demo.core.dao.entity.ParentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentRepository extends JpaRepository<ParentEntity, Integer> {
}
