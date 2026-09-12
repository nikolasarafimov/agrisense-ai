package mk.ukim.team38.backend.dto;

public record CropResponse(
        Long id,
        String name,
        String type,
        String plantingDate
) {
}