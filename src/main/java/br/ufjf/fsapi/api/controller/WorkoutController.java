package br.ufjf.fsapi.api.controller;

import br.ufjf.fsapi.api.dto.PlanDTO;
import br.ufjf.fsapi.api.dto.UserDTO;
import br.ufjf.fsapi.api.dto.WorkoutDTO;
import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.BodyMetrics;
import br.ufjf.fsapi.model.entity.Plan;
import br.ufjf.fsapi.model.entity.User;
import br.ufjf.fsapi.model.entity.Workout;
import br.ufjf.fsapi.service.PlanService;
import br.ufjf.fsapi.service.UserService;
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
@RequestMapping("/api/workout")
@RequiredArgsConstructor
@CrossOrigin
@Tag(name = "Treinos")

public class WorkoutController {
    private final WorkoutService service;
    private final UserService userService;
    private final PlanService planService;

    @GetMapping("/{id}")
    @Operation(summary = "Busca um treino por ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Treino encontrado",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = WorkoutDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Treino não encontrado",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
            )
    })
    public ResponseEntity find(@PathVariable("id") Long id){
        Optional<Workout> workout = service.getById(id);
        if(!workout.isPresent()){
            return new ResponseEntity("Treino não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(workout.map(WorkoutDTO::create));
    }

    @GetMapping("/{id}/plans")
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
    public ResponseEntity getPlans(@PathVariable("id") Long id){
        Optional<Workout> workout = service.getById(id);
        if(!workout.isPresent()){
            return new ResponseEntity("Treino não encontrado", HttpStatus.NOT_FOUND);
        }

        List<Plan> plans = planService.getByWorkout(workout);
        return ResponseEntity.ok(plans.stream().map(PlanDTO::create).collect(Collectors.toList()));
    }

    @PostMapping()
    @Operation(summary = "Cria um treino")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Treino foi criado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Workout.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Treino não foi criado",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
            )
    })
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
    @Operation(summary = "Atualiza os dados de um treino")
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
    @Operation(summary = "Deleta um treino")
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
