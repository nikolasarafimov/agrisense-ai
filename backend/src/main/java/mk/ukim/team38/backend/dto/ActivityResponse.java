package mk.ukim.team38.backend.dto;

import java.time.LocalDate;

public record ActivityResponse(
        Long id,
        String description,
        LocalDate date,
        String type
) {
}