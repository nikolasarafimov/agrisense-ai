package mk.ukim.team38.backend;

import mk.ukim.team38.backend.model.Activity;
import mk.ukim.team38.backend.model.Crop;
import mk.ukim.team38.backend.model.Parcel;
import mk.ukim.team38.backend.model.User;
import mk.ukim.team38.backend.repository.ActivityRepository;
import mk.ukim.team38.backend.repository.CropRepository;
import mk.ukim.team38.backend.repository.ParcelRepository;
import mk.ukim.team38.backend.service.ExportService;
import mk.ukim.team38.backend.service.ImportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImportExportServiceTest {

    @Mock
    private CropRepository cropRepository;

    @Mock
    private ParcelRepository parcelRepository;

    @Mock
    private ActivityRepository activityRepository;

    private ImportService importService;
    private ExportService exportService;

    private User user;

    @BeforeEach
    void setUp() {
        importService = new ImportService(
                cropRepository,
                parcelRepository,
                activityRepository
        );

        exportService = new ExportService(
                cropRepository,
                parcelRepository,
                activityRepository
        );

        user = new User(
                1L,
                "Test User",
                "test@example.com",
                "password",
                "USER"
        );
    }

    @Test
    void importCropsCsvShouldParseValidLocalDate() throws Exception {
        String csv = """
                id,name,type,plantingDate
                1,Wheat,Grain,2026-09-12
                """;

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "crops.csv",
                        "text/csv",
                        csv.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        Map<String, Object> result =
                importService.importCropsCsv(
                        file,
                        user
                );

        assertEquals(
                1,
                result.get("imported")
        );

        assertEquals(
                0,
                result.get("skipped")
        );

        ArgumentCaptor<Crop> captor =
                ArgumentCaptor.forClass(
                        Crop.class
                );

        verify(cropRepository)
                .save(captor.capture());

        Crop savedCrop =
                captor.getValue();

        assertEquals(
                "Wheat",
                savedCrop.getName()
        );

        assertEquals(
                "Grain",
                savedCrop.getType()
        );

        assertEquals(
                LocalDate.of(
                        2026,
                        9,
                        12
                ),
                savedCrop.getPlantingDate()
        );

        assertEquals(
                user,
                savedCrop.getUser()
        );
    }

    @Test
    void importCropsCsvShouldRejectInvalidDate() throws Exception {
        String csv = """
                id,name,type,plantingDate
                1,Wheat,Grain,2026-99-99
                """;

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "crops.csv",
                        "text/csv",
                        csv.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        Map<String, Object> result =
                importService.importCropsCsv(
                        file,
                        user
                );

        assertEquals(
                0,
                result.get("imported")
        );

        assertEquals(
                1,
                result.get("skipped")
        );

        @SuppressWarnings("unchecked")
        List<String> errors =
                (List<String>) result.get(
                        "errors"
                );

        assertEquals(
                1,
                errors.size()
        );

        assertTrue(
                errors.getFirst()
                        .contains(
                                "Planting date must use yyyy-MM-dd format."
                        )
        );

        verify(
                cropRepository,
                never()
        ).save(
                any(Crop.class)
        );
    }

    @Test
    void importParcelsCsvShouldParseValidParcel() throws Exception {
        String csv = """
                id,location,size,soilType
                1,North Field,2.5,Loam
                """;

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "parcels.csv",
                        "text/csv",
                        csv.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        Map<String, Object> result =
                importService.importParcelsCsv(
                        file,
                        user
                );

        assertEquals(
                1,
                result.get("imported")
        );

        assertEquals(
                0,
                result.get("skipped")
        );

        ArgumentCaptor<Parcel> captor =
                ArgumentCaptor.forClass(
                        Parcel.class
                );

        verify(parcelRepository)
                .save(captor.capture());

        Parcel savedParcel =
                captor.getValue();

        assertEquals(
                "North Field",
                savedParcel.getLocation()
        );

        assertEquals(
                2.5,
                savedParcel.getSize()
        );

        assertEquals(
                "Loam",
                savedParcel.getSoilType()
        );

        assertEquals(
                user,
                savedParcel.getUser()
        );
    }

    @Test
    void importParcelsCsvShouldRejectNonPositiveSize() throws Exception {
        String csv = """
                id,location,size,soilType
                1,North Field,0,Loam
                """;

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "parcels.csv",
                        "text/csv",
                        csv.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        Map<String, Object> result =
                importService.importParcelsCsv(
                        file,
                        user
                );

        assertEquals(
                0,
                result.get("imported")
        );

        assertEquals(
                1,
                result.get("skipped")
        );

        @SuppressWarnings("unchecked")
        List<String> errors =
                (List<String>) result.get(
                        "errors"
                );

        assertEquals(
                1,
                errors.size()
        );

        assertTrue(
                errors.getFirst()
                        .contains(
                                "Parcel size must be greater than zero."
                        )
        );

        verify(
                parcelRepository,
                never()
        ).save(
                any(Parcel.class)
        );
    }

    @Test
    void importActivitiesCsvShouldParseValidLocalDate() throws Exception {
        String csv = """
                id,description,date,type
                1,Irrigation completed,2026-09-12,Irrigation
                """;

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "activities.csv",
                        "text/csv",
                        csv.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        Map<String, Object> result =
                importService.importActivitiesCsv(
                        file,
                        user
                );

        assertEquals(
                1,
                result.get("imported")
        );

        assertEquals(
                0,
                result.get("skipped")
        );

        ArgumentCaptor<Activity> captor =
                ArgumentCaptor.forClass(
                        Activity.class
                );

        verify(activityRepository)
                .save(captor.capture());

        Activity savedActivity =
                captor.getValue();

        assertEquals(
                "Irrigation completed",
                savedActivity.getDescription()
        );

        assertEquals(
                LocalDate.of(
                        2026,
                        9,
                        12
                ),
                savedActivity.getDate()
        );

        assertEquals(
                "Irrigation",
                savedActivity.getType()
        );

        assertEquals(
                user,
                savedActivity.getUser()
        );
    }

    @Test
    void importActivitiesCsvShouldRejectInvalidDate() throws Exception {
        String csv = """
                id,description,date,type
                1,Irrigation completed,not-a-date,Irrigation
                """;

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "activities.csv",
                        "text/csv",
                        csv.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        Map<String, Object> result =
                importService.importActivitiesCsv(
                        file,
                        user
                );

        assertEquals(
                0,
                result.get("imported")
        );

        assertEquals(
                1,
                result.get("skipped")
        );

        @SuppressWarnings("unchecked")
        List<String> errors =
                (List<String>) result.get(
                        "errors"
                );

        assertEquals(
                1,
                errors.size()
        );

        assertTrue(
                errors.getFirst()
                        .contains(
                                "Activity date must use yyyy-MM-dd format."
                        )
        );

        verify(
                activityRepository,
                never()
        ).save(
                any(Activity.class)
        );
    }

    @Test
    void exportCropsCsvShouldFormatLocalDateAsIsoDate() {
        Crop crop =
                new Crop();

        crop.setId(1L);
        crop.setName("Wheat");
        crop.setType("Grain");

        crop.setPlantingDate(
                LocalDate.of(
                        2026,
                        9,
                        12
                )
        );

        crop.setUser(user);

        when(
                cropRepository.findByUser(
                        user
                )
        ).thenReturn(
                List.of(crop)
        );

        String csv =
                exportService.exportCropsCsv(
                        user
                );

        assertEquals(
                """
                id,name,type,plantingDate
                1,Wheat,Grain,2026-09-12
                """,
                csv
        );
    }

    @Test
    void exportParcelsCsvShouldExportParcelData() {
        Parcel parcel =
                new Parcel();

        parcel.setId(1L);
        parcel.setLocation(
                "North Field"
        );

        parcel.setSize(
                2.5
        );

        parcel.setSoilType(
                "Loam"
        );

        parcel.setUser(user);

        when(
                parcelRepository.findByUser(
                        user
                )
        ).thenReturn(
                List.of(parcel)
        );

        String csv =
                exportService.exportParcelsCsv(
                        user
                );

        assertEquals(
                """
                id,location,size,soilType
                1,North Field,2.5,Loam
                """,
                csv
        );
    }

    @Test
    void exportActivitiesCsvShouldFormatLocalDateAsIsoDate() {
        Activity activity =
                new Activity();

        activity.setId(1L);

        activity.setDescription(
                "Irrigation completed"
        );

        activity.setDate(
                LocalDate.of(
                        2026,
                        9,
                        12
                )
        );

        activity.setType(
                "Irrigation"
        );

        activity.setUser(user);

        when(
                activityRepository.findByUser(
                        user
                )
        ).thenReturn(
                List.of(activity)
        );

        String csv =
                exportService.exportActivitiesCsv(
                        user
                );

        assertEquals(
                """
                id,description,date,type
                1,Irrigation completed,2026-09-12,Irrigation
                """,
                csv
        );
    }
}