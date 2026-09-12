package mk.ukim.team38.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mk.ukim.team38.backend.dto.ActivityRequest;
import mk.ukim.team38.backend.dto.ActivityResponse;
import mk.ukim.team38.backend.exception.ResourceNotFoundException;
import mk.ukim.team38.backend.model.Activity;
import mk.ukim.team38.backend.service.ActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    public List<ActivityResponse> getAllActivities(
            @RequestParam(required = false) String search
    ) {
        return activityService.findAll(search)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ActivityResponse getActivityById(
            @PathVariable Long id
    ) {
        Activity activity =
                activityService.findById(id)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Activity not found with id: " + id
                                )
                        );

        return toResponse(activity);
    }

    @PostMapping
    public ResponseEntity<ActivityResponse> createActivity(
            @Valid @RequestBody ActivityRequest request
    ) {
        Activity activity = new Activity();

        activity.setDescription(
                request.description()
        );
        activity.setDate(
                request.date()
        );
        activity.setType(
                request.type()
        );

        Activity savedActivity =
                activityService.save(activity);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedActivity));
    }

    @PutMapping("/{id}")
    public ActivityResponse updateActivity(
            @PathVariable Long id,
            @Valid @RequestBody ActivityRequest request
    ) {
        Activity activityDetails =
                new Activity();

        activityDetails.setDescription(
                request.description()
        );
        activityDetails.setDate(
                request.date()
        );
        activityDetails.setType(
                request.type()
        );

        return toResponse(
                activityService.update(
                        id,
                        activityDetails
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(
            @PathVariable Long id
    ) {
        activityService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private ActivityResponse toResponse(
            Activity activity
    ) {
        return new ActivityResponse(
                activity.getId(),
                activity.getDescription(),
                activity.getDate(),
                activity.getType()
        );
    }
}