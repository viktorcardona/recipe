package abn.recipe.persistence.repositories;

import abn.recipe.persistence.entity.UnitOfMeasure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnitOfMeasureRepository extends JpaRepository<UnitOfMeasure, Long> {
    List<UnitOfMeasure> findAllByOrderByNameAsc();
}