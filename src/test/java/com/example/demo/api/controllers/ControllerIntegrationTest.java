package com.example.demo.api.controllers;

import com.example.demo.AbstractIntegrationTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(value = "/sql/add_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class ControllerIntegrationTest  {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void getParentBilling() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/parent/1/2024-02")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.parentFee").value(415.5))
                .andExpect(jsonPath("$.parent.firstname").value("John"))
                .andExpect(jsonPath("$.parent.lastname").value("Doe"))
                .andExpect(jsonPath("$.childrenBillingSummary[0].childFee").value(84))
                .andExpect(jsonPath("$.childrenBillingSummary[0].child.firstname").value("Alice"))
                .andExpect(jsonPath("$.childrenBillingSummary[0].child.lastname").value("Doe"))
                .andExpect(jsonPath("$.childrenBillingSummary[0].timeAtSchool").value("PT14H"))
                .andExpect(jsonPath("$.childrenBillingSummary[1].childFee").value(331.5))
                .andExpect(jsonPath("$.childrenBillingSummary[1].child.firstname").value("Sam"))
                .andExpect(jsonPath("$.childrenBillingSummary[1].child.lastname").value("Doe"))
                .andExpect(jsonPath("$.childrenBillingSummary[1].timeAtSchool").value("PT33H59M59S"));
    }

    @Test
    @SneakyThrows
    void getSchoolBilling() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/school/2/2024-02")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.total").value(382.5))
                .andExpect(jsonPath("$.parentsBilling[0].parentFee").value(331.5))
                .andExpect(jsonPath("$.parentsBilling[0].parent.firstname").value("John"))
                .andExpect(jsonPath("$.parentsBilling[0].parent.lastname").value("Doe"))
                .andExpect(jsonPath("$.parentsBilling[0].childrenBillingSummary[0].childFee").value(331.5))
                .andExpect(jsonPath("$.parentsBilling[0].childrenBillingSummary[0].child.firstname").value("Sam"))
                .andExpect(jsonPath("$.parentsBilling[0].childrenBillingSummary[0].child.lastname").value("Doe"))
                .andExpect(jsonPath("$.parentsBilling[0].childrenBillingSummary[0].timeAtSchool").value("PT33H59M59S"))
                .andExpect(jsonPath("$.parentsBilling[1].parentFee").value(51))
                .andExpect(jsonPath("$.parentsBilling[1].parent.firstname").value("Jane"))
                .andExpect(jsonPath("$.parentsBilling[1].parent.lastname").value("Smith"))
                .andExpect(jsonPath("$.parentsBilling[1].childrenBillingSummary[0].childFee").value(51))
                .andExpect(jsonPath("$.parentsBilling[1].childrenBillingSummary[0].child.firstname").value("Bob"))
                .andExpect(jsonPath("$.parentsBilling[1].childrenBillingSummary[0].child.lastname").value("Smith"))
                .andExpect(jsonPath("$.parentsBilling[1].childrenBillingSummary[0].timeAtSchool").value("PT7H"));
    }
}