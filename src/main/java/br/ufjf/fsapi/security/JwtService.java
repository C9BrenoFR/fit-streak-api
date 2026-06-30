package br.ufjf.fsapi.security;

import br.ufjf.fsapi.api.dto.AuthDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtService {
    @Value("${security.jwt.expires}")
    private String expires;

    @Value("${security.jwt.api-key}")
    private String apiKey;

    public String generateToken(AuthDTO user){
        long expString = Long.valueOf(expires);
        LocalDateTime expDateTime = LocalDateTime.now().plusDays(expString);
        Instant instant = expDateTime.atZone(ZoneId.systemDefault()).toInstant();
        Date date = Date.from(instant);

        return Jwts
                .builder()
                .setSubject(user.getEmail())
                .setExpiration(date)
                .signWith( SignatureAlgorithm.HS512, apiKey )
                .compact();
    }

    private Claims getClaims( String token ) throws ExpiredJwtException {
        return Jwts
                .parser()
                .setSigningKey(apiKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validToken(String token){
        try{
            Claims claims = getClaims(token);
            Date dateExpires = claims.getExpiration();
            LocalDateTime date =
                    dateExpires.toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDateTime();
            return !LocalDateTime.now().isAfter(date);
        }catch (Exception e){
            return false;
        }
    }

    public String getUserLogin(String token) throws ExpiredJwtException{
        return (String) getClaims(token).getSubject();
    }
}
