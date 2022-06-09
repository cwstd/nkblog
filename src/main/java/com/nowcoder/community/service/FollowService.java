package com.nowcoder.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface FollowService {

    /***
     * 关注
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    int Follow(int userId,int entityType,int entityId);

    /**
     * 取关
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    int unFollow(int userId,int entityType,int entityId);
    /**
     * 查询实体关注的数量
     * @param userId
     * @param entityType
     * @return
     */
    long findFolloweeCount(int userId,int entityType);

    /**
     * 查询实体被关注的数量
     * @param entityType
     * @param entityId
     * @return
     */
    long findFollowerCount(int entityType,int entityId);

    /**
     * 查询用户是否关注该实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    boolean hasFollowed(int userId,int entityType,int entityId);

    /**
     * 查询关注的人
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Map<String,Object>> findFollowee(int userId,int offset,int limit);

    /***
     * 查询粉丝
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Map<String,Object>> findFollower(int userId,int offset,int limit);
}
