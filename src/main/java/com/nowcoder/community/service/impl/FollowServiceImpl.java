package com.nowcoder.community.service.impl;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.nowcoder.community.util.CommunityConstant.ENTITY_TYPE_USER;

@Service
public class FollowServiceImpl implements FollowService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;
    @Override
    public int Follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                redisOperations.multi();
                redisOperations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                redisOperations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());
                return redisOperations.exec();
            }
        });



        return 1;
    }

    @Override
    public int unFollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                redisOperations.multi();
                redisOperations.opsForZSet().remove(followeeKey,entityId);
                redisOperations.opsForZSet().remove(followerKey,userId);
                return redisOperations.exec();
            }
        });
        return 1;
    }

    @Override
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        Long count = redisTemplate.opsForZSet().zCard(followeeKey);
        return count;
    }

    @Override
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        Long count = redisTemplate.opsForZSet().zCard(followerKey);
        return count;
    }

    @Override
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey,entityId)!=null;
    }

    @Override
    public List<Map<String, Object>> findFollowee(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        Set<Integer> set = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if(set==null){
            return null;
        }

        List<Map<String,Object>>list=new ArrayList<>();
        for(Integer id:set){
            HashMap<String, Object> map = new HashMap<>();
            User user = userService.findUserById(id);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, id);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> findFollower(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer> set = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1); if(set==null){
            return null;
        }

        List<Map<String,Object>>list=new ArrayList<>();
        for(Integer id:set){
            HashMap<String, Object> map = new HashMap<>();
            User user = userService.findUserById(id);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followerKey, id);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }
}
