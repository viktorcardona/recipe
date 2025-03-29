package abn.recipe.api.controllers;

import abn.recipe.services.UnitOfMeasureService;
import abn.recipe.spec.api.UnitsOfMeasureApi;
import abn.recipe.spec.spec.UnitOfMeasureDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class UnitOfMeasureController implements UnitsOfMeasureApi {

    private final UnitOfMeasureService service;

    @Override
    public ResponseEntity<List<UnitOfMeasureDto>> getAllUnitsOfMeasure() {
        return ResponseEntity.ok(service.getUnitsOfMeasure());
    }

}
