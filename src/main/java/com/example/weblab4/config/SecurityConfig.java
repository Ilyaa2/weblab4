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

import java.util.Optional;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String LOGIN_ENDPOINT = "/auth/login";

    private final JwtTokenProvider jwtTokenProvider;

    /*
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        //отключить корс защиту
        http
                //.csrf().disable().cors().disable().sessionManagement().disable() - пока работает и без отключения
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS,"/dot").permitAll()
                .antMatchers("/register").permitAll()
                .antMatchers("/dot").hasRole(Role.USER.name())
                .anyRequest()
                .authenticated()
                .and().sessionManagement().disable()
                .logout()
                .and()
                .httpBasic();
                //.formLogin();
    }

     */

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
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(LOGIN_ENDPOINT).permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
    }
}
