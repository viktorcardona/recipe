package abn.recipe.api.controllers;

import abn.recipe.core.AbstractRecipeServiceTests;
import abn.recipe.spec.spec.DishTypeDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

class DishTypeControllerTest extends AbstractRecipeServiceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllDishTypes_shouldReturnOkAndAllDishTypes() throws Exception {
        var expectedDishTypes = Arrays.stream(DishTypeDto.values())
                .map(Enum::name)
                .toList();

        String expectedJson = objectMapper.writeValueAsString(expectedDishTypes);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/dish-types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(expectedJson));

    }

}