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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Description is required.")
    @Size(
            max = 255,
            message = "Description must not exceed 255 characters."
    )
    @Column(
            nullable = false,
            length = 255
    )
    private String description;

    @NotNull(message = "Activity date is required.")
    @Column(nullable = false)
    private LocalDate date;

    @NotBlank(message = "Activity type is required.")
    @Size(
            max = 100,
            message = "Activity type must not exceed 100 characters."
    )
    @Column(
            nullable = false,
            length = 100
    )
    private String type;

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