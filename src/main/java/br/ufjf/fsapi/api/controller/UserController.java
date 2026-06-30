package br.ufjf.fsapi.api.controller;

import br.ufjf.fsapi.api.dto.BodyMetricsDTO;
import br.ufjf.fsapi.api.dto.DayHistoryDTO;
import br.ufjf.fsapi.api.dto.UserDTO;
import br.ufjf.fsapi.api.dto.WorkoutDTO;
import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.BodyMetrics;
import br.ufjf.fsapi.model.entity.DayHistory;
import br.ufjf.fsapi.model.entity.User;
import br.ufjf.fsapi.model.entity.Workout;
import br.ufjf.fsapi.service.BodyMetricsService;
import br.ufjf.fsapi.service.DayHistoryService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin
@Tag(name = "Usuários")
public class UserController {
    private final UserService service;
    private final WorkoutService workoutService;
    private final BodyMetricsService dayService;
    private final DayHistoryService dayHistoryService;

    @GetMapping()
    @Operation(summary = "Busca todos usuários")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuários",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))
            )
    })
    public ResponseEntity get(){
        List<User> users = service.getAll();
        return ResponseEntity.ok(users.stream().map(UserDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um usuário por ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário encontrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
            )
    })
    public ResponseEntity find(@PathVariable("id") Long id){
        Optional<User> user = service.getById(id);
        if(!user.isPresent()){
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(user.map(UserDTO::create));
    }

    @GetMapping("/{id}/workouts")
    @Operation(summary = "Busca os treinos de um usuário")
    public ResponseEntity getWorkouts(@PathVariable("id") Long id){
        Optional<User> user = service.getById(id);
        if(!user.isPresent()){
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }

        List<Workout> workouts = workoutService.getByUser(user);
        return ResponseEntity.ok(workouts.stream().map(WorkoutDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}/bodymetrics")
    @Operation(summary = "Busca as métricas corporais de um usuário")
    public ResponseEntity getBodyMetrics(@PathVariable("id") Long id){
        Optional<User> user = service.getById(id);
        if(!user.isPresent()){
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }

        List<BodyMetrics> day = dayService.getByUser(user);
        return ResponseEntity.ok(day.stream().map(BodyMetricsDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Busca os históricos de treino diário de um usuário")
    public ResponseEntity getDayHistories(@PathVariable("id") Long id){
        Optional<User> user = service.getById(id);
        if(!user.isPresent()){
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }

        List<DayHistory> dayHistories = dayHistoryService.getByUser(user);
        return ResponseEntity.ok(dayHistories.stream().map(DayHistoryDTO::create).collect(Collectors.toList()));
    }

    @PostMapping()
    @Operation(summary = "Cria um usuário")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário foi criado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não foi criado",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
            )
    })
    public ResponseEntity store(@RequestBody UserDTO dto){
        try {
            User user = convert(dto);
            user = service.save(user);
            return new ResponseEntity(user, HttpStatus.CREATED);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza os dados de um usuário")
    public ResponseEntity update(@PathVariable("id") Long id,@RequestBody UserDTO dto){
        if(!service.getById(id).isPresent()){
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            User user = convert(dto);
            user.setId(id);
            user = service.save(user);
            return new ResponseEntity(user, HttpStatus.OK);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um usuário")
    public ResponseEntity delete(@PathVariable("id") Long id){
        Optional<User> user = service.getById(id);
        if(!user.isPresent()){
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.destroy(user.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public User convert(UserDTO dto){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, User.class);
    }
}
