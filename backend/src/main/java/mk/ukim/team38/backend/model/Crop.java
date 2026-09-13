package mk.ukim.team38.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Crop name is required.")
    @Size(
            max = 100,
            message = "Crop name must not exceed 100 characters."
    )
    @Column(
            nullable = false,
            length = 100
    )
    private String name;

    @NotBlank(message = "Crop type is required.")
    @Size(
            max = 100,
            message = "Crop type must not exceed 100 characters."
    )
    @Column(
            nullable = false,
            length = 100
    )
    private String type;

    @NotNull(message = "Planting date is required.")
    @Column(nullable = false)
    private LocalDate plantingDate;

    @NotNull(message = "User is required.")
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    @JsonIgnoreProperties({
            "password"
    })
    private User user;
}