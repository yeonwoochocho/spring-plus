package org.example.expert.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private static final String SECRET_KEY = "secret";

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Long getIdFromToken(String token) {
        return getClaims(token).get("id", Long.class);
    }

    public String getEmailFromToken(String token) {
        return getClaims(token).get("email", String.class);
    }

    public String getNicknameFromToken(String token) {
        return getClaims(token).get("nickname", String.class);
    }

    public String getUserRoleFromToken(String token) {
        return getClaims(token).get("userRole", String.class);
    }

    public UserRole parseUserRole(String role) {
        try {
            return UserRole.valueOf(role);
        } catch (Exception e) {
            return UserRole.DEFAULT;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
