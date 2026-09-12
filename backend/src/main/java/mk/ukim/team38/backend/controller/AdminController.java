package mk.ukim.team38.backend.controller;

import lombok.RequiredArgsConstructor;
import mk.ukim.team38.backend.dto.UserProfileResponse;
import mk.ukim.team38.backend.model.Activity;
import mk.ukim.team38.backend.model.Crop;
import mk.ukim.team38.backend.model.Parcel;
import mk.ukim.team38.backend.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public List<UserProfileResponse> getUsers() {
        return adminService.getUsers();
    }

    @GetMapping("/crops")
    public List<Crop> getCrops() {
        return adminService.getCrops();
    }

    @GetMapping("/parcels")
    public List<Parcel> getParcels() {
        return adminService.getParcels();
    }

    @GetMapping("/activities")
    public List<Activity> getActivities() {
        return adminService.getActivities();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id
    ) {
        adminService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/crops/{id}")
    public ResponseEntity<Void> deleteCrop(
            @PathVariable Long id
    ) {
        adminService.deleteCrop(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/parcels/{id}")
    public ResponseEntity<Void> deleteParcel(
            @PathVariable Long id
    ) {
        adminService.deleteParcel(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/activities/{id}")
    public ResponseEntity<Void> deleteActivity(
            @PathVariable Long id
    ) {
        adminService.deleteActivity(id);

        return ResponseEntity.noContent().build();
    }
}