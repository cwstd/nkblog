package com.nowcoder.community.controller;


import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;
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

}
