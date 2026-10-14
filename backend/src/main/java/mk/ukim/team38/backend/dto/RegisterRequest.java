package mk.ukim.team38.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

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

    @NotBlank(message = "Password is required.")
    @Size(
            min = 6,
            max = 72,
            message = "Password must contain between 6 and 72 characters."
    )
    private String password;
}