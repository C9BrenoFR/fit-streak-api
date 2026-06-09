package br.ufjf.fsapi.api.controller;

import br.ufjf.fsapi.api.dto.DayHistoryDTO;
import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.DayHistory;
import br.ufjf.fsapi.service.DayHistoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
@CrossOrigin
public class DayHistoryController {
    private final DayHistoryService service;

    @PostMapping()
    public ResponseEntity store(@RequestBody DayHistoryDTO dto){
        try{
            DayHistory dayhistory = convert(dto);
            dayhistory = service.save(dayhistory);
            return new ResponseEntity(dayhistory, HttpStatus.CREATED);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") Long id,@RequestBody DayHistoryDTO dto){
        if(!service.getById(id).isPresent()){
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            DayHistory dayhistory = convert(dto);
            dayhistory.setId(id);
            dayhistory = service.save(dayhistory);
            return new ResponseEntity(dayhistory, HttpStatus.CREATED);
        } catch (BusinessRuleException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public DayHistory convert(DayHistoryDTO dto){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, DayHistory.class);
    }
}
