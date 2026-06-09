package br.ufjf.fsapi.api.controller;

import br.ufjf.fsapi.api.dto.PlanDTO;
import br.ufjf.fsapi.api.dto.PlanExerciseDTO;
import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.Plan;
import br.ufjf.fsapi.model.entity.PlanExercise;
import br.ufjf.fsapi.model.entity.Workout;
import br.ufjf.fsapi.service.PlanExerciseService;
import br.ufjf.fsapi.service.PlanService;
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
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@CrossOrigin
public class PlanController {
    private final PlanService service;
    private final WorkoutService workoutService;
    private final PlanExerciseService planExerciseService;

    @GetMapping()
    public ResponseEntity get(){
        List<Plan> plans = service.getAll();
        return ResponseEntity.ok(plans.stream().map(PlanDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity find(@PathVariable("id") Long id){
        Optional<Plan> plan = service.getById(id);
        if(!plan.isPresent()){
            return new ResponseEntity("Plano não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(plan.map(PlanDTO::create));
    }

    @GetMapping("/{id}/exercises")
    public ResponseEntity getExercises(@PathVariable("id") Long id){
        Optional<Plan> plan = service.getById(id);
        if(!plan.isPresent()){
            return new ResponseEntity("Plano não encontrado", HttpStatus.NOT_FOUND);
        }

        List<PlanExercise> planExercises = planExerciseService.getByPlan(plan);
        return ResponseEntity.ok(planExercises.stream().map(PlanExerciseDTO::create).collect(Collectors.toList()));
    }

    @PostMapping()
    public ResponseEntity store(@RequestBody PlanDTO dto){
        try {
            Plan plan = convert(dto);
            Workout workout = workoutService.getById(dto.getIdWorkout()).orElseThrow(() -> new BusinessRuleException("Treino não encontrado"));
            plan.setWorkout(workout);
            plan = service.save(plan);
            return new ResponseEntity(plan, HttpStatus.CREATED);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody PlanDTO dto){
        if(!service.getById(id).isPresent()){
            return new ResponseEntity("Plano não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Plan plan = convert(dto);
            Workout workout = workoutService.getById(dto.getIdWorkout()).orElseThrow(() -> new BusinessRuleException("Treino não encontrado"));
            plan.setWorkout(workout);
            plan.setId(id);
            plan = service.save(plan);
            return new ResponseEntity(plan, HttpStatus.OK);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id){
        Optional<Plan> plan = service.getById(id);
        if(!plan.isPresent()){
            return new ResponseEntity("Plano não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.destroy(plan.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Plan convert(PlanDTO dto){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Plan.class);
    }
}
