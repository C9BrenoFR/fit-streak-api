package br.ufjf.fsapi.api.controller;

import br.ufjf.fsapi.api.dto.PlanDTO;
import br.ufjf.fsapi.api.dto.PlanExerciseDTO;
import br.ufjf.fsapi.api.dto.WorkoutDTO;
import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.BodyMetrics;
import br.ufjf.fsapi.model.entity.Plan;
import br.ufjf.fsapi.model.entity.PlanExercise;
import br.ufjf.fsapi.model.entity.Workout;
import br.ufjf.fsapi.service.PlanExerciseService;
import br.ufjf.fsapi.service.PlanService;
import br.ufjf.fsapi.service.WorkoutService;
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
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@CrossOrigin
@Tag(name = "Fichas")
public class PlanController {
    private final PlanService service;
    private final WorkoutService workoutService;
    private final PlanExerciseService planExerciseService;

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma ficha pelo ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ficha encontrada",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PlanDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ficha não encontrada",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
            )
    })
    public ResponseEntity find(@PathVariable("id") Long id){
        Optional<Plan> plan = service.getById(id);
        if(!plan.isPresent()){
            return new ResponseEntity("Plano não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(plan.map(PlanDTO::create));
    }

    @GetMapping("/{id}/exercises")
    @Operation(summary = "Busca um exercício pelo ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Exercício do plano encontrado",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PlanExercise.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Exercício do plano não encontrado",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
            )
    })
    public ResponseEntity getExercises(@PathVariable("id") Long id){
        Optional<Plan> plan = service.getById(id);
        if(!plan.isPresent()){
            return new ResponseEntity("Plano não encontrado", HttpStatus.NOT_FOUND);
        }

        List<PlanExercise> planExercises = planExerciseService.getByPlan(plan);
        return ResponseEntity.ok(planExercises.stream().map(PlanExerciseDTO::create).collect(Collectors.toList()));
    }

    @PostMapping()
    @Operation(summary = "Cria uma ficha")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ficha foi criada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Plan.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ficha não foi criada",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
            )
    })
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
    @Operation(summary = "Atualiza os dados de uma ficha")
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
    @Operation(summary = "Deleta uma ficha")
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
