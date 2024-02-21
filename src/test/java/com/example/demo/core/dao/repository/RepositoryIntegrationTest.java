package com.example.demo.core.dao.repository;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.core.dao.entity.AttendanceEntity;
import com.example.demo.core.dao.entity.ChildEntity;
import com.example.demo.core.dao.entity.ParentEntity;
import com.example.demo.core.dao.entity.SchoolEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;


class RepositoryIntegrationTest extends AbstractIntegrationTest {

    @Test
    void parentEntityIsPersisted() {
        // Arrange
        var firstname = "firstname";
        var lastname = "firstname";
        var entity = ParentEntity.builder()
                .firstname(firstname)
                .lastname(lastname)
                .build();

        // Act
        parentRepository.save(entity);

        // Assert
        var items = parentRepository.findAll();
        assertThat(items).hasSize(1);
        var item = items.get(0);
        assertThat(item.getFirstname()).isEqualTo(firstname);
        assertThat(item.getLastname()).isEqualTo(lastname);
    }

    @Test
    void childEntityIsPersisted() {
        // Arrange
        var firstname = "firstname";
        var lastname = "firstname";
        var entity = ChildEntity.builder()
                .firstname(firstname)
                .lastname(lastname)
                .build();

        // Act
        childRepository.save(entity);

        // Assert
        var items = childRepository.findAll();
        assertThat(items).hasSize(1);
        var item = items.get(0);
        assertThat(item.getFirstname()).isEqualTo(firstname);
        assertThat(item.getLastname()).isEqualTo(lastname);
    }

    @Test
    void schoolEntityIsPersisted() {
        // Arrange
        var name = "school name";
        var price = 24.25;
        var entity = SchoolEntity.builder()
                .name(name)
                .hourPrice(BigDecimal.valueOf(price))
                .build();

        // Act
        schoolRepository.save(entity);

        // Assert
        var items = schoolRepository.findAll();
        assertThat(items).hasSize(1);
        var item = items.get(0);
        assertThat(item.getName()).isEqualTo(name);
        assertThat(item.getHourPrice()).isEqualTo(BigDecimal.valueOf(price));
    }

    @Test
    void attendanceEntityIsPersisted() {
        // Arrange
        var entry = LocalDateTime.of(2024, 2, 1, 12, 0);
        var exit = LocalDateTime.of(2024, 2, 1, 15, 0);
        var entity = AttendanceEntity.builder()
                .entryDate(entry)
                .exitDate(exit)
                .build();

        // Act
        attendanceRepository.save(entity);

        // Assert
        var items = attendanceRepository.findAll();
        assertThat(items).hasSize(1);
        var item = items.get(0);
        assertThat(item.getEntryDate()).isEqualTo(entry);
        assertThat(item.getExitDate()).isEqualTo(exit);
    }

    @Test
    void savingParentWithChildShouldCascadePersistence() {
        // Arrange
        var parentName = "parent name";
        var parent = ParentEntity.builder()
                .firstname(parentName)
                .lastname(parentName)
                .build();

        var schoolName = "school name";
        var price = 24.25;
        var school = SchoolEntity.builder()
                .name(schoolName)
                .hourPrice(BigDecimal.valueOf(price))
                .build();

        var childName1 = "child 1";
        var childName2 = "child 2";
        var child1 = ChildEntity.builder()
                .firstname(childName1)
                .lastname(childName1)
                .parent(parent)
                .school(school)
                .build();
        var child2 = ChildEntity.builder()
                .firstname(childName2)
                .lastname(childName2)
                .parent(parent)
                .school(school)
                .build();

        var entry1 = LocalDateTime.of(2024, 2, 1, 12, 0);
        var exit1 = LocalDateTime.of(2024, 2, 1, 15, 0);
        var attendance1 = AttendanceEntity.builder()
                .child(child1)
                .entryDate(entry1)
                .exitDate(exit1)
                .build();
        var entry2 = LocalDateTime.of(2024, 2, 1, 14, 0);
        var exit2 = LocalDateTime.of(2024, 2, 1, 17, 0);
        var attendance2 = AttendanceEntity.builder()
                .child(child2)
                .entryDate(entry2)
                .exitDate(exit2)
                .build();

        child1.setAttendance(List.of(attendance1));
        child2.setAttendance(List.of(attendance2));
        school.setChildren(List.of(child1, child2));
        parent.setChildren(List.of(child1, child2));

        // Act
        parentRepository.save(parent);

        // Assert
        assertThat(parentRepository.findAll()).hasSize(1);
        assertThat(parentRepository.findAll().get(0).getFirstname()).isEqualTo(parentName);

        assertThat(childRepository.findAll()).hasSize(2);
        assertThat(childRepository.findAll())
                .extracting("firstname")
                        .containsOnly(childName1, childName2);

        assertThat(schoolRepository.findAll()).hasSize(1);
        assertThat(schoolRepository.findAll().get(0).getName()).isEqualTo(schoolName);

        assertThat(attendanceRepository.findAll()).hasSize(2);
        assertThat(attendanceRepository.findAll())
                .extracting("entryDate", "exitDate")
                        .containsOnly(
                                tuple(entry1, exit1),
                                tuple(entry2, exit2)
                        );
    }
}