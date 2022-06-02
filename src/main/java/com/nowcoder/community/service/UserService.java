package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
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

    int LoginOut(String ticket);
}
