package mk.ukim.team38.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ParcelRequest(

        @NotBlank(message = "Location is required.")
        @Size(max = 200, message = "Location must not exceed 200 characters.")
        String location,

        @NotNull(message = "Parcel size is required.")
        @Positive(message = "Size must be greater than zero.")
        Double size,

        @NotBlank(message = "Soil type is required.")
        @Size(max = 100, message = "Soil type must not exceed 100 characters.")
        String soilType
) {
}