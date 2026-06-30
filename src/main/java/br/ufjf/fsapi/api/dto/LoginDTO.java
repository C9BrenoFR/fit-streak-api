package br.ufjf.fsapi.api.dto;

import br.ufjf.fsapi.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    private User user;
    private String token;
}
