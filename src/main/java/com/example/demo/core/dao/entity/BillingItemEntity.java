package com.example.demo.core.dao.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NamedNativeQuery;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NamedNativeQuery(
        name = "findBillingItems",
        query = "SELECT a.id, s.hour_price, a.child_id, a.entry_date, a.exit_date " +
                " FROM attendance a" +
                " INNER JOIN child c ON a.child_id = c.id" +
                " INNER JOIN school s ON c.school_id = s.id" +
                " WHERE c.parent_id = :parentId" +
                " AND ((MONTH(a.entry_date) = :month AND YEAR(a.entry_date) = :year) OR (MONTH(a.exit_date) = :month AND YEAR(a.exit_date) = :year))",
        resultClass = BillingItemEntity.class
)
public class BillingItemEntity {
    @Id
    private int id;
    private BigDecimal hourPrice;
    private int childId;
    private LocalDateTime entryDate;
    private LocalDateTime exitDate;
}
