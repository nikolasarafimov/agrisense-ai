package mk.ukim.team38.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mk.ukim.team38.backend.model.Crop;
import mk.ukim.team38.backend.service.CropService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crops")
@RequiredArgsConstructor
public class CropController {

    private final CropService cropService;

    @GetMapping
    public List<Crop> getAllCrops(
            @RequestParam(required = false) String search
    ) {
        return cropService.findAll(search);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Crop> getCropById(@PathVariable Long id) {
        return cropService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Crop createCrop(@Valid @RequestBody Crop crop) {
        return cropService.save(crop);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Crop> updateCrop(@PathVariable Long id, @Valid @RequestBody Crop cropDetails) {
        try {
            return ResponseEntity.ok(cropService.update(id, cropDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCrop(@PathVariable Long id) {
        cropService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}