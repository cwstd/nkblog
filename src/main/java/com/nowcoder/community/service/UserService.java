package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


public interface UserService {

    /**
     * 查询用户
     * @param id
     * @return
     */
    User findUserById(int id);

    /**
     * 注册功能
     * @param user
     * @return
     */
    Map<String,Object> register(User user);

    /**
     * 激活账户
     * @param userId
     * @param activateCode
     * @return
     */
    int activateUser(int userId, String activateCode);

    /**
     *
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */
    Map<String,Object> login(String username,String password,int expiredSeconds);

    /**
     * 用户退出
     * @param ticket
     * @return
     */
    int LoginOut(String ticket);

    /**
     * 查询ticket
     * @param ticket
     * @return
     */
    LoginTicket selectTicket(String ticket);

    /**
     * 更新用户头像信息
     * @param userId
     * @param headerUrl
     * @return
     */
    int updateHeader(int userId,String headerUrl);

    /***
     * 更新用户密码
     * @param userId
     * @param password
     * @return
     */
    int updatePassword(int userId,String password);

    User findUserByName(String username);
}
