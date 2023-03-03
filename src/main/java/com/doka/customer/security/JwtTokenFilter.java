package com.doka.customer.security;

import com.doka.customer.dto.output.ApiResponseDto;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    TokenManager tokenManager;

    @Autowired
    Gson gson;

    @Override
    @SneakyThrows
    protected void doFilterInternal(@NotNull HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        final String authorization = request.getHeader("Authorization");

        String errorMessage = null;
        Long customerId = null;


        try {
            if(authorization != null && authorization.startsWith("Bearer ")) {
                String jwtToken = tokenManager.getBearerToken(authorization);
                Claims claims = tokenManager.getClaims(jwtToken);
                customerId = Long.valueOf(claims.getSubject());
            }
        } catch (ExpiredJwtException e) {
            errorMessage = "Session is expired. Login required.";
        } catch (BadCredentialsException e) {
            errorMessage = "Unauthorized: bad credentials";
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Unexpected server side error";
        }

        if (customerId != null || errorMessage == null) {
            if (customerId != null) {
                request.setAttribute("customerId", customerId);

                UsernamePasswordAuthenticationToken usernamePasswordToken =
                        new UsernamePasswordAuthenticationToken(customerId, null, null);

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordToken);

                // generate refresh-token
                String newToken = tokenManager.generateToken(String.valueOf(customerId), null);
                response.setHeader("refresh-token", newToken);

                filterChain.doFilter(request, response);
            }
        } else {
            ApiResponseDto apiResponseDto = new ApiResponseDto(errorMessage);
            String newContent = gson.toJson(apiResponseDto);
            response.setCharacterEncoding("ISO-8859-9");
            response.setContentType("application/json");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentLength(newContent.length());
            response.getWriter().write(newContent);
            response.getWriter().close();
        }
    }

}