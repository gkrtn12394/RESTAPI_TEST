package com.RestAPI.test.controller;

import com.RestAPI.test.common.TokenProvider;
import com.RestAPI.test.dto.TokenDto;
import com.RestAPI.test.dto.UserDto;
import com.RestAPI.test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private UserService userService;
    private TokenProvider tokenProvider;

    @Autowired
    public UserController(UserService userService, TokenProvider tokenProvider) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    /**
     * 사용자를 가입한다.
     * @param userDto
     * @return
     */
    @PostMapping("/users")
    @ResponseBody
    public ResponseEntity<UserDto> insert(@RequestBody UserDto userDto) {
        if(StringUtils.isEmpty(userDto.getUsername()) || StringUtils.isEmpty(userDto.getPassword())) {
            throw new RuntimeException("Required username and password");
        }

        int result = userService.insert(userDto);
        if(result <= 0)
            throw new RuntimeException("이미 등록된 user");

        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }

    /**
     * 로그인 한뒤 토큰을 생성한다.
     * @param userDto
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<TokenDto> login(@RequestBody UserDto userDto) {
        if(StringUtils.isEmpty(userDto.getUsername()) || StringUtils.isEmpty(userDto.getPassword())) {
            throw new RuntimeException("Required username and password");
        }

        // 로그인 체크
        boolean isLogin = false;
        UserDto user = userService.findUser(userDto.getUsername());
        TokenDto tokenDto = new TokenDto();
        if(user.getUsername().equals(userDto.getUsername()) && user.getPassword().equals(userDto.getPassword())) {
            tokenDto = tokenProvider.createToken(user);
            isLogin = true;
        }

        if(isLogin) {
            return ResponseEntity.status(HttpStatus.OK).body(tokenDto);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(tokenDto);
        }

    }

    /**
     * 토큰 테스트
     * @param userDto
     * @return
     */
    @PostMapping("/tokentest")
    @ResponseBody
    public ResponseEntity<UserDto> tokentest(@RequestAttribute("user") UserDto userDto) {

        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }
}
