package mk.ukim.team38.backend.service;

import lombok.RequiredArgsConstructor;
import mk.ukim.team38.backend.dto.UserProfileResponse;
import mk.ukim.team38.backend.model.Activity;
import mk.ukim.team38.backend.model.Crop;
import mk.ukim.team38.backend.model.Parcel;
import mk.ukim.team38.backend.model.User;
import mk.ukim.team38.backend.repository.ActivityRepository;
import mk.ukim.team38.backend.repository.CropRepository;
import mk.ukim.team38.backend.repository.ParcelRepository;
import mk.ukim.team38.backend.repository.UserRepository;
import mk.ukim.team38.backend.security.AuthenticatedUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CropRepository cropRepository;
    private final ParcelRepository parcelRepository;
    private final ActivityRepository activityRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public List<UserProfileResponse> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toUserProfileResponse)
                .toList();
    }

    public List<Crop> getCrops() {
        return cropRepository.findAll();
    }

    public List<Parcel> getParcels() {
        return parcelRepository.findAll();
    }

    public List<Activity> getActivities() {
        return activityRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long id) {
        User currentAdmin =
                authenticatedUserService.getCurrentUser();

        if (currentAdmin.getId().equals(id)) {
            throw new IllegalArgumentException(
                    "You cannot delete your own admin account."
            );
        }

        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "User not found with id: " + id
                        )
                );

        activityRepository.deleteByUser(user);
        cropRepository.deleteByUser(user);
        parcelRepository.deleteByUser(user);

        userRepository.delete(user);
    }

    public void deleteCrop(Long id) {
        if (!cropRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Crop not found with id: " + id
            );
        }

        cropRepository.deleteById(id);
    }

    public void deleteParcel(Long id) {
        if (!parcelRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Parcel not found with id: " + id
            );
        }

        parcelRepository.deleteById(id);
    }

    public void deleteActivity(Long id) {
        if (!activityRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Activity not found with id: " + id
            );
        }

        activityRepository.deleteById(id);
    }

    private UserProfileResponse toUserProfileResponse(
            User user
    ) {
        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }
}