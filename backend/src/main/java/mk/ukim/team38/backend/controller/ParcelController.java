package mk.ukim.team38.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mk.ukim.team38.backend.model.Parcel;
import mk.ukim.team38.backend.service.ParcelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parcels")
@RequiredArgsConstructor
public class ParcelController {

    private final ParcelService parcelService;

    @GetMapping
    public List<Parcel> getAllParcels(
            @RequestParam(required = false) String search
    ) {
        return parcelService.findAll(search);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Parcel> getParcelById(@PathVariable Long id) {
        return parcelService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Parcel createParcel(@Valid @RequestBody Parcel parcel) {
        return parcelService.save(parcel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Parcel> updateParcel(@PathVariable Long id, @Valid @RequestBody Parcel parcelDetails) {
        try {
            return ResponseEntity.ok(parcelService.update(id, parcelDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParcel(@PathVariable Long id) {
        parcelService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}