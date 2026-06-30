package br.ufjf.fsapi.api.controller;

import br.ufjf.fsapi.api.dto.PlanDTO;
import br.ufjf.fsapi.api.dto.PlanExerciseDTO;
import br.ufjf.fsapi.api.dto.SetDTO;
import br.ufjf.fsapi.api.dto.UserDTO;
import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.Exercise;
import br.ufjf.fsapi.model.entity.Plan;
import br.ufjf.fsapi.model.entity.PlanExercise;
import br.ufjf.fsapi.model.entity.Set;
import br.ufjf.fsapi.service.ExerciseService;
import br.ufjf.fsapi.service.PlanExerciseService;
import br.ufjf.fsapi.service.PlanService;
import br.ufjf.fsapi.service.SetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Exercícios da ficha")

public class PlanExerciseController {
    private final PlanExerciseService service;
    private final PlanService planService;
    private final ExerciseService exerciseService;
    private final SetService setService;

    @GetMapping()
    @Operation(summary = "Busca todos os exercícios da ficha")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de Exercícios da ficha",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PlanExerciseDTO.class)))
            )
    })
    public ResponseEntity get(){
        List<PlanExercise> planExercises = service.getAll();
        return ResponseEntity.ok(planExercises.stream().map(PlanExerciseDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um exercício da ficha pelo ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Exercício da ficha encontrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlanExerciseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Exercício da ficha não encontrado",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
            )
    })
    public ResponseEntity find(@PathVariable("id") Long id){
        Optional<PlanExercise> planExercise = service.getById(id);
        if(!planExercise.isPresent()){
            return new ResponseEntity("Exercício da ficha não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(planExercise.map(PlanExerciseDTO::create));
    }

    @GetMapping("/{id}/sets")
    @Operation(summary = "Busca uma série pelo ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Série encontrada",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SetDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Série não encontrada",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
            )
    })
    public ResponseEntity getSets(@PathVariable("id") Long id){
        Optional<PlanExercise> planExercise = service.getById(id);
        if(!planExercise.isPresent()){
            return new ResponseEntity("Exercício da ficha não encontrado", HttpStatus.NOT_FOUND);
        }

        List<Set> sets = setService.getByPlanExercise(planExercise);
        return ResponseEntity.ok(sets.stream().map(SetDTO::create).collect(Collectors.toList()));
    }

    @PostMapping()
    @Operation(summary = "Cria um exercício da ficha")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Exercício da ficha foi criado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlanExercise.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Exercício da ficha não foi criado",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
            )
    })
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
    @Operation(summary = "Atualiza os dados de um exercício do plano")
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody PlanExerciseDTO dto){
        if(!service.getById(id).isPresent()){
            return new ResponseEntity("Exercício da ficha não encontrado", HttpStatus.NOT_FOUND);
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
    @Operation(summary = "Deleta um exercício da ficha")
    public ResponseEntity delete(@PathVariable("id") Long id){
        Optional<PlanExercise> planExercise = service.getById(id);
        if(!planExercise.isPresent()){
            return new ResponseEntity("Exercício da ficha não encontrado", HttpStatus.NOT_FOUND);
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
