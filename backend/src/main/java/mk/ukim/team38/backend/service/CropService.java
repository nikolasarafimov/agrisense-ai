package mk.ukim.team38.backend.service;

import lombok.RequiredArgsConstructor;
import mk.ukim.team38.backend.model.Crop;
import mk.ukim.team38.backend.model.User;
import mk.ukim.team38.backend.repository.CropRepository;
import mk.ukim.team38.backend.security.AuthenticatedUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CropService {

    private final CropRepository cropRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public List<Crop> findAll(String search) {
        User user = authenticatedUserService.getCurrentUser();

        if (search != null && !search.isBlank()) {
            return cropRepository
                    .findByUserAndNameContainingIgnoreCaseOrUserAndTypeContainingIgnoreCase(
                            user,
                            search,
                            user,
                            search
                    );
        }

        return cropRepository.findByUser(user);
    }

    public Optional<Crop> findById(Long id) {
        User user = authenticatedUserService.getCurrentUser();

        return cropRepository.findByIdAndUser(id, user);
    }

    public Crop save(Crop crop) {
        User user = authenticatedUserService.getCurrentUser();

        crop.setId(null);
        crop.setUser(user);

        return cropRepository.save(crop);
    }

    public Crop update(Long id, Crop cropDetails) {
        User user = authenticatedUserService.getCurrentUser();

        Crop crop = cropRepository.findByIdAndUser(id, user)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Crop not found with id: " + id
                        )
                );

        crop.setName(cropDetails.getName());
        crop.setType(cropDetails.getType());
        crop.setPlantingDate(cropDetails.getPlantingDate());

        return cropRepository.save(crop);
    }

    public void deleteById(Long id) {
        User user = authenticatedUserService.getCurrentUser();

        Crop crop = cropRepository.findByIdAndUser(id, user)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Crop not found with id: " + id
                        )
                );

        cropRepository.delete(crop);
    }
}