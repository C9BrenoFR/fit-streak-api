package br.ufjf.fsapi.model.repository;

import br.ufjf.fsapi.model.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
}
