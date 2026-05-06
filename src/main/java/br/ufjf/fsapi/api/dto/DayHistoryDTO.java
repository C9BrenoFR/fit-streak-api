package br.ufjf.fsapi.api.dto;

import br.ufjf.fsapi.model.entity.DayHistory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayHistoryDTO {

    private Long id;
    private LocalDate date;
    private int percentage;
    private Long userId;

    public static DayHistoryDTO create(DayHistory dayHistory){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dayHistory, DayHistoryDTO.class);
    }
}
