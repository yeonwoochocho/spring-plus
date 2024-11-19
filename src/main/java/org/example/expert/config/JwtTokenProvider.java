package org.example.expert.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private static final String SECRET_KEY = "secret";  // 실제로는 외부 환경에서 관리해야 함

    // 토큰에서 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);  // 토큰 파싱
            return true;  // 토큰이 유효하면 true 반환
        } catch (Exception e) {
            return false;  // 토큰 유효하지 않으면 false 반환
        }
    }

    // 요청에서 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // "Bearer " 제거한 토큰 반환
        }
        return null;
    }

    // 토큰에서 "id" 값을 가져오기
    public Long getIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("id", Long.class);  // "id" 값을 추출
    }

    // 토큰에서 "email" 값을 가져오기
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("email", String.class);  // "email" 값을 추출
    }

    // 토큰에서 "userRole" 값을 가져오기
    public String getUserRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userRole", String.class);  // "userRole" 값을 추출
    }

    // 토큰에서 AuthUser 객체 생성
    public AuthUser getAuthUserFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long id = claims.get("id", Long.class);  // "id" 필드 사용
        String email = claims.get("email", String.class);
        String nickname = claims.get("nickname", String.class);
        UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));

        return new AuthUser(id, email, nickname, userRole);
    }

    public String getNicknameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("nickname", String.class);  // "nickname" 값을 추출
    }

}
