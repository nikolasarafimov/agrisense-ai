package mk.ukim.team38.backend.dto;

import java.time.LocalDate;

public record CropResponse(
        Long id,
        String name,
        String type,
        LocalDate plantingDate
) {
}