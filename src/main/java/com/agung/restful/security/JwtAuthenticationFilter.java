package com.agung.restful.security;

import com.agung.restful.entity.User;
import com.agung.restful.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            try {
                String token = authHeader.substring(7);

                if (jwtUtil.isTokenExpired(token)) {
                    throw new ExpiredJwtException(null, null, "Token Expired");
                }
                String username = jwtUtil.extractUsername(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    User userDetails = userRepository.findById(username).orElseThrow();
                    if (jwtUtil.validateAccessToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, null);
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (ExpiredJwtException ex) {
                request.setAttribute("exceptionMessage", "Token Expired");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (SignatureException ex) {
                request.setAttribute("exceptionMessage", "Invalid Token Signature");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (MalformedJwtException ex) {
                request.setAttribute("exceptionMessage", "Invalid Token Format");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (UnsupportedJwtException ex) {
                request.setAttribute("exceptionMessage", "Unsupported JWT Token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (IllegalArgumentException ex) {
                request.setAttribute("exceptionMessage", "Token is Missing");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
