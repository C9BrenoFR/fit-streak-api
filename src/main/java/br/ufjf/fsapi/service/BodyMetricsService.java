package br.ufjf.fsapi.service;

import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.BodyMetrics;
import br.ufjf.fsapi.model.entity.User;
import br.ufjf.fsapi.model.repository.BodyMetricsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BodyMetricsService {
    private BodyMetricsRepository repository;

    public BodyMetricsService(BodyMetricsRepository repository) {
        this.repository = repository;
    }

    public List<BodyMetrics> getAll(){
        return repository.findAll();
    }

    public Optional<BodyMetrics> getById(Long id){
        return repository.findById(id);
    }

    public List<BodyMetrics> getByUser(Optional<User> user){
        return repository.findByUser(user);
    }

    @Transactional
    public BodyMetrics save(BodyMetrics bodyMetrics){
        validate(bodyMetrics);
        bodyMetrics.setImc(bodyMetrics.getWeight() / Math.pow(bodyMetrics.getHeight() , 2));
        return repository.save(bodyMetrics);
    }

    @Transactional
    public void destroy(BodyMetrics bodyMetrics){
        Objects.requireNonNull(bodyMetrics.getId());
        repository.delete(bodyMetrics);
    }

    public void validate(BodyMetrics bodyMetrics){
        if ( bodyMetrics.getWeight() <= 0 )
        {
            throw new BusinessRuleException("Peso invalido.");
        }
        if(bodyMetrics.getHeight()<=0)
        {
            throw new BusinessRuleException("Altura invalida.");
        }
        if(bodyMetrics.getBodyFatPercentage()<=0 || bodyMetrics.getBodyFatPercentage() >=100)
        {
            throw new BusinessRuleException("Porcentagem de gordura corporal invalida");
        }
        if(bodyMetrics.getLeanMass()<= 0)
        {
            throw new BusinessRuleException("Massa magra invalida.");
        }
        if(bodyMetrics.getBodyWater()<=0 || bodyMetrics.getBodyWater() >=100)
        {
            throw new BusinessRuleException("Água corporal invalida.");
        }
        if(bodyMetrics.getVisceralFat()<=0 || bodyMetrics.getVisceralFat() >= 60){
            throw new BusinessRuleException("Gordura viceral invalida");
        }
    }


}
