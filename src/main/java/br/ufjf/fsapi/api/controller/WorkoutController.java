package br.ufjf.fsapi.api.controller;

import br.ufjf.fsapi.api.dto.PlanDTO;
import br.ufjf.fsapi.api.dto.WorkoutDTO;
import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.Plan;
import br.ufjf.fsapi.model.entity.User;
import br.ufjf.fsapi.model.entity.Workout;
import br.ufjf.fsapi.service.PlanService;
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
@RequestMapping("/api/workout")
@RequiredArgsConstructor
@CrossOrigin
public class WorkoutController {
    private final WorkoutService service;
    private final UserService userService;
    private final PlanService planService;

    @GetMapping("/{id}")
    public ResponseEntity find(@PathVariable("id") Long id){
        Optional<Workout> workout = service.getById(id);
        if(!workout.isPresent()){
            return new ResponseEntity("Treino não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(workout.map(WorkoutDTO::create));
    }

    @GetMapping("/{id}/plans")
    public ResponseEntity getPlans(@PathVariable("id") Long id){
        Optional<Workout> workout = service.getById(id);
        if(!workout.isPresent()){
            return new ResponseEntity("Treino não encontrado", HttpStatus.NOT_FOUND);
        }

        List<Plan> plans = planService.getByWorkout(workout);
        return ResponseEntity.ok(plans.stream().map(PlanDTO::create).collect(Collectors.toList()));
    }

    @PostMapping()
    public ResponseEntity store(@RequestBody WorkoutDTO dto){
        try{
            Workout workout = convert(dto);
            User user = userService.getById(dto.getIdUser()).orElseThrow(() -> new BusinessRuleException("Usuário não encontrado"));
            User creator = userService.getById(dto.getIdCreator()).orElseThrow(() -> new BusinessRuleException("Criador não encontrado"));
            workout.setUser(user);
            workout.setCreator(creator);
            workout = service.save(workout);
            return new ResponseEntity(workout, HttpStatus.CREATED);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody WorkoutDTO dto){
        if(!service.getById(id).isPresent()){
            return new ResponseEntity("Treino não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Workout workout = convert(dto);
            User user = userService.getById(dto.getIdUser()).orElseThrow(() -> new BusinessRuleException("Usuário não encontrado"));
            User creator = userService.getById(dto.getIdCreator()).orElseThrow(() -> new BusinessRuleException("Criador não encontrado"));
            workout.setUser(user);
            workout.setCreator(creator);
            workout.setId(id);
            workout = service.save(workout);
            return new ResponseEntity(workout, HttpStatus.OK);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id){
        Optional<Workout> workout = service.getById(id);
        if(!workout.isPresent()){
            return new ResponseEntity("Treino não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.destroy(workout.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Workout convert(WorkoutDTO dto){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Workout.class);
    }
}
