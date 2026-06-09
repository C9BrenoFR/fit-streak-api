package br.ufjf.fsapi.service;

import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.Plan;
import br.ufjf.fsapi.model.entity.Workout;
import br.ufjf.fsapi.model.repository.PlanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PlanService {
    private PlanRepository repository;

    public PlanService(PlanRepository repository) {
        this.repository = repository;
    }

    public List<Plan> getAll(){
        return repository.findAll();
    }

    public Optional<Plan> getById(Long id){
        return repository.findById(id);
    }

    public List<Plan> getByWorkout(Optional<Workout> workout)
    {
        return repository.findByWorkout(workout);
    }

    @Transactional
    public Plan save(Plan plan)
    {
        validate(plan);
        return repository.save(plan);
    }

    @Transactional
    public void destroy(Plan plan)
    {
        Objects.requireNonNull(plan.getId());
        repository.delete(plan);
    }

    public void validate(Plan plan){
        if(plan.getName() == null || plan.getName().trim().equals(" "))
        {
            throw new BusinessRuleException("Nome inválido.");
        }
        if(plan.getDescription() == null || plan.getDescription().trim().equals(" "))
        {
            throw new BusinessRuleException("Descrição inválida.");
        }
        if(plan.getWorkout() == null || plan.getWorkout().getId() == null || plan.getWorkout().getId() == 0){
            throw new BusinessRuleException("O plano precisa ser associado em um treino");
        }
    }
}
