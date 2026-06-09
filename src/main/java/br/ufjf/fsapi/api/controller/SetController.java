package br.ufjf.fsapi.api.controller;

import br.ufjf.fsapi.api.dto.SetDTO;
import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.PlanExercise;
import br.ufjf.fsapi.model.entity.Set;
import br.ufjf.fsapi.service.PlanExerciseService;
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
@RequestMapping("/api/sets")
@RequiredArgsConstructor
@CrossOrigin
public class SetController {
    private final SetService service;
    private final PlanExerciseService planExerciseService;

    @GetMapping()
    public ResponseEntity get(){
        List<Set> sets = service.getAll();
        return ResponseEntity.ok(sets.stream().map(SetDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity find(@PathVariable("id") Long id){
        Optional<Set> set = service.getById(id);
        if(!set.isPresent()){
            return new ResponseEntity("Série não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(set.map(SetDTO::create));
    }

    @PostMapping()
    public ResponseEntity store(@RequestBody SetDTO dto){
        try {
            Set set = convert(dto);
            PlanExercise planExercise = planExerciseService.getById(dto.getIdPlanExercise()).orElseThrow(() -> new BusinessRuleException("Exercício do plano não encontrado"));
            set.setPlanExercise(planExercise);
            set = service.save(set);
            return new ResponseEntity(set, HttpStatus.CREATED);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody SetDTO dto){
        if(!service.getById(id).isPresent()){
            return new ResponseEntity("Série não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            Set set = convert(dto);
            PlanExercise planExercise = planExerciseService.getById(dto.getIdPlanExercise()).orElseThrow(() -> new BusinessRuleException("Exercício do plano não encontrado"));
            set.setPlanExercise(planExercise);
            set.setId(id);
            set = service.save(set);
            return new ResponseEntity(set, HttpStatus.OK);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id){
        Optional<Set> set = service.getById(id);
        if(!set.isPresent()){
            return new ResponseEntity("Série não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            service.destroy(set.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Set convert(SetDTO dto){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Set.class);
    }
}
