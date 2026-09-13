package mk.ukim.team38.backend.dto;

public record UserProfileResponse(
        Long id,
        String fullName,
        String email,
        String role
) {
}