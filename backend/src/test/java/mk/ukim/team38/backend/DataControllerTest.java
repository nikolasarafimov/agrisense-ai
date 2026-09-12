package mk.ukim.team38.backend;

import mk.ukim.team38.backend.controller.DataController;
import mk.ukim.team38.backend.model.User;
import mk.ukim.team38.backend.security.AuthenticatedUserService;
import mk.ukim.team38.backend.security.JwtAuthenticationFilter;
import mk.ukim.team38.backend.service.ExportService;
import mk.ukim.team38.backend.service.ImportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DataController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class DataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExportService exportService;

    @MockBean
    private ImportService importService;

    @MockBean
    private AuthenticatedUserService authenticatedUserService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void testExportCrops() throws Exception {
        User user = new User(
                1L,
                "John",
                "john@example.com",
                "pass",
                "USER"
        );

        String csv =
                "id,name,type,plantingDate\n"
                        + "1,Wheat,Grain,2024-01-01\n";

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(exportService.exportCropsCsv(user))
                .thenReturn(csv);

        mockMvc.perform(
                        get("/api/data/export/crops")
                )
                .andExpect(status().isOk())
                .andExpect(
                        header().string(
                                "Content-Disposition",
                                "attachment; filename=\"crops.csv\""
                        )
                )
                .andExpect(
                        header().string(
                                "Content-Type",
                                "text/csv;charset=UTF-8"
                        )
                )
                .andExpect(content().string(csv));
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

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        Map<String, Object> result = Map.of(
                "imported", 1,
                "skipped", 0,
                "errors", List.of()
        );

        when(importService.importCropsCsv(any(), eq(user)))
                .thenReturn(result);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "crops.csv",
                "text/csv",
                (
                        "id,name,type,plantingDate\n"
                                + "1,Corn,Vegetable,2024-04-01\n"
                ).getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(
                        multipart("/api/data/import/crops")
                                .file(file)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported").value(1))
                .andExpect(jsonPath("$.skipped").value(0));
    }
}