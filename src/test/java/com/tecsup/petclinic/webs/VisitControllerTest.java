package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.VisitDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class VisitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testFindAllVisits() throws Exception {
        mockMvc.perform(get("/visits"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("rabies shot")));
    }

    @Test
    void testFindVisitOK() throws Exception {
        mockMvc.perform(get("/visits/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("rabies shot")))
                .andExpect(jsonPath("$.petId", is(7)))
                .andExpect(jsonPath("$.vetId", is(2)))
                .andExpect(jsonPath("$.visitDate", is("2010-03-04")));
    }

    @Test
    void testFindVisitKO() throws Exception {
        mockMvc.perform(get("/visits/666"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindByPetId() throws Exception {
        mockMvc.perform(get("/visits").param("petId", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].petId", is(7)))
                .andExpect(jsonPath("$[1].petId", is(7)));
    }

    @Test
    void testCreateVisit() throws Exception {
        VisitDTO visit = newVisit("integration visit create");

        MvcResult result = mockMvc.perform(post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(visit)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.description", is(visit.getDescription())))
                .andExpect(jsonPath("$.petId", is(visit.getPetId())))
                .andExpect(jsonPath("$.visitDate", is(visit.getVisitDate().toString())))
                .andReturn();
        Long id = JsonPath.parse(result.getResponse().getContentAsString()).read("$.id", Long.class);
        mockMvc.perform(delete("/visits/{id}", id)).andExpect(status().isOk());
    }

    @Test
    void testUpdateVisit() throws Exception {
        Long id = createVisit("integration visit before update");
        VisitDTO update = newVisit("integration visit after update");

        mockMvc.perform(put("/visits/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(jsonPath("$.description", is(update.getDescription())));

        mockMvc.perform(get("/visits/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(update.getDescription())));

        mockMvc.perform(delete("/visits/{id}", id)).andExpect(status().isOk());
    }

    @Test
    void testDeleteVisit() throws Exception {
        Long id = createVisit("integration visit delete");

        mockMvc.perform(delete("/visits/{id}", id))
                .andExpect(status().isOk());
        mockMvc.perform(get("/visits/{id}", id))
                .andExpect(status().isNotFound());
    }

    private Long createVisit(String description) throws Exception {
        MvcResult result = mockMvc.perform(post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newVisit(description))))
                .andExpect(status().isCreated())
                .andReturn();
        return JsonPath.parse(result.getResponse().getContentAsString()).read("$.id", Long.class);
    }

    private VisitDTO newVisit(String description) {
        return VisitDTO.builder()
                .petId(1)
                .vetId(1)
                .visitDate(LocalDate.of(2025, 1, 15))
                .description(description)
                .cost(new BigDecimal("35.00"))
                .build();
    }
}
