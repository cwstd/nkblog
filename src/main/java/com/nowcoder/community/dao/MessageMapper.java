package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    /**
     * 查询用户会话列表
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
   List<Message> selectConversation(int userId, int offset, int limit);

    /**
     * 查询当前用户会话数量
     * @param userId
     * @return
     */
   int selectConversationCount(int userId);

    /**
     * 查询某个绘画包含的私信
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectLetters(String conversationId,int offset,int limit);

    /**
     * 查询某个绘画包含的私信数量
     * @param conversationId
     * @return
     */
    int selectLettersCount(String conversationId);

    /**
     * 查询未读私信的数量
     * @param userId
     * @param conversationId
     * @return
     */
    int selectLettersUnreadCount(int userId,String conversationId);

    /**
     * 新增消息
     * @param message
     * @return
     */
    int insertMessage(Message message);

    /***
     * 更新消息状态
     * @param ids
     * @param status
     * @return
     */
    int updateMessageStatus(List<Integer> ids,int status);
}
