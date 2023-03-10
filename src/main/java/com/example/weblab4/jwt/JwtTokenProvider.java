package com.example.weblab4.jwt;

import com.example.weblab4.model.Role;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtTokenProvider {
    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.expired}")
    private long validityInMilliseconds;

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
        return bCryptPasswordEncoder;
    }

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    //Payload — это полезные данные, которые хранятся внутри JWT. Эти данные также называют JWT-claims (заявки).
    public String createToken(String username, Role role) {

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", getRoleName(role));

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }


    /**
     Аутентификация — это проверка, что пользователь есть тот, за кого себя выдает. Чтобы выполнить проверку, надо:

     Извлечь имя и пароль из HTTP-запроса. За это отвечает UsernamePasswordAuthenticationFilter (конкретно в нашем приложении с Form-Based аутентификацией).
     Сравнить их с реальными именем и паролем, хранящимся где-то (в базе, на LDAP-сервере, во временной памяти приложения и т.д. где угодно). Это делает AuthenticationManager в методе authenticate().
     Вызывается authenticate() из фильтра UsernamePasswordAuthenticationFilter сразу после извлечения имени/пароля из HTTP-запроса.
     https://sysout.ru/kak-ustroena-autentifikatsiya-v-spring-security/
     */

    //UsernamePasswordAuthenticationToken - это реализация интерфейса Authentication,
    //которая хранит в себе имя пользователя и пароль
    //Сам по себе Authentication позволяет достать getCredentials() (здесь храниться пароль до проверки, после проверки он будет null)
    //getDetails() - additional details about the authentication request
    //getPrincipal() - имя пользователя до проверки

    //после того как AuthenticationManager выполнит свой единственный метод:
    // Authentication authenticate(Authentication var1) throws AuthenticationException - Сравнение имени и пароля пользователя,
    // которые пришли из запроса с их реальными именем и паролем, хранящимся где-то (в базе, на LDAP-сервере, во временной памяти приложения и т.д. где угодно).


    public Authentication getAuthentication(String token) {
        //log.info("My token in getAuthentication : {} ", token);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));

        // так как в конструкторе мы обозначили привилегии, то используется метод внутри super.setAuthenticated(true); - аутентификация корректна
        //если бы мы вернули Authentication без привилегий, то тогда super.setAuthenticated(false); - не корректна
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer_")) {
            //return bearerToken.substring(7, bearerToken.length());
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            //ВЫЧЛЕНЯЕМ ПОЛЯ ТОКЕНА, ЗАВОРАЧИВАЯ КАЖДЫЙ В ОБЪЕКТ CLAIMS И ЗАСОВЫВАЕМ В ЛИСТ
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);

            if (claims.getBody().getExpiration().before(new Date())) {
                return false;
            }

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }
    }

    private List<String> getRoleName(Role userRole) {
        List<String> result = new ArrayList<>(){{
            add(userRole.name());
        }};

        return result;
    }
}
