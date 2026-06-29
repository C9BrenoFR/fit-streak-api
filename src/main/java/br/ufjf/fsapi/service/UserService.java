package br.ufjf.fsapi.service;

import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.User;
import br.ufjf.fsapi.model.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private PasswordEncoder encoder;

    @Autowired
    private UserRepository repository;

    public UserService(UserRepository repository){
        this.repository = repository;
    }

    public List<User> getAll(){
        return repository.findAll();
    }

    public Optional<User> getById(Long id){ return repository.findById(id); }

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

    public UserDetails authenticate(User user){
        UserDetails userDetails = loadUserByUsername(user.getEmail());
        boolean isPasswordCorrect = encoder.matches(user.getPassword(), userDetails.getPassword());

        if (isPasswordCorrect){
            return userDetails;
        }
        throw new BusinessRuleException("Senha inválida");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        String[] roles = user.isAdmin()
                ? new String[]{"ADMIN", "USER"}
                : new String[]{"USER"};

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(roles)
                .build();
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
