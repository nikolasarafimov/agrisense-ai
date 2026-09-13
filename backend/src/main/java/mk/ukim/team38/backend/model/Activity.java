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

import java.time.LocalDate;

@Entity
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

    public Activity() {
    }

    public Activity(
            String description,
            LocalDate date,
            String type
    ) {
        this.description = description;
        this.date = date;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public User getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(
            String description
    ) {
        this.description = description;
    }

    public void setDate(
            LocalDate date
    ) {
        this.date = date;
    }

    public void setType(
            String type
    ) {
        this.type = type;
    }

    public void setUser(
            User user
    ) {
        this.user = user;
    }
}