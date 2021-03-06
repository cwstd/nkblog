package com.nowcoder.community.service.impl;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService  {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;
//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private RedisTemplate redisTemplate;

    public User findUserById(int id) {
        User cache = getCache(id);
        if(cache==null){
            User user = setCache(id);
         return user;
        }
        return cache;
    }

    @Override
    public Map<String, Object> register(User user) {
        Map<String,Object> map=new HashMap<>();
        if(user==null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱为空");
            return map;
        }
        User user1 = userMapper.selectByName(user.getUsername());
        if(user1!=null){
            map.put("usernameMsg","账号已经存在！");
            return map;
        }

        User user2 = userMapper.selectByEmail(user.getEmail());
        if(user2!=null){
            map.put("emailMsg","账号已经存在！");
            return map;
        }

        user.setSalt(CommunityUtil.gennerateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.gennerateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        int i = userMapper.insertUser(user);
        if(i>0){
            Context context = new Context();
            context.setVariable("email",user.getEmail());
            String url=domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
            context.setVariable("url",url);
            String process = templateEngine.process("/mail/activation.html", context);
            System.out.println(user.getEmail());
            mailClient.sendMail(user.getEmail(),"激活账户",process);
        }

        return map;
    }

    @Override
    public int activateUser(int userId, String activateCode) {
        User user = userMapper.selectById(userId);
        if(user.getStatus()==1){
            return CommunityConstant.REACTIVATE;
        }
        if(user.getActivationCode().equals(activateCode)){
            userMapper.updateStatus(userId,1);
            clearCache(userId);
            return CommunityConstant.ACTIVATE_SUCCESS;
        }
        return CommunityConstant.ACTIVATE_FAIL;
    }

    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        HashMap<String, Object> map = new HashMap<>();
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","用户名不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }
        User user = userMapper.selectByName(username);
        if(user==null){
            map.put("username","账号不存在！");
            return map;
        }
        if(user.getStatus()==0){
            map.put("username","账号未激活！");
        }
        String s = CommunityUtil.md5(password + user.getSalt());
        if(! s.equals(user.getPassword())){
            map.put("passwordMsg","密码错误！");
        }

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.gennerateUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        loginTicket.setStatus(0);
//        int i = loginTicketMapper.insertLoginTicket(loginTicket);
        //存入Redis
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
        map.put("ticket",loginTicket.getTicket());

        return map;
    }

    @Override
    public int LoginOut(String ticket) {
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);

        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
//        int i = loginTicketMapper.updateStatus(ticket, 1);
        return 1;
    }

    @Override
    public LoginTicket selectTicket(String ticket) {
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    }

    @Override
    public int updateHeader(int userId, String headerUrl) {
        int i = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);

        return i;
    }

    @Override
    public int updatePassword(int userId, String password) {
        return userMapper.updatePassword(userId,password);
    }

    @Override
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    private User getCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        User user = (User) redisTemplate.opsForValue().get(userKey);
        return user;
    }

    private User setCache(int userId){
        User user = userMapper.selectById(userId);
        String userKey = RedisKeyUtil.getUserKey(user.getId());
        redisTemplate.opsForValue().set(userKey,user,3600, TimeUnit.MINUTES);
        return user;
    }
    private void clearCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }

    @Override
    public Collection<? extends GrantedAuthority> grantedAuthorities(int userId){
        User user = userMapper.selectById(userId);
        List<GrantedAuthority> list=new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()){
                    case 1:
                        return CommunityConstant.AUTHORITY_ADMIN;
                    case 2:
                        return CommunityConstant.AUTHORITY_MODERATOE;
                    default:
                        return CommunityConstant.AUTHORITY_USER;
                }
            }
        });
        return list;
    }


}
