package br.ufjf.fsapi.model.repository;

import br.ufjf.fsapi.model.entity.Plan;
import br.ufjf.fsapi.model.entity.PlanExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanExerciseRepository extends JpaRepository<PlanExercise, Long> {
    List<PlanExercise> findByPlan (Optional<Plan> plan);
}
