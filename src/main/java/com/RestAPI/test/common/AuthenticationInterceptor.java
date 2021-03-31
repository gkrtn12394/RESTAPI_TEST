package com.RestAPI.test.common;

import com.RestAPI.test.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);
    private TokenProvider tokenProvider;

    @Autowired
    public AuthenticationInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        if (token.isEmpty()) {
            logger.info("token is empty");

            throw new IllegalArgumentException("유효하지 않은 토큰");
        }

        if (!tokenProvider.validateToken(token)) {
            logger.info("current user is not logged in");

            throw new IllegalArgumentException("유효하지 않은 토큰");
        }

        UserDto userDto = new UserDto();
        request.setAttribute("user", userDto);

        return true;
    }

}
