package br.ufjf.fsapi.model.repository;

import br.ufjf.fsapi.model.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
