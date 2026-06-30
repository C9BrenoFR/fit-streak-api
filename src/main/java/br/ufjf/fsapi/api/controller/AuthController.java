package br.ufjf.fsapi.api.controller;

import br.ufjf.fsapi.api.dto.AuthDTO;
import br.ufjf.fsapi.api.dto.LoginDTO;
import br.ufjf.fsapi.exception.BusinessRuleException;
import br.ufjf.fsapi.model.entity.User;
import br.ufjf.fsapi.security.JwtService;
import br.ufjf.fsapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
@CrossOrigin
@Tag(name = "Autenticação")
public class AuthController {
    private final UserService service;
    private final JwtService jwtService;

    @PostMapping()
    @Operation(summary = "Autentica um usuário")
    public LoginDTO store(@RequestBody AuthDTO dto){
        try{
            UserDetails userDetails = service.authenticate(dto);
            Optional<User> user = service.getByEmail(userDetails.getUsername());
            String token = jwtService.generateToken(dto);
            return new LoginDTO(user.get(), token);
        } catch (UsernameNotFoundException | BusinessRuleException exception){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, exception.getMessage());
        }
    }

}
