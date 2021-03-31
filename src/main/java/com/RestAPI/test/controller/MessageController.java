package com.RestAPI.test.controller;

import com.RestAPI.test.common.TokenProvider;
import com.RestAPI.test.dto.MessageDto;
import com.RestAPI.test.dto.UserDto;
import com.RestAPI.test.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class MessageController {

    private MessageService messageService;
    private TokenProvider tokenProvider;

    @Autowired
    public MessageController(MessageService messageService, TokenProvider tokenProvider) {
        this.messageService = messageService;
        this.tokenProvider = tokenProvider;
    }

    /**
     * 메세지를 입력한다.
     * @param userDto
     * @param messageDto
     * @return
     */
    @PostMapping("/messages")
    @ResponseBody
    public ResponseEntity<Map<String,Object>> insert(@RequestAttribute("user") UserDto userDto, @RequestBody MessageDto messageDto) {
        if(StringUtils.isEmpty(messageDto.getSender()) || StringUtils.isEmpty(messageDto.getRecipient()) || messageDto.getContent() == null) {
            throw new RuntimeException("Sender, recipient, and content required");
        }

        // message 저장
        if(messageDto.getTimestamp() == null)
            messageDto.setTimestamp(new Date());

        int id = messageService.insert(messageDto);

        Map<String,Object> re = new HashMap<>();
        re.put("id", id);
        re.put("time_stamp", messageDto.getTimestamp());

        return ResponseEntity.status(HttpStatus.OK).body(re);
    }

    /**
     * 메세지 리스트를 가지고 온다.
     * @param recipient
     * @param start
     * @param limit
     * @return
     */
    @GetMapping("/messages")
    public ResponseEntity<Map<String,List<MessageDto>>> getMessages(@RequestParam int recipient, @RequestParam int start, @RequestParam(required = false, defaultValue = "100") int limit) {
        //메세지를 가지고 오는 구조 개발
        List<MessageDto> messageDtos = messageService.select(recipient, start, limit);

        Map<String,List<MessageDto>> re = new HashMap<>();
        re.put("messages", messageDtos);
        return ResponseEntity.status(HttpStatus.OK).body(re);
    }

}
