package mk.ukim.team38.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CropRequest(

        @NotBlank(message = "Crop name is required.")
        @Size(max = 100, message = "Crop name must not exceed 100 characters.")
        String name,

        @NotBlank(message = "Crop type is required.")
        @Size(max = 100, message = "Crop type must not exceed 100 characters.")
        String type,

        @NotNull(message = "Planting date is required.")
        LocalDate plantingDate
) {
}