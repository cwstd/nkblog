package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit,int orderMode);

    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在<if>里使用,则必须加别名.
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostByid(int id);

    /**
     * 给帖子评论，增加评论的数量
     * @param id
     * @return
     */
    int updateDiscussCommentCount(int id,int commentCount);

    /***
     * 更新帖子状态
     * @param id
     * @param status
     * @return
     */
    int updateStatus(int id, int status);

    /**
     * 更新帖子加精
     * @param id
     * @param status
     * @return
     */
    int updateType(int id,int type);
    int updateScore(int id,double score);

}
