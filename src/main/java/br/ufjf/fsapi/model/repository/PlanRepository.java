package br.ufjf.fsapi.model.repository;

import br.ufjf.fsapi.model.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
}
