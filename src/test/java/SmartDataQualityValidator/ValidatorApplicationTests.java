
package SmartDataQualityValidator;

import com.dataquality.validator.controller.DataQualityController;
import com.dataquality.validator.repository.ValidationRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DataQualityController.class)
@Import(DataQualityController.class)
class ValidatorApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ValidationRepository validationRepository;

    @Test
    void healthEndpointShouldReturnSuccess() throws Exception {
        mockMvc.perform(get("/api/vehicle/health"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Vehicle Number Plate Validator is running!"
                ));
    }

    @Test
    void validNumberPlateShouldBeAccepted() throws Exception {
        mockMvc.perform(post("/api/vehicle/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"AP39AB1234\""))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Valid Number Plate: AP39AB1234"
                ));

        verify(validationRepository).save(any());
    }

    @Test
    void invalidNumberPlateShouldBeRejected() throws Exception {
        mockMvc.perform(post("/api/vehicle/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"INVALID123\""))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Invalid Number Plate: INVALID123"
                ));

        verify(validationRepository).save(any());
    }

    @Test
    void lowercaseNumberPlateShouldBeNormalized() throws Exception {
        mockMvc.perform(post("/api/vehicle/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"ap39ab1234\""))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Valid Number Plate: AP39AB1234"
                ));
    }

    @Test
    void statisticsShouldReturnCounts() throws Exception {
        when(validationRepository.count()).thenReturn(5L);
        when(validationRepository.countByValidTrue()).thenReturn(3L);
        when(validationRepository.countByValidFalse()).thenReturn(2L);

        mockMvc.perform(get("/api/vehicle/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(5))
                .andExpect(jsonPath("$.valid").value(3))
                .andExpect(jsonPath("$.invalid").value(2));
    }

    @Test
    void historyShouldReturnRecords() throws Exception {
        when(validationRepository.findAll())
                .thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/vehicle/history"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void deleteHistoryShouldClearRecords() throws Exception {
        mockMvc.perform(delete("/api/vehicle/history"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Validation history cleared successfully"
                ));

        verify(validationRepository).deleteAll();
    }
}
