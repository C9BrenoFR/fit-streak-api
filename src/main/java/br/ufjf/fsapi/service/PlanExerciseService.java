package br.ufjf.fsapi.service;

import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.Plan;
import br.ufjf.fsapi.model.entity.PlanExercise;
import br.ufjf.fsapi.model.repository.PlanExerciseRepository;
import br.ufjf.fsapi.model.repository.PlanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PlanExerciseService {
    private PlanExerciseRepository repository;

    public PlanExerciseService(PlanExerciseRepository repository) {
        this.repository = repository;
    }

    public List<PlanExercise> getAll(){
        return repository.findAll();
    }

    public Optional<PlanExercise> getById(Long id){
        return repository.findById(id);
    }

    public List<PlanExercise> getByPlan (Optional<Plan> plan){
        return repository.findByPlan(plan);
    }

    @Transactional
    public PlanExercise save(PlanExercise planExercise)
    {
        validate(planExercise);
        return repository.save(planExercise);
    }

    @Transactional
    public void destroy(PlanExercise planExercise)
    {
        Objects.requireNonNull(planExercise.getId());
        repository.delete(planExercise);
    }



    public void validate(PlanExercise planExercise)
    {
        if( planExercise.getExerciseType() == null || planExercise.getExerciseType().trim().equals(" "))
        {
            throw new BusinessRuleException("Tipo de exercício inválido.");
        }
        if( planExercise.getRestTime() <= 0 )
        {
            throw new BusinessRuleException("Tempo de descanço inválido.");
        }
        if( planExercise.getSetsNumber() <= 0 ){
            throw new BusinessRuleException("Número de séries inválido.");
        }
        if( planExercise.getRepsNumber()<=0 ){
            throw new BusinessRuleException("Número de repetições inválido.");
        }
        if( planExercise.getPlan() == null )
        {
            throw new BusinessRuleException("Plano inválido");
        }
        if( planExercise.getExercise() == null )
        {
            throw new BusinessRuleException("Exercicio inválido");
        }
    }
}
