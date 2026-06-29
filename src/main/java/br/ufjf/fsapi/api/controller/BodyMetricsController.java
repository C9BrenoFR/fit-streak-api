package br.ufjf.fsapi.api.controller;

import br.ufjf.fsapi.api.dto.BodyMetricsDTO;
import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.BodyMetrics;
import br.ufjf.fsapi.model.entity.User;
import br.ufjf.fsapi.service.BodyMetricsService;
import br.ufjf.fsapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/bodymetrics")
@RequiredArgsConstructor
@CrossOrigin
@Tag(name = "Métricas Corporais")
public class BodyMetricsController {
    private final BodyMetricsService service;
    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma métrica corporal por ID")
    public ResponseEntity find(@PathVariable("id") Long id){
        Optional<BodyMetrics> bodyMetrics = service.getById(id);
        if(!bodyMetrics.isPresent()){
            return new ResponseEntity("Métrica corporal não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(bodyMetrics.map(BodyMetricsDTO::create));
    }

    @PostMapping()
    @Operation(summary = "Cria uma nova métrica corporal")
    public ResponseEntity store(@RequestBody BodyMetricsDTO dto){
        try {
            BodyMetrics bodyMetrics = convert(dto);
            User user = userService.getById(dto.getIdUser()).orElseThrow(() -> new BusinessRuleException("Usuário não encontrado"));
            bodyMetrics.setUser(user);
            bodyMetrics = service.save(bodyMetrics);
            return new ResponseEntity(bodyMetrics, HttpStatus.CREATED);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza os dados de uma métrica corporal")
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody BodyMetricsDTO dto){
        if(!service.getById(id).isPresent()){
            return new ResponseEntity("Métrica corporal não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            BodyMetrics bodyMetrics = convert(dto);
            User user = userService.getById(dto.getIdUser()).orElseThrow(() -> new BusinessRuleException("Usuário não encontrado"));
            bodyMetrics.setUser(user);
            bodyMetrics.setId(id);
            bodyMetrics = service.save(bodyMetrics);
            return new ResponseEntity(bodyMetrics, HttpStatus.OK);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta uma métrica corporal")
    public ResponseEntity delete(@PathVariable("id") Long id){
        Optional<BodyMetrics> bodyMetrics = service.getById(id);
        if(!bodyMetrics.isPresent()){
            return new ResponseEntity("Métrica corporal não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            service.destroy(bodyMetrics.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public BodyMetrics convert(BodyMetricsDTO dto){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, BodyMetrics.class);
    }
}
