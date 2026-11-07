package com.tecsup.petclinic.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitDTO {

    private Long id;
    private Integer petId;
    private Integer vetId;
    private LocalDate visitDate;
    private String description;
    private BigDecimal cost;
}
