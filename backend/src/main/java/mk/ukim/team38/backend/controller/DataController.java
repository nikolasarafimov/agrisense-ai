package mk.ukim.team38.backend.controller;

import lombok.RequiredArgsConstructor;
import mk.ukim.team38.backend.model.User;
import mk.ukim.team38.backend.security.AuthenticatedUserService;
import mk.ukim.team38.backend.service.ExportService;
import mk.ukim.team38.backend.service.ImportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataController {

    private final ExportService exportService;
    private final ImportService importService;
    private final AuthenticatedUserService authenticatedUserService;

    @GetMapping("/export/crops")
    public ResponseEntity<byte[]> exportCrops() {
        User user = authenticatedUserService.getCurrentUser();

        String csv = exportService.exportCropsCsv(user);

        return ResponseEntity.ok()
                .headers(csvHeaders("crops.csv"))
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping("/import/crops")
    public ResponseEntity<Map<String, Object>> importCrops(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        User user = authenticatedUserService.getCurrentUser();

        return ResponseEntity.ok(
                importService.importCropsCsv(file, user)
        );
    }

    @GetMapping("/export/parcels")
    public ResponseEntity<byte[]> exportParcels() {
        User user = authenticatedUserService.getCurrentUser();

        String csv = exportService.exportParcelsCsv(user);

        return ResponseEntity.ok()
                .headers(csvHeaders("parcels.csv"))
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping("/import/parcels")
    public ResponseEntity<Map<String, Object>> importParcels(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        User user = authenticatedUserService.getCurrentUser();

        return ResponseEntity.ok(
                importService.importParcelsCsv(file, user)
        );
    }

    @GetMapping("/export/activities")
    public ResponseEntity<byte[]> exportActivities() {
        User user = authenticatedUserService.getCurrentUser();

        String csv = exportService.exportActivitiesCsv(user);

        return ResponseEntity.ok()
                .headers(csvHeaders("activities.csv"))
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping("/import/activities")
    public ResponseEntity<Map<String, Object>> importActivities(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        User user = authenticatedUserService.getCurrentUser();

        return ResponseEntity.ok(
                importService.importActivitiesCsv(file, user)
        );
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportAllExcel()
            throws IOException {

        User user = authenticatedUserService.getCurrentUser();

        byte[] excelData =
                exportService.exportAllToExcel(user);

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
        );

        headers.setContentDisposition(
                ContentDisposition
                        .attachment()
                        .filename("agriculture-data.xlsx")
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    @PostMapping("/import/excel")
    public ResponseEntity<Map<String, Object>> importExcel(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        User user = authenticatedUserService.getCurrentUser();

        return ResponseEntity.ok(
                importService.importFromExcel(file, user)
        );
    }

    private HttpHeaders csvHeaders(String filename) {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.parseMediaType(
                        "text/csv; charset=UTF-8"
                )
        );

        headers.setContentDisposition(
                ContentDisposition
                        .attachment()
                        .filename(filename)
                        .build()
        );

        return headers;
    }
}