
package com.tecsup.petclinic.webs;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Generar los datos JSON de un propietario
    private String ownerJson(String firstName, String lastName) {
        return """
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "address": "Av. Ejercito 123",
                  "city": "Arequipa",
                  "telephone": "999888777"
                }
                """.formatted(firstName, lastName);
    }

    // Crear un propietario y devolver su ID
    private int createOwner(String firstName, String lastName)
            throws Exception {

        MvcResult result = mockMvc.perform(post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ownerJson(firstName, lastName)))
                .andExpect(status().isCreated())
                .andReturn();

        Number id = JsonPath.read(
                result.getResponse().getContentAsString(),
                "$.id"
        );

        return id.intValue();
    }

    // PRUEBA 1: Listar todos los propietarios
    @Test
    void testFindAllOwners() throws Exception {
        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    // PRUEBA 2: Buscar propietario existente
    @Test
    void testFindOwnerOK() throws Exception {
        mockMvc.perform(get("/owners/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("George")))
                .andExpect(jsonPath("$.lastName", is("Franklin")));
    }

    // PRUEBA 3: Buscar propietario inexistente
    @Test
    void testFindOwnerKO() throws Exception {
        mockMvc.perform(get("/owners/666"))
                .andExpect(status().isNotFound());
    }

    // PRUEBA 4: Crear propietario correctamente
    @Test
    void testCreateOwner() throws Exception {
        mockMvc.perform(post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ownerJson("Darlyn", "Huertas")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("Darlyn")))
                .andExpect(jsonPath("$.lastName", is("Huertas")))
                .andExpect(jsonPath("$.city", is("Arequipa")));
    }

    // PRUEBA 5: Rechazar propietario sin apellido
    @Test
    void testCreateOwner_BadRequest() throws Exception {
        String invalidJson = """
                {
                  "firstName": "Prueba",
                  "address": "Av. Principal 100",
                  "city": "Arequipa",
                  "telephone": "999888777"
                }
                """;

        mockMvc.perform(post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    // PRUEBA 6: Buscar propietarios con apellido Davis
    @Test
    void testFindByLastName() throws Exception {
        mockMvc.perform(get("/owners")
                        .param("lastName", "Davis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // PRUEBA 7: Actualizar propietario
    @Test
    void testUpdateOwner() throws Exception {
        int id = createOwner("Carlos", "Prueba");

        mockMvc.perform(put("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ownerJson("Carlos", "Actualizado")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/owners/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Carlos")))
                .andExpect(jsonPath("$.lastName", is("Actualizado")));
    }

    // PRUEBA 8: Eliminar propietario recién creado
    @Test
    void testDeleteOwner() throws Exception {
        int id = createOwner("Eliminar", "Temporal");

        mockMvc.perform(delete("/owners/{id}", id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/owners/{id}", id))
                .andExpect(status().isNotFound());
    }

    // PRUEBA 9: Eliminar propietario inexistente
    @Test
    void testDeleteOwnerKO() throws Exception {
        mockMvc.perform(delete("/owners/1000"))
                .andExpect(status().isNotFound());
    }
}
