package com.example.demo.core.dao.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@Builder
@Entity
@Table(name = "school")
@NoArgsConstructor
@AllArgsConstructor
public class SchoolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "hour_price")
    private BigDecimal hourPrice;

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
    private List<ChildEntity> children;
}
