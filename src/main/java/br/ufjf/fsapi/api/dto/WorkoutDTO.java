package br.ufjf.fsapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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
}
