package br.ufjf.fsapi.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String exerciseType;
    private double restTime;
    private Integer setsNumber;
    private Integer repsNumber;

    @ManyToOne
    private Plan plan;

    @ManyToOne
    private Exercise exercise;
}
