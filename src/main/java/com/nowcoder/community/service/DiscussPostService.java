package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DiscussPostService {


    /**
     * 分页查询
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);

    /**
     * 查询总页数
     * @param userId
     * @return
     */
    int findDiscussPostRows(int userId);

    /**
     * 添加帖子
     * @param discussPost
     */
    int addDiscussPost(DiscussPost discussPost);

    /***
     * 查询一个文件
     * @param id
     * @return
     */
    DiscussPost selectDiscussPostOne(int id);

    /**
     * 增加帖子评论数量
     * @param id
     * @param commentCount
     * @return
     */
    int addCommentCount(int id,int commentCount);


    /***
     * 更新帖子状态
     * @param id
     * @param status
     * @return
     */
    int ReStatus(int id, int status);

    /**
     * 更新帖子加精
     * @param id
     * @param status
     * @return
     */
    int ReType(int id,int type);
}
