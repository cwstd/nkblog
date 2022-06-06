package com.nowcoder.community.controller;


import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {


    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${community.path.domain}")
    private String domain;

    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @LoginRequired
    @RequestMapping(value = "/setting",method = RequestMethod.GET)
    public String getSetingPage(){
        return "/site/setting";
    }
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeaderUrl(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error","您还没有选择图像");
            return "/site/setting";
        }
        String Filename = headerImage.getOriginalFilename();
        String suffix = Filename.substring(Filename.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确！");
            return "/site/setting";
        }
        String file = CommunityUtil.gennerateUUID() + suffix;
        File dest = new File(uploadPath, "/" + file);
        try {
            headerImage.transferTo(dest);
        }catch (IOException e){
            logger.error("上传文件失败"+e);
            throw new RuntimeException("上传文件失败"+e);
        }

        //更新用户头像路径
        User user = hostHolder.getUser();
        String url=domain+contextPath+"/user/header/"+file;
        userService.updateHeader(user.getId(),url);
        return "redirect:/index";
    }

    @LoginRequired
    @RequestMapping(value = "/password",method = RequestMethod.POST)
    public String updatePassword(String repassword, String newpassword, @CookieValue("ticket") String ticket , Model model){

        User user = hostHolder.getUser();
        String userrepassword = user.getPassword();
        if(!userrepassword.equals(CommunityUtil.md5(repassword+user.getSalt()))){
            model.addAttribute("passerror","原始密码错误");
            return "/site/setting";
        }
        if(StringUtils.isBlank(newpassword)){
            model.addAttribute("newerror","新密码不能为空！");
            return "/site/setting";
        }
        int i = userService.updatePassword(user.getId(), CommunityUtil.md5(newpassword + user.getSalt()));
        int i1 = userService.LoginOut(ticket);
        return "redirect:/login";

    }

    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeaderImg(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存放文件地址
        fileName = uploadPath + "/" + fileName;
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/"+suffix);
        try(
                OutputStream outputStream = response.getOutputStream();
                FileInputStream fileInputStream = new FileInputStream(fileName);
                )
        {
            byte[] bytes = new byte[1024];
            int b=0;
            while((b=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,b);
            }
        }catch (Exception e){
            logger.error("读取头像失败："+e);
        }

    }




}
