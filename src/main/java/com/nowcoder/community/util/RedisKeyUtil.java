package com.nowcoder.community.util;

public class RedisKeyUtil {

    private static final String SPLIT=":";
    private static final String PREFIX_ENTITY_LIKE="like:entity";
    private static final String PREFIX_USER_LIKE="like:user";
    private static final String PREFIX_FOLLOWEE="followee";
    private static final String PREFIX_FOLLOWER="follower";
    private static final String KAPTCHA_KEY="kaptcha";
    private static final String TICKET_KEY="ticket";
    private static final String USER_KEY="user";
    public  static  String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE+SPLIT+userId;
    }

    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }

    public static String getFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }
    //登录验证码
    public static String getKaptcha(String owner){
        return KAPTCHA_KEY+SPLIT+owner;
    }
    //登录凭证
    public static String getTicketKey(String ticket){
        return TICKET_KEY+SPLIT+ticket;
    }

    //登录凭证
    public static String getUserKey(int userId){
        return USER_KEY+SPLIT+userId;
    }
}
