package mk.ukim.team38.backend.dto;

public record ParcelResponse(
        Long id,
        String location,
        Double size,
        String soilType
) {
}