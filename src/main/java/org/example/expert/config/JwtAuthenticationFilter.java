package org.example.expert.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // JWT 토큰 추출
        String token = jwtTokenProvider.resolveToken(request);

        // 토큰이 존재하고 유효하면 Authentication 객체 설정
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Long id = jwtTokenProvider.getIdFromToken(token);  // 토큰에서 사용자 id 추출
            String email = jwtTokenProvider.getEmailFromToken(token);// 토큰에서 이메일 추출
            String nickname = jwtTokenProvider.getNicknameFromToken(token);
            UserRole userRole = UserRole.valueOf(jwtTokenProvider.getUserRoleFromToken(token));  // 토큰에서 역할 추출

            AuthUser authUser = new AuthUser(id, email, nickname, userRole);  // AuthUser 객체 생성

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authUser, null, null  // 인증 정보 설정
            );
            // SecurityContext에 인증 객체 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 필터 체인으로 요청 전달
        filterChain.doFilter(request, response);
    }
}
