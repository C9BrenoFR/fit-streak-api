package br.ufjf.fsapi.api.dto;

import br.ufjf.fsapi.model.entity.Exercise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDTO {

    private Long id;
    private String name;
    private String muscularGroup;
    private String description;
    private String image;

    public static ExerciseDTO create(Exercise exercise){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(exercise, ExerciseDTO.class);
    }
}
