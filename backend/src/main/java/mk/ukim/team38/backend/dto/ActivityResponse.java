package mk.ukim.team38.backend.dto;

public record ActivityResponse(
        Long id,
        String description,
        String date,
        String type
) {
}