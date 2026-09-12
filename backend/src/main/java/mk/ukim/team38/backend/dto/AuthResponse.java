package mk.ukim.team38.backend.dto;

public class AuthResponse {

    private final Long id;
    private final String fullName;
    private final String email;
    private final String role;
    private final String token;
    private final String message;

    public AuthResponse(
            Long id,
            String fullName,
            String email,
            String role,
            String token,
            String message
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.token = token;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }
}