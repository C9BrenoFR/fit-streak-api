package br.ufjf.fsapi.service;

import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.Exercise;
import br.ufjf.fsapi.model.repository.ExerciseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ExerciseService {
    private ExerciseRepository repository;

    public List<Exercise> getAll ()
    {
        return repository.findAll();
    }

    @Transactional
    public Exercise save(Exercise exercise){
        validate(exercise);
        return repository.save(exercise);
    }

    @Transactional
    public void destroy(Exercise exercise)
    {
        Objects.requireNonNull(exercise.getId());
        repository.delete(exercise);
    }


    public void validate(Exercise exercise)
    {
        if( exercise.getName() == null || exercise.getName().trim().equals(" "))
        {
            throw new BusinessRuleException("Nome inválido.");
        }
        if( exercise.getMuscularGroup() == null || exercise.getMuscularGroup().trim().equals(" "))
        {
            throw new BusinessRuleException("Grupo muscular inválido.");
        }
        if( exercise.getDescription() == null || exercise.getDescription().trim().equals(" "))
        {
            throw new BusinessRuleException("Descrição inválida.");
        }
    }
}
