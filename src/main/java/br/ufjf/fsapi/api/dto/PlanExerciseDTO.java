package br.ufjf.fsapi.api.dto;

import br.ufjf.fsapi.model.entity.PlanExercise;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanExerciseDTO {

    private Long id;
    private String exerciseType;
    private double restTime;
    private Integer setsNumber;
    private Integer repsNumber;
    private Long idPlan;
    private Long idExercise;

    // Additional values
    private String name;
    private String muscularGroup;

    public static PlanExerciseDTO create(PlanExercise planExercise){
        ModelMapper modelMapper = new ModelMapper();
        PlanExerciseDTO dto = modelMapper.map(planExercise, PlanExerciseDTO.class);
        dto.name = planExercise.getExercise().getName();
        dto.muscularGroup = planExercise.getExercise().getMuscularGroup();
        return dto;
    }
}
