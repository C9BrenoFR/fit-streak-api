package br.ufjf.fsapi.api.controller;

import br.ufjf.fsapi.api.dto.DayHistoryDTO;
import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.DayHistory;
import br.ufjf.fsapi.model.entity.User;
import br.ufjf.fsapi.service.DayHistoryService;
import br.ufjf.fsapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
@CrossOrigin
public class DayHistoryController {
    private final DayHistoryService service;
    private final UserService userService;

    @GetMapping()
    public ResponseEntity get(){
        List<DayHistory> dayHistories = service.getByUser(Optional.empty());
        return ResponseEntity.ok(dayHistories.stream().map(DayHistoryDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity find(@PathVariable("id") Long id){
        Optional<DayHistory> dayHistory = service.getById(id);
        if(!dayHistory.isPresent()){
            return new ResponseEntity("Histórico do dia não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(dayHistory.map(DayHistoryDTO::create));
    }

    @PostMapping()
    public ResponseEntity store(@RequestBody DayHistoryDTO dto){
        try{
            DayHistory dayhistory = convert(dto);
            User user = userService.getById(dto.getUserId()).orElseThrow(() -> new BusinessRuleException("Usuário não encontrado"));
            dayhistory.setUser(user);
            dayhistory = service.save(dayhistory);
            return new ResponseEntity(dayhistory, HttpStatus.CREATED);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody DayHistoryDTO dto){
        if(!service.getById(id).isPresent()){
            return new ResponseEntity("Histórico do dia não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            DayHistory dayhistory = convert(dto);
            User user = userService.getById(dto.getUserId()).orElseThrow(() -> new BusinessRuleException("Usuário não encontrado"));
            dayhistory.setUser(user);
            dayhistory.setId(id);
            dayhistory = service.save(dayhistory);
            return new ResponseEntity(dayhistory, HttpStatus.OK);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id){
        Optional<DayHistory> dayHistory = service.getById(id);
        if(!dayHistory.isPresent()){
            return new ResponseEntity("Histórico do dia não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.destroy(dayHistory.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public DayHistory convert(DayHistoryDTO dto){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, DayHistory.class);
    }
}
