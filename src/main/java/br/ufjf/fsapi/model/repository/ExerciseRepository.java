package br.ufjf.fsapi.model.repository;

import br.ufjf.fsapi.model.entity.Exercise;
import br.ufjf.fsapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
