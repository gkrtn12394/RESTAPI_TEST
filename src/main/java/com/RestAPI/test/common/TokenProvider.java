package com.RestAPI.test.common;

import com.RestAPI.test.dto.TokenDto;
import com.RestAPI.test.dto.UserDto;
import com.RestAPI.test.service.UserService;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TokenProvider {

    @Value("${token-key}")
    private String key;
    @Value("${token-expiredTime}")
    private Long expiredTime;

    private UserService userService;

    @Autowired
    private TokenProvider(UserService userService) {
        this.userService = userService;
    }

    public TokenDto createToken(UserDto user) {
        TokenDto tokenDto = new TokenDto();

        //비밀번호 확인 등의 유효성 검사 진행
        Date cur = new Date();
        Date expiration = new Date(); // 토큰 만료 시간
        expiration.setTime(expiration.getTime() + expiredTime);

        tokenDto.setId(user.getId());
        tokenDto.setToken(Jwts.builder()
                .claim("username", user.getUsername())
                .claim("password", user.getPassword())
                .setIssuedAt(cur)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact());

        return tokenDto;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            if (claims.getBody().getExpiration().before(new Date())) {
                return false;
            }

            String username = (String) claims.getBody().get("username");
            String password = (String) claims.getBody().get("password");

            UserDto userDto = userService.findUser(username);
            if(!userDto.getPassword().equals(password))
                return false;

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
