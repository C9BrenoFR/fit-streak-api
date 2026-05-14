package br.ufjf.fsapi.model.repository;

import br.ufjf.fsapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
