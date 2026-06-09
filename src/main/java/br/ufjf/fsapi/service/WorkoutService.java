package br.ufjf.fsapi.service;

import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.User;
import br.ufjf.fsapi.model.entity.Workout;
import br.ufjf.fsapi.model.repository.WorkoutRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class WorkoutService {
    private WorkoutRepository repository;

    public WorkoutService(WorkoutRepository repository) {
        this.repository = repository;
    }

    public List<Workout> getByUser (Optional<User> user){
        return repository.findByUser(user);
    }

    public Optional<Workout> getById (Long id) { return repository.findById(id); }

    @Transactional
    public Workout save(Workout workout){
        validate(workout);
        return repository.save(workout);
    }

    @Transactional
    public void destroy(Workout workout)
    {
        Objects.requireNonNull(workout.getId());
        repository.delete(workout);
    }

    public void validate(Workout workout){
        if( workout.getName() == null || workout.getName().trim().equals(" "))
        {
            throw new BusinessRuleException("Nome inválido");
        }
        if( workout.getDescription() == null || workout.getDescription().trim().equals(" ") )
        {
            throw new BusinessRuleException("Descrição inválida");
        }
        if( workout.getUser() == null)
        {
            throw new BusinessRuleException("Usuário inválido");
        }
    }
}
