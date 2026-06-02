package br.ufjf.fsapi.model.repository;

import br.ufjf.fsapi.model.entity.DayHistory;
import br.ufjf.fsapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DayHistoryRepository extends JpaRepository<DayHistory, Long> {
    List<DayHistory> findByUser (Optional<User> user);
}
