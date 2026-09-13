package mk.ukim.team38.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

    @NotBlank(message = "Full name is required.")
    @Size(
            max = 100,
            message = "Full name must not exceed 100 characters."
    )
    private String fullName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    @Size(
            max = 254,
            message = "Email must not exceed 254 characters."
    )
    private String email;

    @Pattern(
            regexp = "^(?:$|.{6,72})$",
            message = "Password must be empty or contain between 6 and 72 characters."
    )
    private String password;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}