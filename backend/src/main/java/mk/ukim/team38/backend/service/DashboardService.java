package mk.ukim.team38.backend.service;

import lombok.RequiredArgsConstructor;
import mk.ukim.team38.backend.dto.DashboardStatsResponse;
import mk.ukim.team38.backend.model.User;
import mk.ukim.team38.backend.repository.ActivityRepository;
import mk.ukim.team38.backend.repository.CropRepository;
import mk.ukim.team38.backend.repository.ParcelRepository;
import mk.ukim.team38.backend.security.AuthenticatedUserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CropRepository cropRepository;
    private final ParcelRepository parcelRepository;
    private final ActivityRepository activityRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public DashboardStatsResponse getDashboardStats() {
        User user =
                authenticatedUserService.getCurrentUser();

        long cropsCount =
                cropRepository.countByUser(user);

        long parcelsCount =
                parcelRepository.countByUser(user);

        long activitiesCount =
                activityRepository.countByUser(user);

        long totalRecords =
                cropsCount
                        + parcelsCount
                        + activitiesCount;

        return new DashboardStatsResponse(
                cropsCount,
                parcelsCount,
                activitiesCount,
                totalRecords
        );
    }
}