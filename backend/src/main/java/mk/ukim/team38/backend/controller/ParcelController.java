package mk.ukim.team38.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mk.ukim.team38.backend.dto.ParcelRequest;
import mk.ukim.team38.backend.dto.ParcelResponse;
import mk.ukim.team38.backend.exception.ResourceNotFoundException;
import mk.ukim.team38.backend.model.Parcel;
import mk.ukim.team38.backend.service.ParcelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parcels")
@RequiredArgsConstructor
public class ParcelController {

    private final ParcelService parcelService;

    @GetMapping
    public List<ParcelResponse> getAllParcels(
            @RequestParam(required = false) String search
    ) {
        return parcelService.findAll(search)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ParcelResponse getParcelById(
            @PathVariable Long id
    ) {
        Parcel parcel = parcelService.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Parcel not found with id: " + id
                        )
                );

        return toResponse(parcel);
    }

    @PostMapping
    public ResponseEntity<ParcelResponse> createParcel(
            @Valid @RequestBody ParcelRequest request
    ) {
        Parcel parcel = new Parcel();

        parcel.setLocation(request.location());
        parcel.setSize(request.size());
        parcel.setSoilType(request.soilType());

        Parcel savedParcel =
                parcelService.save(parcel);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedParcel));
    }

    @PutMapping("/{id}")
    public ParcelResponse updateParcel(
            @PathVariable Long id,
            @Valid @RequestBody ParcelRequest request
    ) {
        Parcel parcelDetails = new Parcel();

        parcelDetails.setLocation(request.location());
        parcelDetails.setSize(request.size());
        parcelDetails.setSoilType(request.soilType());

        return toResponse(
                parcelService.update(
                        id,
                        parcelDetails
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParcel(
            @PathVariable Long id
    ) {
        parcelService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private ParcelResponse toResponse(Parcel parcel) {
        return new ParcelResponse(
                parcel.getId(),
                parcel.getLocation(),
                parcel.getSize(),
                parcel.getSoilType()
        );
    }
}