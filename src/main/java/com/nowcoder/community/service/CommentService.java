package com.nowcoder.community.service;

import com.nowcoder.community.entity.Comment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {

    /**
     * 查询评论
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> findComments(int entityType, int entityId ,int offset, int limit);


    /***
     *
     * @param entityType
     * @param entityId
     * @return
     */
    int findCommentRows(int entityType, int entityId);

    /**
     * 增加评论
     * @param comment
     * @return
     */
    int addComment(Comment comment);

    /**
     * 根据id查评论
     * @param id
     * @return
     */
    Comment findCommentById(int id);

}
