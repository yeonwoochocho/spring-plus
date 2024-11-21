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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            try {
                Long id = jwtTokenProvider.getIdFromToken(token);
                String email = jwtTokenProvider.getEmailFromToken(token);
                String nickname = jwtTokenProvider.getNicknameFromToken(token);
                UserRole userRole = jwtTokenProvider.parseUserRole(jwtTokenProvider.getUserRoleFromToken(token));

                AuthUser authUser = new AuthUser(id, email, nickname, userRole);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        authUser, null, Collections.singletonList(new SimpleGrantedAuthority(userRole.name()))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            }
        }
    }
}