package mk.ukim.team38.backend.repository;

import mk.ukim.team38.backend.model.Crop;
import mk.ukim.team38.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CropRepository extends JpaRepository<Crop, Long> {

    List<Crop> findByUser(User user);

    List<Crop> findByNameContainingIgnoreCaseOrTypeContainingIgnoreCase(String name, String type);

    List<Crop> findByUserAndNameContainingIgnoreCaseOrUserAndTypeContainingIgnoreCase(
            User user1,
            String name,
            User user2,
            String type
    );

    Optional<Crop> findByIdAndUser(Long id, User user);

    void deleteByUser(User user);
}