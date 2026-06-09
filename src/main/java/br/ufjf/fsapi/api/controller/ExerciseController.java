package br.ufjf.fsapi.api.controller;

import br.ufjf.fsapi.api.dto.ExerciseDTO;
import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.Exercise;
import br.ufjf.fsapi.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
@CrossOrigin
public class ExerciseController {
    private final ExerciseService service;

    @GetMapping()
    public ResponseEntity get(){
        List<Exercise> exercises = service.getAll();
        return ResponseEntity.ok(exercises.stream().map(ExerciseDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity find(@PathVariable("id") Long id){
        Optional<Exercise> exercise = service.getById(id);
        if(!exercise.isPresent()){
            return new ResponseEntity("Exercício não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(exercise.map(ExerciseDTO::create));
    }

    @PostMapping()
    public ResponseEntity store(@RequestBody ExerciseDTO dto){
        try {
            Exercise exercise = convert(dto);
            exercise = service.save(exercise);
            return new ResponseEntity(exercise, HttpStatus.CREATED);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody ExerciseDTO dto){
        if(!service.getById(id).isPresent()){
            return new ResponseEntity("Exercício não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Exercise exercise = convert(dto);
            exercise.setId(id);
            exercise = service.save(exercise);
            return new ResponseEntity(exercise, HttpStatus.OK);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id){
        Optional<Exercise> exercise = service.getById(id);
        if(!exercise.isPresent()){
            return new ResponseEntity("Exercício não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.destroy(exercise.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Exercise convert(ExerciseDTO dto){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Exercise.class);
    }
}
