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
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Location is required.")
    @Size(
            max = 200,
            message = "Location must not exceed 200 characters."
    )
    @Column(
            nullable = false,
            length = 200
    )
    private String location;

    @NotNull(message = "Parcel size is required.")
    @Positive(message = "Size must be greater than zero.")
    @Column(nullable = false)
    private Double size;

    @NotBlank(message = "Soil type is required.")
    @Size(
            max = 100,
            message = "Soil type must not exceed 100 characters."
    )
    @Column(
            nullable = false,
            length = 100
    )
    private String soilType;

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