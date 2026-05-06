package br.ufjf.fsapi.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Set {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isValid = true;
    private Integer reps;
    private double load;
    private String loadType;
    //private LocalDate date;

    @ManyToOne
    private PlanExercise planExercise;

    public Exercise getExercise(){
        return planExercise.getExercise();
    }
}
