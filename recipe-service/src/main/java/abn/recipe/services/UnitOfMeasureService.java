package abn.recipe.services;

import abn.recipe.mappers.UnitOfMeasureMapper;
import abn.recipe.persistence.repositories.UnitOfMeasureRepository;
import abn.recipe.spec.spec.UnitOfMeasureDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UnitOfMeasureService {

    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final UnitOfMeasureMapper unitOfMeasureMapper;

    public List<UnitOfMeasureDto> getUnitsOfMeasure() {
        return unitOfMeasureRepository.findAllByOrderByNameAsc().stream()
                .map(unitOfMeasureMapper::entityToDto)
                .toList();
    }
}
