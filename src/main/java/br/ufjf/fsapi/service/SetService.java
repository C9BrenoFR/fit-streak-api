package br.ufjf.fsapi.service;

import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.PlanExercise;
import br.ufjf.fsapi.model.entity.Set;
import br.ufjf.fsapi.model.repository.SetRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SetService {
    private SetRepository repository;

    public List<Set> getByPlanExercise (Optional<PlanExercise> planExercise){
        return repository.findByPlanExercise(planExercise);
    }

    @Transactional
    public Set save(Set set){
        validate(set);
        return repository.save(set);
    }

    @Transactional
    public void destroy(Set set){
        Objects.requireNonNull(set.getId());
        repository.delete(set);
    }


    public void validate(Set set){
        if( set.getReps() <= 0)
        {
            throw new BusinessRuleException("Repetições inválidas.");
        }
        if( set.getLoad() < 0 )
        {
            throw new BusinessRuleException("Carga inválida.");
        }
        if( set.getLoadType() == null || set.getLoadType().trim().equals(" "))
        {
            throw new BusinessRuleException("Tipo de Carga inválida.");
        }
        if( set.getPlanExercise() == null)
        {
            throw new BusinessRuleException("Plano de exercício inválido.");
        }
    }
}
