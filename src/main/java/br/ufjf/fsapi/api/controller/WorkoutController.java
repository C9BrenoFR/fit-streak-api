package br.ufjf.fsapi.api.controller;

import br.ufjf.fsapi.api.dto.UserDTO;
import br.ufjf.fsapi.api.dto.WorkoutDTO;
import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.User;
import br.ufjf.fsapi.model.entity.Workout;
import br.ufjf.fsapi.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/workout")
@RequiredArgsConstructor
@CrossOrigin
public class WorkoutController {
    private final WorkoutService service;

    @GetMapping("/{id}")
    public ResponseEntity find(@PathVariable("id") Long id){
        Optional<Workout> workout = service.getById(id);
        if(!workout.isPresent()){
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(workout.map(WorkoutDTO::create));
    }

    @PostMapping()
    public ResponseEntity store(@RequestBody WorkoutDTO dto){
        try{
            Workout workout = convert(dto);
            workout = service.save(workout);
            return new ResponseEntity(workout, HttpStatus.CREATED);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") Long id,@RequestBody WorkoutDTO dto){
        if(!service.getById(id).isPresent()){
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Workout workout = convert(dto);
            workout.setId(id);
            workout = service.save(workout);
            return new ResponseEntity(workout, HttpStatus.CREATED);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Workout convert(WorkoutDTO dto){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Workout.class);
    }
}
