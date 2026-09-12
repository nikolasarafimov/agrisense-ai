package mk.ukim.team38.backend.controller;

import mk.ukim.team38.backend.model.User;
import mk.ukim.team38.backend.repository.UserRepository;
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
public class DataController {

    private final ExportService exportService;
    private final ImportService importService;
    private final UserRepository userRepository;

    public DataController(ExportService exportService,
                          ImportService importService,
                          UserRepository userRepository) {
        this.exportService = exportService;
        this.importService = importService;
        this.userRepository = userRepository;
    }

    @GetMapping("/export/crops")
    public ResponseEntity<byte[]> exportCrops(@RequestParam Long userId) throws IOException {
        User user = getUser(userId);
        String csv = exportService.exportCropsCsv(user);

        HttpHeaders headers = csvHeaders("crops.csv");
        return ResponseEntity.ok()
                .headers(headers)
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping("/import/crops")
    public ResponseEntity<Map<String, Object>> importCrops(@RequestParam("file") MultipartFile file,
                                                           @RequestParam Long userId) throws IOException {
        User user = getUser(userId);
        return ResponseEntity.ok(importService.importCropsCsv(file, user));
    }

    @GetMapping("/export/parcels")
    public ResponseEntity<byte[]> exportParcels(@RequestParam Long userId) throws IOException {
        User user = getUser(userId);
        String csv = exportService.exportParcelsCsv(user);

        HttpHeaders headers = csvHeaders("parcels.csv");
        return ResponseEntity.ok()
                .headers(headers)
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping("/import/parcels")
    public ResponseEntity<Map<String, Object>> importParcels(@RequestParam("file") MultipartFile file,
                                                             @RequestParam Long userId) throws IOException {
        User user = getUser(userId);
        return ResponseEntity.ok(importService.importParcelsCsv(file, user));
    }

    @GetMapping("/export/activities")
    public ResponseEntity<byte[]> exportActivities(@RequestParam Long userId) throws IOException {
        User user = getUser(userId);
        String csv = exportService.exportActivitiesCsv(user);

        HttpHeaders headers = csvHeaders("activities.csv");
        return ResponseEntity.ok()
                .headers(headers)
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping("/import/activities")
    public ResponseEntity<Map<String, Object>> importActivities(@RequestParam("file") MultipartFile file,
                                                                @RequestParam Long userId) throws IOException {
        User user = getUser(userId);
        return ResponseEntity.ok(importService.importActivitiesCsv(file, user));
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportAllExcel(@RequestParam Long userId) throws IOException {
        User user = getUser(userId);
        byte[] excelData = exportService.exportAllToExcel(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ));
        headers.setContentDispositionFormData("attachment", "agriculture_data.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    @PostMapping("/import/excel")
    public ResponseEntity<Map<String, Object>> importExcel(@RequestParam("file") MultipartFile file,
                                                           @RequestParam Long userId) throws IOException {
        User user = getUser(userId);
        return ResponseEntity.ok(importService.importFromExcel(file, user));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " was not found."));
    }

    private HttpHeaders csvHeaders(String filename) {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.parseMediaType("text/csv; charset=UTF-8")
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