package mk.ukim.team38.backend;

import mk.ukim.team38.backend.controller.DataController;
import mk.ukim.team38.backend.repository.UserRepository;
import mk.ukim.team38.backend.service.ExportService;
import mk.ukim.team38.backend.service.ImportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import mk.ukim.team38.backend.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DataController.class)
class DataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExportService exportService;

    @MockBean
    private ImportService importService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testExportCrops() throws Exception {
        User user = new User(
                1L,
                "John",
                "john@example.com",
                "pass",
                "USER"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(exportService.exportCropsCsv(user))
                .thenReturn("id,name,type,plantingDate\n1,Wheat,Grain,2024-01-01\n");

        mockMvc.perform(get("/api/data/export/crops").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(
                        header().string(
                                "Content-Disposition",
                                "attachment; filename=\"crops.csv\""
                        )
                )
                .andExpect(header().string("Content-Type", "text/csv;charset=UTF-8"))
                .andExpect(content().string("id,name,type,plantingDate\n1,Wheat,Grain,2024-01-01\n"));
    }

    @Test
    void testImportCrops() throws Exception {
        User user = new User(
                1L,
                "John",
                "john@example.com",
                "pass",
                "USER"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Map<String, Object> result = Map.of(
                "imported", 1,
                "skipped", 0,
                "errors", List.of()
        );

        when(importService.importCropsCsv(any(), eq(user))).thenReturn(result);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "crops.csv",
                "text/csv",
                "id,name,type,plantingDate\n1,Corn,Vegetable,2024-04-01\n".getBytes()
        );

        mockMvc.perform(multipart("/api/data/import/crops")
                        .file(file)
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported").value(1))
                .andExpect(jsonPath("$.skipped").value(0));
    }
}