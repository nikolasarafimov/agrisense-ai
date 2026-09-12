package mk.ukim.team38.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ActivityRequest(

        @NotBlank(message = "Description is required.")
        @Size(max = 255, message = "Description must not exceed 255 characters.")
        String description,

        @NotNull(message = "Activity date is required.")
        LocalDate date,

        @NotBlank(message = "Activity type is required.")
        @Size(max = 100, message = "Activity type must not exceed 100 characters.")
        String type
) {
}