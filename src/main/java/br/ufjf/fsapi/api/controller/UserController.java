package br.ufjf.fsapi.api.controller;

import br.ufjf.fsapi.api.dto.UserDTO;
import br.ufjf.fsapi.api.dto.WorkoutDTO;
import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.User;
import br.ufjf.fsapi.model.entity.Workout;
import br.ufjf.fsapi.service.UserService;
import br.ufjf.fsapi.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {
    private final UserService service;
    private final WorkoutService workoutService;

    @GetMapping()
    public ResponseEntity get(){
        List<User> users = service.getAll();
        return ResponseEntity.ok(users.stream().map(UserDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity find(@PathVariable("id") Long id){
        Optional<User> user = service.getById(id);
        if(!user.isPresent()){
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(user.map(UserDTO::create));
    }

    @GetMapping("/{id}/workouts")
    public ResponseEntity getWorkouts(@PathVariable("id") Long id){
        Optional<User> user = service.getById(id);
        if(!user.isPresent()){
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }

        List<Workout> workouts = workoutService.getByUser(user);
        return ResponseEntity.ok(workouts.stream().map(WorkoutDTO::create).collect(Collectors.toList()));
    }

    @PostMapping()
    public ResponseEntity store(@RequestBody UserDTO dto){
        try {
            User user = convert(dto);
            user = service.save(user);
            return new ResponseEntity(user, HttpStatus.CREATED);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") Long id,@RequestBody UserDTO dto){
        if(!service.getById(id).isPresent()){
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            User user = convert(dto);
            user.setId(id);
            user = service.save(user);
            return new ResponseEntity(user, HttpStatus.CREATED);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public User convert(UserDTO dto){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, User.class);
    }
}
