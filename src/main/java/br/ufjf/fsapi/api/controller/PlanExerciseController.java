package br.ufjf.fsapi.api.controller;

import br.ufjf.fsapi.api.dto.PlanExerciseDTO;
import br.ufjf.fsapi.api.dto.SetDTO;
import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.Exercise;
import br.ufjf.fsapi.model.entity.Plan;
import br.ufjf.fsapi.model.entity.PlanExercise;
import br.ufjf.fsapi.model.entity.Set;
import br.ufjf.fsapi.service.ExerciseService;
import br.ufjf.fsapi.service.PlanExerciseService;
import br.ufjf.fsapi.service.PlanService;
import br.ufjf.fsapi.service.SetService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/planexercises")
@RequiredArgsConstructor
@CrossOrigin
public class PlanExerciseController {
    private final PlanExerciseService service;
    private final PlanService planService;
    private final ExerciseService exerciseService;
    private final SetService setService;

    @GetMapping()
    public ResponseEntity get(){
        List<PlanExercise> planExercises = service.getAll();
        return ResponseEntity.ok(planExercises.stream().map(PlanExerciseDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity find(@PathVariable("id") Long id){
        Optional<PlanExercise> planExercise = service.getById(id);
        if(!planExercise.isPresent()){
            return new ResponseEntity("Exercício do plano não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(planExercise.map(PlanExerciseDTO::create));
    }

    @GetMapping("/{id}/sets")
    public ResponseEntity getSets(@PathVariable("id") Long id){
        Optional<PlanExercise> planExercise = service.getById(id);
        if(!planExercise.isPresent()){
            return new ResponseEntity("Exercício do plano não encontrado", HttpStatus.NOT_FOUND);
        }

        List<Set> sets = setService.getByPlanExercise(planExercise);
        return ResponseEntity.ok(sets.stream().map(SetDTO::create).collect(Collectors.toList()));
    }

    @PostMapping()
    public ResponseEntity store(@RequestBody PlanExerciseDTO dto){
        try {
            PlanExercise planExercise = convert(dto);
            Plan plan = planService.getById(dto.getIdPlan()).orElseThrow(() -> new BusinessRuleException("Plano não encontrado"));
            Exercise exercise = exerciseService.getById(dto.getIdExercise()).orElseThrow(() -> new BusinessRuleException("Exercício não encontrado"));
            planExercise.setPlan(plan);
            planExercise.setExercise(exercise);
            planExercise = service.save(planExercise);
            return new ResponseEntity(planExercise, HttpStatus.CREATED);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody PlanExerciseDTO dto){
        if(!service.getById(id).isPresent()){
            return new ResponseEntity("Exercício do plano não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            PlanExercise planExercise = convert(dto);
            Plan plan = planService.getById(dto.getIdPlan()).orElseThrow(() -> new BusinessRuleException("Plano não encontrado"));
            Exercise exercise = exerciseService.getById(dto.getIdExercise()).orElseThrow(() -> new BusinessRuleException("Exercício não encontrado"));
            planExercise.setPlan(plan);
            planExercise.setExercise(exercise);
            planExercise.setId(id);
            planExercise = service.save(planExercise);
            return new ResponseEntity(planExercise, HttpStatus.OK);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id){
        Optional<PlanExercise> planExercise = service.getById(id);
        if(!planExercise.isPresent()){
            return new ResponseEntity("Exercício do plano não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.destroy(planExercise.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public PlanExercise convert(PlanExerciseDTO dto){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, PlanExercise.class);
    }
}
