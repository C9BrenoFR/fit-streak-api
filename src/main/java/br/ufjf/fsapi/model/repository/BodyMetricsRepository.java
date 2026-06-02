package br.ufjf.fsapi.model.repository;

import br.ufjf.fsapi.model.entity.BodyMetrics;
import br.ufjf.fsapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BodyMetricsRepository extends JpaRepository<BodyMetrics, Long> {
    List<BodyMetrics> findByUser (Optional<User> user);
}
