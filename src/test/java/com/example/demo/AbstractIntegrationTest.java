package com.example.demo;

import com.example.demo.core.dao.repository.AttendanceRepository;
import com.example.demo.core.dao.repository.ChildRepository;
import com.example.demo.core.dao.repository.ParentRepository;
import com.example.demo.core.dao.repository.SchoolRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {
    @Autowired
    protected ParentRepository parentRepository;
    @Autowired
    protected ChildRepository childRepository;
    @Autowired
    protected SchoolRepository schoolRepository;
    @Autowired
    protected AttendanceRepository attendanceRepository;

    @AfterEach
    protected void cleanUp() {
        parentRepository.deleteAll();
        childRepository.deleteAll();
        schoolRepository.deleteAll();
        attendanceRepository.deleteAll();
    }
}
