package mk.ukim.team38.backend.service;

import lombok.RequiredArgsConstructor;
import mk.ukim.team38.backend.model.Activity;
import mk.ukim.team38.backend.model.User;
import mk.ukim.team38.backend.repository.ActivityRepository;
import mk.ukim.team38.backend.security.AuthenticatedUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public List<Activity> findAll(String search) {
        User user = authenticatedUserService.getCurrentUser();

        if (search != null && !search.isBlank()) {
            return activityRepository
                    .findByUserAndDescriptionContainingIgnoreCaseOrUserAndTypeContainingIgnoreCase(
                            user,
                            search,
                            user,
                            search
                    );
        }

        return activityRepository.findByUser(user);
    }

    public Optional<Activity> findById(Long id) {
        User user = authenticatedUserService.getCurrentUser();

        return activityRepository.findByIdAndUser(id, user);
    }

    public Activity save(Activity activity) {
        User user = authenticatedUserService.getCurrentUser();

        activity.setId(null);
        activity.setUser(user);

        return activityRepository.save(activity);
    }

    public Activity update(
            Long id,
            Activity activityDetails
    ) {
        User user = authenticatedUserService.getCurrentUser();

        Activity activity =
                activityRepository.findByIdAndUser(id, user)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Activity not found with id: " + id
                                )
                        );

        activity.setDescription(
                activityDetails.getDescription()
        );
        activity.setDate(
                activityDetails.getDate()
        );
        activity.setType(
                activityDetails.getType()
        );

        return activityRepository.save(activity);
    }

    public void deleteById(Long id) {
        User user = authenticatedUserService.getCurrentUser();

        Activity activity =
                activityRepository.findByIdAndUser(id, user)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Activity not found with id: " + id
                                )
                        );

        activityRepository.delete(activity);
    }
}