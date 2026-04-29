package br.ufjf.fsapi.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BodyMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double weight;
    private double height;
    private double imc;
    private double bodyFatPercentage;
    private double leanMass;
    private double bodyWater;
    private Integer visceralFat;
    private Integer bmi;
    //private LocalDate date;

    @ManyToOne
    private User user;

}
