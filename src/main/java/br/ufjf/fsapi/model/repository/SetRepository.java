package br.ufjf.fsapi.model.repository;

import br.ufjf.fsapi.model.entity.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SetRepository extends JpaRepository<Set, Long> {
}
