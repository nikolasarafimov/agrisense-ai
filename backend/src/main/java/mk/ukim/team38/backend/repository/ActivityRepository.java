package mk.ukim.team38.backend.repository;

import mk.ukim.team38.backend.model.Activity;
import mk.ukim.team38.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByUser(User user);

    List<Activity> findByUserAndDescriptionContainingIgnoreCaseOrUserAndTypeContainingIgnoreCase(
            User user1,
            String description,
            User user2,
            String type
    );

    Optional<Activity> findByIdAndUser(Long id, User user);

    void deleteByUser(User user);
}