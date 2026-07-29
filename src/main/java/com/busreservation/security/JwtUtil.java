package com.busreservation.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(String email, String role, Integer userId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public String extractRole(String token) {
        return (String) Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().get("role");
    }

    public Integer extractUserId(String token) {
        Object userId = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().get("userId");
        return userId != null ? ((Number) userId).intValue() : null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("JWT rejected: token expired at {}", e.getClaims().getExpiration());
        } catch (SignatureException e) {
            logger.warn("JWT rejected: bad signature (secret mismatch between the token's issuer and this server)");
        } catch (MalformedJwtException e) {
            logger.warn("JWT rejected: malformed token");
        } catch (Exception e) {
            logger.warn("JWT rejected: {} - {}", e.getClass().getSimpleName(), e.getMessage());
        }
        return false;
    }
}