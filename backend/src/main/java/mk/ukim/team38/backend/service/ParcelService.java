package mk.ukim.team38.backend.service;

import lombok.RequiredArgsConstructor;
import mk.ukim.team38.backend.model.Parcel;
import mk.ukim.team38.backend.model.User;
import mk.ukim.team38.backend.repository.ParcelRepository;
import mk.ukim.team38.backend.security.AuthenticatedUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParcelService {

    private final ParcelRepository parcelRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public List<Parcel> findAll(String search) {
        User user = authenticatedUserService.getCurrentUser();

        if (search != null && !search.isBlank()) {
            return parcelRepository
                    .findByUserAndLocationContainingIgnoreCaseOrUserAndSoilTypeContainingIgnoreCase(
                            user,
                            search,
                            user,
                            search
                    );
        }

        return parcelRepository.findByUser(user);
    }

    public Optional<Parcel> findById(Long id) {
        User user = authenticatedUserService.getCurrentUser();

        return parcelRepository.findByIdAndUser(id, user);
    }

    public Parcel save(Parcel parcel) {
        User user = authenticatedUserService.getCurrentUser();

        parcel.setId(null);
        parcel.setUser(user);

        return parcelRepository.save(parcel);
    }

    public Parcel update(Long id, Parcel parcelDetails) {
        User user = authenticatedUserService.getCurrentUser();

        Parcel parcel = parcelRepository.findByIdAndUser(id, user)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Parcel not found with id: " + id
                        )
                );

        parcel.setLocation(parcelDetails.getLocation());
        parcel.setSize(parcelDetails.getSize());
        parcel.setSoilType(parcelDetails.getSoilType());

        return parcelRepository.save(parcel);
    }

    public void deleteById(Long id) {
        User user = authenticatedUserService.getCurrentUser();

        Parcel parcel = parcelRepository.findByIdAndUser(id, user)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Parcel not found with id: " + id
                        )
                );

        parcelRepository.delete(parcel);
    }
}