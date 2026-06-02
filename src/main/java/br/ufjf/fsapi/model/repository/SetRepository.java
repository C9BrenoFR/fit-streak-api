package br.ufjf.fsapi.model.repository;

import br.ufjf.fsapi.model.entity.PlanExercise;
import br.ufjf.fsapi.model.entity.Set;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SetRepository extends JpaRepository<Set, Long> {
    List<Set> findByPlanExercise (Optional<PlanExercise> planExercise);
}
