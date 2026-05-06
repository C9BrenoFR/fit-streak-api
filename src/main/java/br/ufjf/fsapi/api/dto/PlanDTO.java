package br.ufjf.fsapi.api.dto;

import br.ufjf.fsapi.model.entity.Plan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanDTO {

    private Long id;
    private String name;
    private String description;
    private Long idWorkout;

    public static PlanDTO create(Plan plan){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(plan, PlanDTO.class);
    }
}
