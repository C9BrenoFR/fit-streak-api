package br.ufjf.fsapi.model.repository;

import br.ufjf.fsapi.model.entity.DayHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayHistoryRepository extends JpaRepository<DayHistory, Long> {
}
