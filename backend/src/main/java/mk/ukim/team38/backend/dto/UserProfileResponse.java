package mk.ukim.team38.backend.dto;

public class UserProfileResponse {

    private final Long id;
    private final String fullName;
    private final String email;
    private final String role;

    public UserProfileResponse(
            Long id,
            String fullName,
            String email,
            String role
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
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
}