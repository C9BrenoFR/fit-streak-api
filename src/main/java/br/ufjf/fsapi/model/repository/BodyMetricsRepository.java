package br.ufjf.fsapi.model.repository;

import br.ufjf.fsapi.model.entity.BodyMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyMetricsRepository extends JpaRepository<BodyMetrics, Long> {
}
