package mk.ukim.team38.backend.dto;

public record AuthResponse(
        Long id,
        String fullName,
        String email,
        String role,
        String token,
        String message
) {
}