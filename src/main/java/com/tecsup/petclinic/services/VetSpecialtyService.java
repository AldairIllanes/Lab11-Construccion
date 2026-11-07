
package com.tecsup.petclinic.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VetSpecialtyService {

    private final JdbcTemplate jdbc;

    public VetSpecialtyService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Objetos para devolver datos en JSON
    public record SpecialtyInfo(
            int id,
            String name,
            boolean primary,
            int yearsExperience,
            LocalDate certificationDate) {}

    public record VetInfo(
            int id,
            String firstName,
            String lastName,
            int yearsExperience,
            boolean primary) {}

    public record AssignmentInfo(
            int vetId,
            int specialtyId,
            boolean primary,
            int yearsExperience,
            LocalDate certificationDate) {}

    // Comprobar si existe el veterinario
    public boolean vetExists(int id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM vets WHERE id = ?",
                Integer.class, id);
        return count != null && count > 0;
    }

    // Comprobar si existe la especialidad
    public boolean specialtyExists(int id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM specialties WHERE id = ?",
                Integer.class, id);
        return count != null && count > 0;
    }

    // Comprobar si la asignacion ya existe
    public boolean assignmentExists(int vetId, int specId) {
        Integer count = jdbc.queryForObject(
                """
                SELECT COUNT(*) FROM vet_specialties
                WHERE vet_id = ? AND specialty_id = ?
                """,
                Integer.class, vetId, specId);
        return count != null && count > 0;
    }

    // Consultar las especialidades de un veterinario
    public List<SpecialtyInfo> findSpecialtiesByVet(int vetId) {
        return jdbc.query("""
                SELECT s.id, s.name, vs.is_primary,
                       vs.years_experience, vs.certification_date
                FROM specialties s
                JOIN vet_specialties vs
                  ON s.id = vs.specialty_id
                WHERE vs.vet_id = ?
                ORDER BY s.id
                """,
                (rs, row) -> new SpecialtyInfo(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getBoolean("is_primary"),
                        rs.getInt("years_experience"),
                        toLocalDate(rs.getDate("certification_date"))
                ),
                vetId);
    }

    // Consultar veterinarios por especialidad y experiencia
    public List<VetInfo> findVetsBySpecialty(
            int specId, int minExperience) {

        return jdbc.query("""
                SELECT v.id, v.first_name, v.last_name,
                       vs.years_experience, vs.is_primary
                FROM vets v
                JOIN vet_specialties vs
                  ON v.id = vs.vet_id
                WHERE vs.specialty_id = ?
                  AND vs.years_experience >= ?
                ORDER BY v.id
                """,
                (rs, row) -> new VetInfo(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("years_experience"),
                        rs.getBoolean("is_primary")
                ),
                specId, minExperience);
    }

    // Consultar una asignacion especifica
    public Optional<AssignmentInfo> findAssignment(
            int vetId, int specId) {

        List<AssignmentInfo> result = jdbc.query("""
                SELECT vet_id, specialty_id, is_primary,
                       years_experience, certification_date
                FROM vet_specialties
                WHERE vet_id = ? AND specialty_id = ?
                """,
                (rs, row) -> new AssignmentInfo(
                        rs.getInt("vet_id"),
                        rs.getInt("specialty_id"),
                        rs.getBoolean("is_primary"),
                        rs.getInt("years_experience"),
                        toLocalDate(rs.getDate("certification_date"))
                ),
                vetId, specId);

        return result.stream().findFirst();
    }

    // Asignar una especialidad a un veterinario
    @Transactional
    public void assign(int vetId, int specId) {
        jdbc.update("""
                INSERT INTO vet_specialties
                (vet_id, specialty_id, years_experience, is_primary)
                VALUES (?, ?, 0, FALSE)
                """,
                vetId, specId);
    }

    // Eliminar una asignacion
    @Transactional
    public int remove(int vetId, int specId) {
        return jdbc.update("""
                DELETE FROM vet_specialties
                WHERE vet_id = ? AND specialty_id = ?
                """,
                vetId, specId);
    }

    // Marcar una especialidad como principal
    @Transactional
    public boolean setPrimary(int vetId, int specId) {
        if (!assignmentExists(vetId, specId)) {
            return false;
        }

        jdbc.update("""
                UPDATE vet_specialties
                SET is_primary = FALSE
                WHERE vet_id = ?
                """,
                vetId);

        jdbc.update("""
                UPDATE vet_specialties
                SET is_primary = TRUE
                WHERE vet_id = ? AND specialty_id = ?
                """,
                vetId, specId);

        return true;
    }

    // Actualizar los años de experiencia
    @Transactional
    public int updateExperience(
            int vetId, int specId, int years) {

        return jdbc.update("""
                UPDATE vet_specialties
                SET years_experience = ?
                WHERE vet_id = ? AND specialty_id = ?
                """,
                years, vetId, specId);
    }

    // Actualizar la fecha de certificacion
    @Transactional
    public int updateCertification(
            int vetId, int specId, LocalDate date) {

        return jdbc.update("""
                UPDATE vet_specialties
                SET certification_date = ?
                WHERE vet_id = ? AND specialty_id = ?
                """,
                Date.valueOf(date), vetId, specId);
    }

    private LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }
}
