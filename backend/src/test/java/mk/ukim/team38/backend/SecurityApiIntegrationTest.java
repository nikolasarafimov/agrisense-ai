package mk.ukim.team38.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void protectedEndpointWithoutTokenShouldReturnUnauthorized()
            throws Exception {

        mockMvc.perform(
                        get("/api/crops")
                )
                .andExpect(
                        status().isUnauthorized()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(401)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Unauthorized")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Authentication is required."
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value("/api/crops")
                );
    }

    @Test
    void regularUserShouldNotAccessAdminEndpoint()
            throws Exception {

        String token =
                registerAndGetToken(
                        "security-user@example.com"
                );

        mockMvc.perform(
                        get("/api/admin/users")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(
                        status().isForbidden()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(403)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Forbidden")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "You do not have permission to access this resource."
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value(
                                        "/api/admin/users"
                                )
                );
    }

    @Test
    void invalidRegistrationShouldReturnBadRequest()
            throws Exception {

        String requestBody = """
                {
                  "fullName": "",
                  "email": "not-an-email",
                  "password": "123"
                }
                """;

        mockMvc.perform(
                        post("/api/users/register")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Bad Request")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Validation failed."
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.fullName"
                        )
                                .value(
                                        "Full name is required."
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.email"
                        )
                                .value(
                                        "Email must be valid."
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.password"
                        )
                                .value(
                                        "Password must contain between 6 and 72 characters."
                                )
                );
    }

    @Test
    void invalidLoginShouldReturnUnauthorized()
            throws Exception {

        registerAndGetToken(
                "login-test@example.com"
        );

        String requestBody = """
                {
                  "email": "login-test@example.com",
                  "password": "wrongpw"
                }
                """;

        mockMvc.perform(
                        post("/api/users/login")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isUnauthorized()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(401)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Unauthorized")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Invalid email or password."
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value(
                                        "/api/users/login"
                                )
                );
    }

    @Test
    void duplicateRegistrationShouldReturnConflict()
            throws Exception {

        registerAndGetToken(
                "duplicate@example.com"
        );

        String requestBody = """
                {
                  "fullName": "Duplicate User",
                  "email": "duplicate@example.com",
                  "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/api/users/register")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(409)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Conflict")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "User with this email already exists."
                                )
                );
    }

    @Test
    void missingOwnedResourceShouldReturnNotFound()
            throws Exception {

        String token =
                registerAndGetToken(
                        "not-found@example.com"
                );

        mockMvc.perform(
                        get("/api/crops/999999")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Not Found")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Crop not found with id: 999999"
                                )
                );
    }

    @Test
    void malformedDateShouldReturnBadRequest()
            throws Exception {

        String token =
                registerAndGetToken(
                        "date-test@example.com"
                );

        String requestBody = """
                {
                  "name": "Wheat",
                  "type": "Grain",
                  "plantingDate": "2026-99-99"
                }
                """;

        mockMvc.perform(
                        post("/api/crops")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Bad Request")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Request body contains invalid or incorrectly formatted data."
                                )
                );
    }

    private String registerAndGetToken(
            String email
    ) throws Exception {

        String requestBody =
                """
                {
                  "fullName": "Test User",
                  "email": "%s",
                  "password": "password123"
                }
                """.formatted(email);

        String responseBody =
                mockMvc.perform(
                                post(
                                        "/api/users/register"
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                requestBody
                                        )
                        )
                        .andExpect(
                                status().isOk()
                        )
                        .andExpect(
                                jsonPath("$.token")
                                        .isNotEmpty()
                        )
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode response =
                objectMapper.readTree(
                        responseBody
                );

        return response
                .get("token")
                .asText();
    }

    @Test
    void registrationShouldRejectSameEmailWithDifferentCase()
            throws Exception {

        registerAndGetToken(
                "case-test@example.com"
        );

        String requestBody = """
            {
              "fullName": "Another User",
              "email": "CASE-TEST@EXAMPLE.COM",
              "password": "password123"
            }
            """;

        mockMvc.perform(
                        post("/api/users/register")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(409)
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "User with this email already exists."
                                )
                );
    }

    @Test
    void loginShouldBeCaseInsensitiveForEmail()
            throws Exception {

        registerAndGetToken(
                "mixed-case@example.com"
        );

        String requestBody = """
            {
              "email": "MIXED-CASE@EXAMPLE.COM",
              "password": "password123"
            }
            """;

        mockMvc.perform(
                        post("/api/users/login")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.token")
                                .isNotEmpty()
                )
                .andExpect(
                        jsonPath("$.email")
                                .value(
                                        "mixed-case@example.com"
                                )
                );
    }
}