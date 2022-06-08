package com.nowcoder.community.service.impl;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.MessageService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Override
    public List<Message> findConversation(int userId, int offset, int limit) {
        return messageMapper.selectConversation(userId,offset,limit);
    }

    @Override
    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    @Override
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId,offset,limit);
    }

    @Override
    public int findLettersCount(String conversationId) {
        return messageMapper.selectLettersCount(conversationId);
    }

    @Override
    public int findLettersUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLettersUnreadCount(userId,conversationId);
    }
}
