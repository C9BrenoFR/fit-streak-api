package br.ufjf.fsapi.api.dto;

import br.ufjf.fsapi.model.entity.Set;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetDTO {

    private Long id;
    private boolean isValid = true;
    private Integer reps;
    private double load;
    private String loadType;
    //private LocalDate date;
    private Long idPlanExercise;

    // Additional Values
    private String name;

    public static SetDTO create(Set set){
        ModelMapper modelMapper = new ModelMapper();
        SetDTO dto = modelMapper.map(set, SetDTO.class);
        dto.name = set.getExercise().getName();
        return dto;
    }
}
