package mk.ukim.team38.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "app_user",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "email"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @NotBlank(
            message = "Full name is required."
    )
    @Size(
            max = 100,
            message = "Full name must not exceed 100 characters."
    )
    @Column(
            nullable = false,
            length = 100
    )
    private String fullName;

    @NotBlank(
            message = "Email is required."
    )
    @Email(
            message = "Email must be valid."
    )
    @Size(
            max = 254,
            message = "Email must not exceed 254 characters."
    )
    @Column(
            nullable = false,
            length = 254
    )
    private String email;

    @JsonIgnore
    @NotBlank(
            message = "Password is required."
    )
    @Size(
            max = 100,
            message = "Password must not exceed 100 characters."
    )
    @Column(
            nullable = false,
            length = 100
    )
    private String password;

    @NotBlank(
            message = "Role is required."
    )
    @Size(
            max = 20,
            message = "Role must not exceed 20 characters."
    )
    @Column(
            nullable = false,
            length = 20
    )
    private String role = "USER";
}