package com.example.weblab4.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

@Slf4j
public class JwtTokenFilter extends GenericFilterBean {
    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {
        //проверка на корретность заголовка
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);
        //проверка на валидность и непросроченность токена
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);

                if (auth != null) {
                    //положили в контекст, чтоб потом можно было извлечь
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
            filterChain.doFilter(req, res);
        } catch(AuthenticationException authException){
            HttpServletResponse httpResponse = (HttpServletResponse) res;
            httpResponse.setStatus(403);
            if (authException instanceof JwtAuthenticationException){
                log.warn("JWT token is expired or invalid");
            } else if (authException instanceof UsernameNotFoundException){
                log.warn("Username with this name not found");
            }
        }
    }
}
