package com.nowcoder.community.service;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageService {
    /**
     * 查询用户会话列表
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> findConversation(int userId, int offset, int limit);

    /**
     * 查询当前用户会话数量
     * @param userId
     * @return
     */
    int findConversationCount(int userId);

    /**
     * 查询某个绘画包含的私信
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> findLetters(String conversationId,int offset,int limit);

    /**
     * 查询某个绘画包含的私信数量
     * @param conversationId
     * @return
     */
    int findLettersCount(String conversationId);

    /**
     * 查询未读私信的数量
     * @param userId
     * @param conversationId
     * @return
     */
    int findLettersUnreadCount(int userId,String conversationId);
}
