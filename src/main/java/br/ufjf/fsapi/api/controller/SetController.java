package br.ufjf.fsapi.api.controller;

import br.ufjf.fsapi.api.dto.SetDTO;
import br.ufjf.fsapi.api.dto.WorkoutDTO;
import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.PlanExercise;
import br.ufjf.fsapi.model.entity.Set;
import br.ufjf.fsapi.model.entity.Workout;
import br.ufjf.fsapi.service.PlanExerciseService;
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
@RequestMapping("/api/sets")
@RequiredArgsConstructor
@CrossOrigin
@Tag(name = "Séries")
public class SetController {
    private final SetService service;
    private final PlanExerciseService planExerciseService;

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma série pelo ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Série encontrado",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SetDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Série não encontrada",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
            )
    })
    public ResponseEntity find(@PathVariable("id") Long id){
        Optional<Set> set = service.getById(id);
        if(!set.isPresent()){
            return new ResponseEntity("Série não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(set.map(SetDTO::create));
    }

    @PostMapping()
    @Operation(summary = "Cria uma série")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Série foi criada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Set.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Série não foi criada",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
            )
    })
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
    @Operation(summary = "Atualiza os dados de uma série")
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
    @Operation(summary = "Deleta uma série")
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
