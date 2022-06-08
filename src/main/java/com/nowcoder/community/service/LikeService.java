package com.nowcoder.community.service;


import org.springframework.stereotype.Service;

@Service
public interface LikeService {

    /***
     *
     * @param userId
     * @param entityType
     * @param entityId
     */
    void like(int userId,int entityType,int entityId);

    /**
     * 查询点赞数量
     * @param entityType
     * @param entityId
     * @return
     */
    long likeCount(int entityType,int entityId);


    /**
     * 查询某人是否点赞
     * @param userId
     * @param entityType
     * @param entityId
     * @return 0 false 1 true
     */
    int findUserLikeStatus(int userId,int entityType,int entityId);
}
