package mk.ukim.team38.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mk.ukim.team38.backend.dto.CropRequest;
import mk.ukim.team38.backend.dto.CropResponse;
import mk.ukim.team38.backend.exception.ResourceNotFoundException;
import mk.ukim.team38.backend.model.Crop;
import mk.ukim.team38.backend.service.CropService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crops")
@RequiredArgsConstructor
public class CropController {

    private final CropService cropService;

    @GetMapping
    public List<CropResponse> getAllCrops(
            @RequestParam(required = false) String search
    ) {
        return cropService.findAll(search)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public CropResponse getCropById(
            @PathVariable Long id
    ) {
        Crop crop = cropService.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Crop not found with id: " + id
                        )
                );

        return toResponse(crop);
    }

    @PostMapping
    public ResponseEntity<CropResponse> createCrop(
            @Valid @RequestBody CropRequest request
    ) {
        Crop crop = new Crop();

        crop.setName(request.name());
        crop.setType(request.type());
        crop.setPlantingDate(
                request.plantingDate()
        );

        Crop savedCrop =
                cropService.save(crop);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedCrop));
    }

    @PutMapping("/{id}")
    public CropResponse updateCrop(
            @PathVariable Long id,
            @Valid @RequestBody CropRequest request
    ) {
        Crop cropDetails = new Crop();

        cropDetails.setName(request.name());
        cropDetails.setType(request.type());
        cropDetails.setPlantingDate(
                request.plantingDate()
        );

        return toResponse(
                cropService.update(id, cropDetails)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCrop(
            @PathVariable Long id
    ) {
        cropService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private CropResponse toResponse(Crop crop) {
        return new CropResponse(
                crop.getId(),
                crop.getName(),
                crop.getType(),
                crop.getPlantingDate()
        );
    }
}