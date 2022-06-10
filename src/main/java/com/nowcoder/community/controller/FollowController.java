package com.nowcoder.community.controller;


import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

import static com.nowcoder.community.util.CommunityConstant.ENTITY_TYPE_USER;

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private UserService userService;
    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType,int entityId){
        User user = hostHolder.getUser();
        if(user==null){
            return CommunityUtil.getJsonString(0,"请登录！");
        }
        followService.Follow(user.getId(),entityType,entityId);
        Event event = new Event();
        event.setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJsonString(1,"关注成功！");
    }
    @RequestMapping(path = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType,int entityId){
        User user = hostHolder.getUser();
        if(user==null){
            return CommunityUtil.getJsonString(0,"请登录！");
        }
        followService.unFollow(user.getId(),entityType,entityId);
        return CommunityUtil.getJsonString(1,"取关成功！");
    }
    @RequestMapping(path = "/followees/{userId}",method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if(user==null){
            throw new RuntimeException("用户为null");
        }
        model.addAttribute("user",user);
        page.setLimit(5);
        page.setPath("/followees/"+userId);
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        page.setRows((int)followeeCount);
        List<Map<String, Object>> followee = followService.findFollowee(userId, page.getOffset(), page.getLimit());
        if(followee!=null){
            for(Map map:followee){
                User u=(User) map.get("user");
                if(hostHolder.getUser()==null){
                    map.put("hasFollowed",false);
                }else {
                    boolean hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
                    map.put("hasFollowed",hasFollowed);
                }

            }
        }
        model.addAttribute("users",followee);

        return "/site/followee";
    }

    @RequestMapping(path = "/followers/{userId}",method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if(user==null){
            throw new RuntimeException("用户为null");
        }
        model.addAttribute("user",user);
        page.setLimit(5);
        page.setPath("/followers/"+userId);
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER,userId);
        page.setRows((int)followerCount);
        List<Map<String, Object>> follower = followService.findFollower(userId, page.getOffset(), page.getLimit());
        if(follower!=null){
            for(Map map:follower){
                User u=(User) map.get("user");
                if(hostHolder.getUser()==null){
                    map.put("hasFollowed",false);
                }else {
                    boolean hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
                    map.put("hasFollowed",hasFollowed);
                }

            }
        }
        model.addAttribute("users",follower);

        return "/site/follower";
    }
}
