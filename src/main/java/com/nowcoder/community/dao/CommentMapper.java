package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * 查询帖子
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> selectComments(int entityType, int entityId ,int offset, int limit);

    /**
     * 查询帖子总数分页用
     * @param entityType
     * @param entityId
     * @return
     */
    int selectCommentRows(int entityType, int entityId);

    /***
     * 添加评论
     * @param comment
     * @return
     */
    int insertComment(Comment comment);
}
