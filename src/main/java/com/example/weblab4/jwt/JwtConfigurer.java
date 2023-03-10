package com.example.weblab4.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private JwtTokenProvider jwtTokenProvider;

    public JwtConfigurer(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        JwtTokenFilter jwtTokenFilter = new JwtTokenFilter(jwtTokenProvider);
        httpSecurity.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * Извлечь имя и пароль из HTTP-запроса. За это отвечает UsernamePasswordAuthenticationFilter.
     * Он сравнивает их с реальными именем и паролем, хранящимся где-то (в базе). Это делает AuthenticationManager в методе authenticate().
     * Вызывается authenticate() из фильтра UsernamePasswordAuthenticationFilter сразу после извлечения имени/пароля из HTTP-запроса.
     */
}
