package com.nowcoder.community.util;

public interface CommunityConstant {

    /**
     * 激活成功
     */
    int ACTIVATE_SUCCESS=0;
    /**
     * 重复激活
     */
    int REACTIVATE=1;
    /**
     * 激活失败
     */
    int ACTIVATE_FAIL=2;
    /**
     * 默认状态的登录超时时间
     */
    int DEFAULT_EXPIRED_SECONDS=3600*12;
    /**
     *
      */
    int REMEMBER_EXPIRED_SECONDS=3600*24*100;

    /***
     * 帖子
     */
    int ENTITY_TYPE_POST=1;

    /**
     * 评论
     *
     */
    int ENTITY_TYPE_COMMENT=2;


    /**
     * 用户
     */
    int ENTITY_TYPE_USER=3;
    /**
     * 主题:评论
     */
    String TOPIC_COMMENT="comment";
    /**
     * 主题:点赞
     */
    String TOPIC_LIKE="like";
    /**
     * 主题:关注
     */
    String TOPIC_FOLLOW="follow";
    /**
     *主题:发帖
     */
    String TOPIC_PUBLISH="publish";
    /**
     * 系统用户ID
     */
    int SYSTEM_ID=1;


}
