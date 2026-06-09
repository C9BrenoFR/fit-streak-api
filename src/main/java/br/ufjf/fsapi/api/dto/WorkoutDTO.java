package br.ufjf.fsapi.api.dto;

import br.ufjf.fsapi.model.entity.User;
import br.ufjf.fsapi.model.entity.Workout;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutDTO {

    private Long id;
    private String name;
    private String description;
    private boolean isPublic;
    //private LocalDate date;
    private Long idUser;
    private Long idCreator;

    public static WorkoutDTO create(Workout workout){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(workout, WorkoutDTO.class);
    }
}
