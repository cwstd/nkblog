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


}
