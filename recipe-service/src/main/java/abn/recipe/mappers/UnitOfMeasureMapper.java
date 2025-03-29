package abn.recipe.mappers;

import abn.recipe.persistence.entity.UnitOfMeasure;
import abn.recipe.spec.spec.UnitOfMeasureDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UnitOfMeasureMapper {

    UnitOfMeasureDto entityToDto(UnitOfMeasure entity);
}
