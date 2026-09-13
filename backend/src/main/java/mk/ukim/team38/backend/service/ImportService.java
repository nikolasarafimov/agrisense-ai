package mk.ukim.team38.backend.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import mk.ukim.team38.backend.model.Activity;
import mk.ukim.team38.backend.model.Crop;
import mk.ukim.team38.backend.model.Parcel;
import mk.ukim.team38.backend.model.User;
import mk.ukim.team38.backend.repository.ActivityRepository;
import mk.ukim.team38.backend.repository.CropRepository;
import mk.ukim.team38.backend.repository.ParcelRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ImportService {

    private final CropRepository cropRepository;
    private final ParcelRepository parcelRepository;
    private final ActivityRepository activityRepository;

    public ImportService(
            CropRepository cropRepository,
            ParcelRepository parcelRepository,
            ActivityRepository activityRepository
    ) {
        this.cropRepository = cropRepository;
        this.parcelRepository = parcelRepository;
        this.activityRepository = activityRepository;
    }

    public Map<String, Object> importCropsCsv(
            MultipartFile file,
            User user
    ) throws IOException {

        int imported = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();

        try (
                CSVReader reader = new CSVReader(
                        new InputStreamReader(
                                file.getInputStream(),
                                StandardCharsets.UTF_8
                        )
                )
        ) {
            String[] row;
            int lineNumber = 0;

            while ((row = reader.readNext()) != null) {
                lineNumber++;

                if (lineNumber == 1) {
                    continue;
                }

                try {
                    if (row.length < 3) {
                        throw new IllegalArgumentException(
                                "Not enough columns."
                        );
                    }

                    Crop crop = new Crop();

                    if (row.length >= 4) {
                        crop.setName(
                                clean(row[1])
                        );

                        crop.setType(
                                clean(row[2])
                        );

                        crop.setPlantingDate(
                                parseRequiredDate(
                                        row[3],
                                        "Planting date"
                                )
                        );
                    } else {
                        crop.setName(
                                clean(row[0])
                        );

                        crop.setType(
                                clean(row[1])
                        );

                        crop.setPlantingDate(
                                parseRequiredDate(
                                        row[2],
                                        "Planting date"
                                )
                        );
                    }

                    validateCrop(crop);

                    crop.setUser(user);

                    cropRepository.save(crop);
                    imported++;

                } catch (Exception e) {
                    skipped++;

                    errors.add(
                            "Line "
                                    + lineNumber
                                    + ": "
                                    + errorMessage(e)
                    );
                }
            }

        } catch (CsvValidationException e) {
            throw new IOException(
                    "Invalid CSV file.",
                    e
            );
        }

        return Map.of(
                "imported", imported,
                "skipped", skipped,
                "errors", errors
        );
    }

    public Map<String, Object> importParcelsCsv(
            MultipartFile file,
            User user
    ) throws IOException {

        int imported = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();

        try (
                CSVReader reader = new CSVReader(
                        new InputStreamReader(
                                file.getInputStream(),
                                StandardCharsets.UTF_8
                        )
                )
        ) {
            String[] row;
            int lineNumber = 0;

            while ((row = reader.readNext()) != null) {
                lineNumber++;

                if (lineNumber == 1) {
                    continue;
                }

                try {
                    if (row.length < 3) {
                        throw new IllegalArgumentException(
                                "Not enough columns."
                        );
                    }

                    Parcel parcel = new Parcel();

                    if (row.length >= 4) {
                        parcel.setLocation(
                                clean(row[1])
                        );

                        parcel.setSize(
                                parseRequiredPositiveDouble(
                                        row[2],
                                        "Parcel size"
                                )
                        );

                        parcel.setSoilType(
                                clean(row[3])
                        );
                    } else {
                        parcel.setLocation(
                                clean(row[0])
                        );

                        parcel.setSize(
                                parseRequiredPositiveDouble(
                                        row[1],
                                        "Parcel size"
                                )
                        );

                        parcel.setSoilType(
                                clean(row[2])
                        );
                    }

                    validateParcel(parcel);

                    parcel.setUser(user);

                    parcelRepository.save(parcel);
                    imported++;

                } catch (Exception e) {
                    skipped++;

                    errors.add(
                            "Line "
                                    + lineNumber
                                    + ": "
                                    + errorMessage(e)
                    );
                }
            }

        } catch (CsvValidationException e) {
            throw new IOException(
                    "Invalid CSV file.",
                    e
            );
        }

        return Map.of(
                "imported", imported,
                "skipped", skipped,
                "errors", errors
        );
    }

    public Map<String, Object> importActivitiesCsv(
            MultipartFile file,
            User user
    ) throws IOException {

        int imported = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();

        try (
                CSVReader reader = new CSVReader(
                        new InputStreamReader(
                                file.getInputStream(),
                                StandardCharsets.UTF_8
                        )
                )
        ) {
            String[] row;
            int lineNumber = 0;

            while ((row = reader.readNext()) != null) {
                lineNumber++;

                if (lineNumber == 1) {
                    continue;
                }

                try {
                    if (row.length < 3) {
                        throw new IllegalArgumentException(
                                "Not enough columns."
                        );
                    }

                    Activity activity = new Activity();

                    if (row.length >= 4) {
                        activity.setDescription(
                                clean(row[1])
                        );

                        activity.setDate(
                                parseRequiredDate(
                                        row[2],
                                        "Activity date"
                                )
                        );

                        activity.setType(
                                clean(row[3])
                        );
                    } else {
                        activity.setDescription(
                                clean(row[0])
                        );

                        activity.setDate(
                                parseRequiredDate(
                                        row[1],
                                        "Activity date"
                                )
                        );

                        activity.setType(
                                clean(row[2])
                        );
                    }

                    validateActivity(activity);

                    activity.setUser(user);

                    activityRepository.save(activity);
                    imported++;

                } catch (Exception e) {
                    skipped++;

                    errors.add(
                            "Line "
                                    + lineNumber
                                    + ": "
                                    + errorMessage(e)
                    );
                }
            }

        } catch (CsvValidationException e) {
            throw new IOException(
                    "Invalid CSV file.",
                    e
            );
        }

        return Map.of(
                "imported", imported,
                "skipped", skipped,
                "errors", errors
        );
    }

    public Map<String, Object> importFromExcel(
            MultipartFile file,
            User user
    ) throws IOException {

        int imported = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();

        try (
                Workbook workbook =
                        new XSSFWorkbook(
                                file.getInputStream()
                        )
        ) {
            Sheet cropsSheet =
                    workbook.getSheet("Crops");

            if (cropsSheet != null) {
                for (
                        int i = 1;
                        i <= cropsSheet.getLastRowNum();
                        i++
                ) {
                    Row row =
                            cropsSheet.getRow(i);

                    if (row == null) {
                        continue;
                    }

                    try {
                        Crop crop = new Crop();

                        crop.setName(
                                getCellValue(
                                        row,
                                        1
                                )
                        );

                        crop.setType(
                                getCellValue(
                                        row,
                                        2
                                )
                        );

                        crop.setPlantingDate(
                                getRequiredDateCellValue(
                                        row,
                                        3,
                                        "Planting date"
                                )
                        );

                        validateCrop(crop);

                        crop.setUser(user);

                        cropRepository.save(crop);
                        imported++;

                    } catch (Exception e) {
                        skipped++;

                        errors.add(
                                "Crops row "
                                        + (i + 1)
                                        + ": "
                                        + errorMessage(e)
                        );
                    }
                }
            }

            Sheet parcelsSheet =
                    workbook.getSheet("Parcels");

            if (parcelsSheet != null) {
                for (
                        int i = 1;
                        i <= parcelsSheet.getLastRowNum();
                        i++
                ) {
                    Row row =
                            parcelsSheet.getRow(i);

                    if (row == null) {
                        continue;
                    }

                    try {
                        Parcel parcel = new Parcel();

                        parcel.setLocation(
                                getCellValue(
                                        row,
                                        1
                                )
                        );

                        parcel.setSize(
                                parseRequiredPositiveDouble(
                                        getCellValue(
                                                row,
                                                2
                                        ),
                                        "Parcel size"
                                )
                        );

                        parcel.setSoilType(
                                getCellValue(
                                        row,
                                        3
                                )
                        );

                        validateParcel(parcel);

                        parcel.setUser(user);

                        parcelRepository.save(parcel);
                        imported++;

                    } catch (Exception e) {
                        skipped++;

                        errors.add(
                                "Parcels row "
                                        + (i + 1)
                                        + ": "
                                        + errorMessage(e)
                        );
                    }
                }
            }

            Sheet activitiesSheet =
                    workbook.getSheet("Activities");

            if (activitiesSheet != null) {
                for (
                        int i = 1;
                        i <= activitiesSheet.getLastRowNum();
                        i++
                ) {
                    Row row =
                            activitiesSheet.getRow(i);

                    if (row == null) {
                        continue;
                    }

                    try {
                        Activity activity =
                                new Activity();

                        activity.setDescription(
                                getCellValue(
                                        row,
                                        1
                                )
                        );

                        activity.setDate(
                                getRequiredDateCellValue(
                                        row,
                                        2,
                                        "Activity date"
                                )
                        );

                        activity.setType(
                                getCellValue(
                                        row,
                                        3
                                )
                        );

                        validateActivity(activity);

                        activity.setUser(user);

                        activityRepository.save(activity);
                        imported++;

                    } catch (Exception e) {
                        skipped++;

                        errors.add(
                                "Activities row "
                                        + (i + 1)
                                        + ": "
                                        + errorMessage(e)
                        );
                    }
                }
            }
        }

        return Map.of(
                "imported", imported,
                "skipped", skipped,
                "errors", errors
        );
    }

    private void validateCrop(
            Crop crop
    ) {
        requireText(
                crop.getName(),
                "Crop name",
                100
        );

        requireText(
                crop.getType(),
                "Crop type",
                100
        );

        if (crop.getPlantingDate() == null) {
            throw new IllegalArgumentException(
                    "Planting date is required."
            );
        }
    }

    private void validateParcel(
            Parcel parcel
    ) {
        requireText(
                parcel.getLocation(),
                "Parcel location",
                200
        );

        if (parcel.getSize() == null) {
            throw new IllegalArgumentException(
                    "Parcel size is required."
            );
        }

        if (parcel.getSize() <= 0) {
            throw new IllegalArgumentException(
                    "Parcel size must be greater than zero."
            );
        }

        requireText(
                parcel.getSoilType(),
                "Soil type",
                100
        );
    }

    private void validateActivity(
            Activity activity
    ) {
        requireText(
                activity.getDescription(),
                "Activity description",
                255
        );

        if (activity.getDate() == null) {
            throw new IllegalArgumentException(
                    "Activity date is required."
            );
        }

        requireText(
                activity.getType(),
                "Activity type",
                100
        );
    }

    private void requireText(
            String value,
            String fieldName,
            int maxLength
    ) {
        if (
                value == null
                        || value.isBlank()
        ) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        if (value.length() > maxLength) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must not exceed "
                            + maxLength
                            + " characters."
            );
        }
    }

    private LocalDate getRequiredDateCellValue(
            Row row,
            int index,
            String fieldName
    ) {
        Cell cell =
                row.getCell(index);

        if (cell == null) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        if (
                cell.getCellType()
                        == CellType.NUMERIC
                        && DateUtil.isCellDateFormatted(
                        cell
                )
        ) {
            return cell
                    .getLocalDateTimeCellValue()
                    .toLocalDate();
        }

        return parseRequiredDate(
                getCellValue(
                        row,
                        index
                ),
                fieldName
        );
    }

    private LocalDate parseRequiredDate(
            String value,
            String fieldName
    ) {
        String cleaned =
                clean(value);

        if (cleaned.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        try {
            return LocalDate.parse(cleaned);

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must use yyyy-MM-dd format."
            );
        }
    }

    private Double parseRequiredPositiveDouble(
            String value,
            String fieldName
    ) {
        String cleaned =
                clean(value);

        if (cleaned.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        final double parsedValue;

        try {
            parsedValue =
                    Double.parseDouble(cleaned);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must be a valid number."
            );
        }

        if (
                !Double.isFinite(parsedValue)
                        || parsedValue <= 0
        ) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must be greater than zero."
            );
        }

        return parsedValue;
    }

    private String getCellValue(
            Row row,
            int index
    ) {
        Cell cell =
                row.getCell(index);

        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING ->
                    cell.getStringCellValue()
                            .trim();

            case NUMERIC -> {
                double value =
                        cell.getNumericCellValue();

                if (
                        value
                                == Math.floor(value)
                ) {
                    yield String.valueOf(
                            (long) value
                    );
                }

                yield String.valueOf(value);
            }

            case BOOLEAN ->
                    String.valueOf(
                            cell.getBooleanCellValue()
                    );

            case FORMULA ->
                    cell.getCellFormula();

            default -> "";
        };
    }

    private String clean(
            String value
    ) {
        return value == null
                ? ""
                : value.trim();
    }

    private String errorMessage(
            Exception exception
    ) {
        String message =
                exception.getMessage();

        return message == null
                || message.isBlank()
                ? "Invalid data."
                : message;
    }
}