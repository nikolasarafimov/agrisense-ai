package mk.ukim.team38.backend.dto;

public record DashboardStatsResponse(
        long cropsCount,
        long parcelsCount,
        long activitiesCount,
        long totalRecords
) {
}