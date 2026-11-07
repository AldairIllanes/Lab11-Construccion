package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.SpecialtyDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class SpecialtyControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFindAllSpecialties() throws Exception {
        mockMvc.perform(get("/specialties"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    public void testFindSpecialtyOK() throws Exception {
        mockMvc.perform(get("/specialties/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("radiology")))
                .andExpect(jsonPath("$.office", is("Farewell")));
    }

    @Test
    public void testFindSpecialtyKO() throws Exception {
        mockMvc.perform(get("/specialties/666"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateSpecialty() throws Exception {
        SpecialtyDTO newSpecialty = SpecialtyDTO.builder()
                .name("cardiology").office("Lima").openHour(9).closeHour(17)
                .build();

        mockMvc.perform(post("/specialties")
                        .content(om.writeValueAsString(newSpecialty))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("cardiology")))
                .andExpect(jsonPath("$.office", is("Lima")));
    }

    @Test
    public void testDeleteSpecialty() throws Exception {
        SpecialtyDTO newSpecialty = SpecialtyDTO.builder()
                .name("to-delete").office("Lima").openHour(8).closeHour(12)
                .build();

        String response = mockMvc.perform(post("/specialties")
                        .content(om.writeValueAsString(newSpecialty))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.parse(response).read("$.id");

        mockMvc.perform(delete("/specialties/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteSpecialtyKO() throws Exception {
        mockMvc.perform(delete("/specialties/1000"))
                .andExpect(status().isNotFound());
    }
}