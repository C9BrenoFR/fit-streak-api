package br.ufjf.fsapi.service;

import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.DayHistory;
import br.ufjf.fsapi.model.entity.User;
import br.ufjf.fsapi.model.repository.DayHistoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DayHistoryService {
    private DayHistoryRepository repository;

    public DayHistoryService(DayHistoryRepository repository) {
        this.repository = repository;
    }

    public List<DayHistory> getByUser (Optional<User> user){
        return repository.findByUser(user);
    }

    public Optional<DayHistory> getById(Long id){ return repository.findById(id); }

    @Transactional
    public DayHistory save(DayHistory dayHistory){
        validate(dayHistory);
        dayHistory.setDate(LocalDate.now());
        return repository.save(dayHistory);
    }

    @Transactional
    public void destroy(DayHistory dayHistory){
        Objects.requireNonNull(dayHistory.getId());
        repository.delete(dayHistory);
    }

    public void validate(DayHistory dayHistory)
    {
        if(dayHistory.getPercentage() > 100 || dayHistory.getPercentage() <= 0)
        {
            throw new BusinessRuleException("Porcentagem inválida.");
        }
    }
}
