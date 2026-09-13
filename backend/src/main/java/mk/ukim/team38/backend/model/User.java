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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "app_user",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "email"
                )
        }
)
@Data
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
    @Column(
            nullable = false,
            length = 100
    )
    private String fullName;

    @Email(
            message = "Email must be valid."
    )
    @NotBlank(
            message = "Email is required."
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
    @Column(
            nullable = false,
            length = 100
    )
    private String password;

    @NotBlank(
            message = "Role is required."
    )
    @Column(
            nullable = false,
            length = 20
    )
    private String role = "USER";
}