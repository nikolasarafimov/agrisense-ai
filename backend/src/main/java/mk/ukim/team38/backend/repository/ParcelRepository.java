package mk.ukim.team38.backend.repository;

import mk.ukim.team38.backend.model.Parcel;
import mk.ukim.team38.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParcelRepository extends JpaRepository<Parcel, Long> {

    List<Parcel> findByUser(User user);

    long countByUser(User user);

    List<Parcel> findByLocationContainingIgnoreCaseOrSoilTypeContainingIgnoreCase(
            String location,
            String soilType
    );

    List<Parcel> findByUserAndLocationContainingIgnoreCaseOrUserAndSoilTypeContainingIgnoreCase(
            User user1,
            String location,
            User user2,
            String soilType
    );

    Optional<Parcel> findByIdAndUser(Long id, User user);

    void deleteByUser(User user);
}