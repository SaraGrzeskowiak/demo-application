package com.example.demo.core.dao.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@Entity
@Table(name = "child")
@NoArgsConstructor
@AllArgsConstructor
public class ChildEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "parent_id")
    private ParentEntity parent;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "school_id")
    private SchoolEntity school;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    private List<AttendanceEntity> attendance;
}
