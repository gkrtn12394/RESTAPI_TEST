package com.RestAPI.test.service;

import com.RestAPI.test.dto.ContentDto;
import com.RestAPI.test.dto.MessageDto;
import com.RestAPI.test.dto.UserDto;
import com.RestAPI.test.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public int insertContent(ContentDto content) {
        return messageRepository.insertContent(content);
    }

    public int insert(MessageDto message) {
        return messageRepository.insert(message);
    }

    public List<MessageDto> select(int recipient, int start, int limit) {
        return messageRepository.select(recipient, start, limit);
    }
}
