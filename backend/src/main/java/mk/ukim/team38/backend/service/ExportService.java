package mk.ukim.team38.backend.service;

import mk.ukim.team38.backend.model.Activity;
import mk.ukim.team38.backend.model.Crop;
import mk.ukim.team38.backend.model.Parcel;
import mk.ukim.team38.backend.model.User;
import mk.ukim.team38.backend.repository.ActivityRepository;
import mk.ukim.team38.backend.repository.CropRepository;
import mk.ukim.team38.backend.repository.ParcelRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE;

    private final CropRepository cropRepository;
    private final ParcelRepository parcelRepository;
    private final ActivityRepository activityRepository;

    public ExportService(
            CropRepository cropRepository,
            ParcelRepository parcelRepository,
            ActivityRepository activityRepository
    ) {
        this.cropRepository = cropRepository;
        this.parcelRepository = parcelRepository;
        this.activityRepository = activityRepository;
    }

    public String exportCropsCsv(
            User user
    ) {
        List<Crop> crops =
                cropRepository.findByUser(
                        user
                );

        StringBuilder sb =
                new StringBuilder();

        sb.append(
                "id,name,type,plantingDate\n"
        );

        for (Crop crop : crops) {
            sb.append(
                            value(crop.getId())
                    )
                    .append(",")
                    .append(
                            escapeCsv(
                                    crop.getName()
                            )
                    )
                    .append(",")
                    .append(
                            escapeCsv(
                                    crop.getType()
                            )
                    )
                    .append(",")
                    .append(
                            formatDate(
                                    crop.getPlantingDate()
                            )
                    )
                    .append("\n");
        }

        return sb.toString();
    }

    public String exportParcelsCsv(
            User user
    ) {
        List<Parcel> parcels =
                parcelRepository.findByUser(
                        user
                );

        StringBuilder sb =
                new StringBuilder();

        sb.append(
                "id,location,size,soilType\n"
        );

        for (Parcel parcel : parcels) {
            sb.append(
                            value(parcel.getId())
                    )
                    .append(",")
                    .append(
                            escapeCsv(
                                    parcel.getLocation()
                            )
                    )
                    .append(",")
                    .append(
                            value(
                                    parcel.getSize()
                            )
                    )
                    .append(",")
                    .append(
                            escapeCsv(
                                    parcel.getSoilType()
                            )
                    )
                    .append("\n");
        }

        return sb.toString();
    }

    public String exportActivitiesCsv(
            User user
    ) {
        List<Activity> activities =
                activityRepository.findByUser(
                        user
                );

        StringBuilder sb =
                new StringBuilder();

        sb.append(
                "id,description,date,type\n"
        );

        for (Activity activity : activities) {
            sb.append(
                            value(activity.getId())
                    )
                    .append(",")
                    .append(
                            escapeCsv(
                                    activity.getDescription()
                            )
                    )
                    .append(",")
                    .append(
                            formatDate(
                                    activity.getDate()
                            )
                    )
                    .append(",")
                    .append(
                            escapeCsv(
                                    activity.getType()
                            )
                    )
                    .append("\n");
        }

        return sb.toString();
    }

    public byte[] exportAllToExcel(
            User user
    ) throws IOException {

        try (
                Workbook workbook =
                        new XSSFWorkbook()
        ) {
            createCropsSheet(
                    workbook,
                    cropRepository.findByUser(
                            user
                    )
            );

            createParcelsSheet(
                    workbook,
                    parcelRepository.findByUser(
                            user
                    )
            );

            createActivitiesSheet(
                    workbook,
                    activityRepository.findByUser(
                            user
                    )
            );

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            workbook.write(
                    outputStream
            );

            return outputStream.toByteArray();
        }
    }

    private void createCropsSheet(
            Workbook workbook,
            List<Crop> crops
    ) {
        Sheet sheet =
                workbook.createSheet(
                        "Crops"
                );

        Row header =
                sheet.createRow(0);

        String[] columns = {
                "ID",
                "Name",
                "Type",
                "Planting Date"
        };

        for (
                int i = 0;
                i < columns.length;
                i++
        ) {
            header.createCell(i)
                    .setCellValue(
                            columns[i]
                    );
        }

        int rowIndex = 1;

        for (Crop crop : crops) {
            Row row =
                    sheet.createRow(
                            rowIndex++
                    );

            row.createCell(0)
                    .setCellValue(
                            crop.getId() == null
                                    ? 0
                                    : crop.getId()
                    );

            row.createCell(1)
                    .setCellValue(
                            value(
                                    crop.getName()
                            )
                    );

            row.createCell(2)
                    .setCellValue(
                            value(
                                    crop.getType()
                            )
                    );

            row.createCell(3)
                    .setCellValue(
                            formatDate(
                                    crop.getPlantingDate()
                            )
                    );
        }

        autoSize(
                sheet,
                columns.length
        );
    }

    private void createParcelsSheet(
            Workbook workbook,
            List<Parcel> parcels
    ) {
        Sheet sheet =
                workbook.createSheet(
                        "Parcels"
                );

        Row header =
                sheet.createRow(0);

        String[] columns = {
                "ID",
                "Location",
                "Size",
                "Soil Type"
        };

        for (
                int i = 0;
                i < columns.length;
                i++
        ) {
            header.createCell(i)
                    .setCellValue(
                            columns[i]
                    );
        }

        int rowIndex = 1;

        for (Parcel parcel : parcels) {
            Row row =
                    sheet.createRow(
                            rowIndex++
                    );

            row.createCell(0)
                    .setCellValue(
                            parcel.getId() == null
                                    ? 0
                                    : parcel.getId()
                    );

            row.createCell(1)
                    .setCellValue(
                            value(
                                    parcel.getLocation()
                            )
                    );

            row.createCell(2)
                    .setCellValue(
                            parcel.getSize() == null
                                    ? 0
                                    : parcel.getSize()
                    );

            row.createCell(3)
                    .setCellValue(
                            value(
                                    parcel.getSoilType()
                            )
                    );
        }

        autoSize(
                sheet,
                columns.length
        );
    }

    private void createActivitiesSheet(
            Workbook workbook,
            List<Activity> activities
    ) {
        Sheet sheet =
                workbook.createSheet(
                        "Activities"
                );

        Row header =
                sheet.createRow(0);

        String[] columns = {
                "ID",
                "Description",
                "Date",
                "Type"
        };

        for (
                int i = 0;
                i < columns.length;
                i++
        ) {
            header.createCell(i)
                    .setCellValue(
                            columns[i]
                    );
        }

        int rowIndex = 1;

        for (
                Activity activity : activities
        ) {
            Row row =
                    sheet.createRow(
                            rowIndex++
                    );

            row.createCell(0)
                    .setCellValue(
                            activity.getId() == null
                                    ? 0
                                    : activity.getId()
                    );

            row.createCell(1)
                    .setCellValue(
                            value(
                                    activity.getDescription()
                            )
                    );

            row.createCell(2)
                    .setCellValue(
                            formatDate(
                                    activity.getDate()
                            )
                    );

            row.createCell(3)
                    .setCellValue(
                            value(
                                    activity.getType()
                            )
                    );
        }

        autoSize(
                sheet,
                columns.length
        );
    }

    private void autoSize(
            Sheet sheet,
            int numberOfColumns
    ) {
        for (
                int i = 0;
                i < numberOfColumns;
                i++
        ) {
            sheet.autoSizeColumn(i);
        }
    }

    private String formatDate(
            LocalDate date
    ) {
        return date == null
                ? ""
                : DATE_FORMATTER.format(
                date
        );
    }

    private String escapeCsv(
            String value
    ) {
        if (value == null) {
            return "";
        }

        if (
                value.contains(",")
                        || value.contains("\"")
                        || value.contains("\n")
        ) {
            return "\""
                    + value.replace(
                    "\"",
                    "\"\""
            )
                    + "\"";
        }

        return value;
    }

    private String value(
            Object value
    ) {
        return value == null
                ? ""
                : String.valueOf(value);
    }
}