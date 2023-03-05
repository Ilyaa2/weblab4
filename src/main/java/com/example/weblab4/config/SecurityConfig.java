package com.example.weblab4.config;

import com.example.weblab4.Role;
import jdk.swing.interop.SwingInterOpUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        //отключить корс защиту
        http
                //.csrf().disable().cors().disable().sessionManagement().disable() - пока работает и без отключения
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS,"/dot").permitAll()
                .antMatchers("/dot").hasRole(Role.USER.name())
                .anyRequest()
                .authenticated()
                .and()
                .logout()
                .and()
                .httpBasic();
                //.formLogin();
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {

        return new InMemoryUserDetailsManager(
                User.builder().username("user")
                        // Use without encode first
                        .password(passwordEncoder().encode("user"))
                        .roles(Role.USER.name())
                        .build()
        );
        // Go to UserDetailsServiceImpl - InMemory
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
