package abn.recipe.api.controllers;

import abn.recipe.core.AbstractRecipeServiceTests;
import abn.recipe.spec.spec.UnitOfMeasureDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UnitOfMeasureControllerTest extends AbstractRecipeServiceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllUnitsOfMeasure_shouldReturnListOfUnitsOfMeasure() throws Exception {

        var result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/units")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var responseContent = result.getResponse().getContentAsString();
        List<UnitOfMeasureDto> actualUnits = objectMapper.readValue(responseContent, new TypeReference<>() {
        });

        assertThat(actualUnits).isNotEmpty();
    }

}