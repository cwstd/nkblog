package com.nowcoder.community.util;

public class RedisKeyUtil {

    private static final String SPLIT=":";
    private static final String PREFIX_ENTITY_LIKE="like:entity";
    private static final String PEFIX_USER_LIKE="like:user";
    public  static  String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }
    public static String getUserLikeKey(int userId){
        return PEFIX_USER_LIKE+SPLIT+userId;
    }
}
