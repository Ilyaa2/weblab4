package com.example.weblab4.config;

import com.example.weblab4.jwt.JwtConfigurer;
import com.example.weblab4.jwt.JwtTokenProvider;
import com.example.weblab4.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/*todo ПРОАНАЛИЗИРУЙ JWT АУТЕНТИФИКАЦИЮ ВНИМАТЕЛЬНЕЕ
  У ТЕБЯ ИСПОЛЬЗУЕТСЯ JWT ТОКЕН, ЧТО ОЗНАЧАЕТ, ЧТО ОБРАЩЕНИЯ К БД ЧТОБ ВЫЧЛЕНИТЬ ДАННЫЕ О ЮЗЕРЕ И СВЕРИТЬ ИХ НЕ ДОЛЖНО ПРОИСХОДИТЬ.
ОБРАЩЕНИЕ К БД ЗА ЭТОЙ ЦЕЛЬЮ - УДЕЛ BASIC AUTH
JWT ТОКЕН НУЖЕН ДЛЯ ТОГО ЧТО ФОРМИРОВАТЬ ЕГО НА СЕРВЕРЕ С ПОМОЩЬЮ СЕКРЕТНОГО КЛЮЧА, А ПОСЛЕ КОГДА ЮЗЕР ЕГО ОТПРАВЛЯЕТ НА СЕРВЕР
ОПЯТЬ, МЫ СМОТРИМ ПО ПОДПИСИ, ЧТО ТОКЕН СОХРАНИЛ СВОЮ ЦЕЛОСТНОСТЬ
 */



@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String LOGIN_ENDPOINT = "/auth/login";
    private static final String REGISTRATION_ENDPOINT = "/auth/register";

    private final JwtTokenProvider jwtTokenProvider;


    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .cors().disable().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers(LOGIN_ENDPOINT).permitAll()
                .antMatchers(REGISTRATION_ENDPOINT).permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
    }

    /*
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

     */
}
