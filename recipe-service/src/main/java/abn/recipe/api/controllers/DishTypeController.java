package abn.recipe.api.controllers;

import abn.recipe.spec.api.DishTypesApi;
import abn.recipe.spec.spec.DishTypeDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@AllArgsConstructor
public class DishTypeController implements DishTypesApi {
    @Override
    public ResponseEntity<List<DishTypeDto>> getAllDishTypes() {
        return ResponseEntity.ok(Arrays.stream(DishTypeDto.values()).toList());
    }
}
