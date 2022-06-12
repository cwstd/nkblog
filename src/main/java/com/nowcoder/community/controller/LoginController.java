package com.nowcoder.community.controller;


import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private RedisTemplate redisTemplate;
    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){ return "/site/register";}
    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String getLoginPage(){ return "/site/login";}
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String,Object> map=userService.register(user);
        if(map==null||map.isEmpty()){
            model.addAttribute("msg","注册成功，我们已经向您的邮箱发送了一个激活邮件，请尽快激活！");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }
    @RequestMapping(path = "/activation/{userId}/{activateCode}",method = RequestMethod.GET)
    public String activateUser(Model model, @PathVariable("userId") int userId, @PathVariable("activateCode") String activateCode){
        int a=userService.activateUser(userId,activateCode);
        Map<String,Object> map=new HashMap<>();
        if(a== CommunityConstant.ACTIVATE_SUCCESS){
            model.addAttribute("msg","激活成功，我们已经向您的邮箱发送了一个激活邮件，请尽快激活！");
            model.addAttribute("target","/login");
        }else if(a==CommunityConstant.REACTIVATE){
            model.addAttribute("msg","无效操作，请勿重复激活！");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","激活失败，激活码错误！");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }
    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberme, Model model, HttpSession session,@CookieValue("kaptchaowner") String kaptchaowner, HttpServletResponse response){

        System.out.println("login");
        System.out.println(username);
        System.out.println(password);
        System.out.println(code);
        if(StringUtils.isBlank(kaptchaowner)){
            model.addAttribute("codeMsg","验证码已经失效！");
            return "/site/login";
        }
        String kaptchaKey = RedisKeyUtil.getKaptcha(kaptchaowner);
        String kaptcha =(String) redisTemplate.opsForValue().get(kaptchaKey);
        //String kaptcha = (String) session.getAttribute("kaptcha");
        //从redis取验证码
        System.out.println(kaptcha);
        if(StringUtils.isBlank(code)||StringUtils.isBlank(kaptcha)||!kaptcha.equals(code)){
            model.addAttribute("codeMsg","验证码不正确！");
            return "/site/login";
        }
        int i = rememberme ? CommunityConstant.REMEMBER_EXPIRED_SECONDS : CommunityConstant.DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> login = userService.login(username, password, i);
        if(login.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", login.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(i);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg",login.get("usernameMsg"));
            model.addAttribute("passwordMsg",login.get("passwordMsg"));
            return "/site/login";
        }

    }
    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.LoginOut(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }



}
