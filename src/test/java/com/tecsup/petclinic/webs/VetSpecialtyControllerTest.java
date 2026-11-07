package com.tecsup.petclinic.webs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@Transactional
class VetSpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // PRUEBA 1: Asignar una especialidad
    @Test
    void testAssignSpecialtyToVet() throws Exception {

        String json = """
                {
                  "vetId": 1,
                  "specialtyId": 1
                }
                """;

        mockMvc.perform(post("/vet-specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vetId", is(1)))
                .andExpect(jsonPath("$.specialtyId", is(1)));

        mockMvc.perform(get("/vets/1/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name",
                        is("radiology")));
    }

    // PRUEBA 2: Eliminar una especialidad asignada
    @Test
    void testRemoveSpecialtyFromVet() throws Exception {

        mockMvc.perform(
                        delete("/vet-specialties/3/3"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/vets/3/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name",
                        is("surgery")));
    }

    // PRUEBA 3: Evitar asignaciones duplicadas
    @Test
    void testAssignDuplicate_ShouldFail() throws Exception {

        String json = """
                {
                  "vetId": 3,
                  "specialtyId": 2
                }
                """;

        mockMvc.perform(post("/vet-specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    // PRUEBA 4: Consultar especialidades de un veterinario
    @Test
    void testFindSpecialtiesByVet() throws Exception {

        mockMvc.perform(get("/vets/3/specialties"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name",
                        is("surgery")))
                .andExpect(jsonPath("$[1].name",
                        is("dentistry")));
    }

    // PRUEBA 5: Consultar veterinarios de una especialidad
    @Test
    void testFindVetsBySpecialty() throws Exception {

        mockMvc.perform(get("/specialties/1/vets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[1].id", is(5)));
    }

    // PRUEBA 6: Veterinario sin especialidades
    @Test
    void testFindSpecialtiesByVet_Empty() throws Exception {

        mockMvc.perform(get("/vets/1/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // PRUEBA 7: Marcar una especialidad como principal
    @Test
    void testSetPrimarySpecialty() throws Exception {

        mockMvc.perform(
                        put("/vet-specialties/3/3/primary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.specialtyId", is(3)))
                .andExpect(jsonPath("$.primary", is(true)));
    }

    // PRUEBA 8: Solo una especialidad principal
    @Test
    void testSetPrimarySpecialty_OnlyOnePrimary()
            throws Exception {

        mockMvc.perform(
                        put("/vet-specialties/3/3/primary"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/vets/3/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].primary",
                        is(false)))
                .andExpect(jsonPath("$[1].primary",
                        is(true)));
    }

    // PRUEBA 9: Especialidad no asignada
    @Test
    void testSetPrimarySpecialty_NotAssigned()
            throws Exception {

        mockMvc.perform(
                        put("/vet-specialties/1/2/primary"))
                .andExpect(status().isNotFound());
    }

    // PRUEBA 10: Filtrar por años de experiencia
    @Test
    void testFindVetsByMinExperience() throws Exception {

        mockMvc.perform(get("/specialties/1/vets")
                        .param("minExperience", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[1].id", is(5)));
    }

    // PRUEBA 11: Actualizar años de experiencia
    @Test
    void testUpdateYearsExperience() throws Exception {

        String json = """
                {
                  "yearsExperience": 15
                }
                """;

        mockMvc.perform(put(
                        "/vet-specialties/3/3/experience")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.yearsExperience",
                        is(15)));

        mockMvc.perform(get("/vets/3/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$[1].yearsExperience", is(15)));
    }

    // PRUEBA 12: Actualizar fecha de certificacion
    @Test
    void testUpdateCertificationDate() throws Exception {

        String json = """
                {
                  "certificationDate": "2026-10-01"
                }
                """;

        mockMvc.perform(put(
                        "/vet-specialties/3/3/certification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificationDate",
                        is("2026-10-01")));

        mockMvc.perform(get("/vets/3/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$[1].certificationDate",
                        is("2026-10-01")));
    }
}
