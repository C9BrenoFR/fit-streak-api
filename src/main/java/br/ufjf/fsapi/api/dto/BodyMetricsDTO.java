package br.ufjf.fsapi.api.dto;

import br.ufjf.fsapi.model.entity.BodyMetrics;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BodyMetricsDTO {

    private Long id;
    private double weight;
    private double height;
    private Double imc;
    private Double bodyFatPercentage;
    private Double leanMass;
    private Double bodyWater;
    private Integer visceralFat;
    private Long idUser;

    public static BodyMetricsDTO create(BodyMetrics bodyMetrics){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(bodyMetrics, BodyMetricsDTO.class);
    }
}
