package br.ufjf.fsapi.service;

import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.User;
import br.ufjf.fsapi.model.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    private UserRepository repository;

    public List<User> getAll(){
        return repository.findAll();
    }

    @Transactional
    public User save(User user){
        validate(user);
        return repository.save(user);
    }

    @Transactional
    public void destroy(User user){
        Objects.requireNonNull(user.getId());
        repository.delete(user);
    }


    public void validate(User user)
    {
        if( user.getName() == null || user.getName().trim().equals(" ")){
            throw new BusinessRuleException("Nome inválido.");
        }
        if( user.getEmail() == null || user.getEmail().trim().equals(" ")){
            throw new BusinessRuleException("Email inválido");
        }
        if( user.getPassword() == null || user.getPassword().trim().equals(" ")){
            throw new BusinessRuleException("Senha inválida.");
        }
        if(!user.getGender().equals("M") && !user.getGender().equals("F") ){
            throw new BusinessRuleException("Genero inválido");
        }
    }
}
