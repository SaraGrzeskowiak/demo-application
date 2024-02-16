package com.example.demo.core.dao.repository;

import com.example.demo.core.dao.entity.ChildEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildRepository extends JpaRepository<ChildEntity, Integer> {
}
