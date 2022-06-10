package com.nowcoder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
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
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {


    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        List<Message> conversationList = messageService.findConversation(user.getId(), page.getOffset(), page.getLimit());

        List<Map<String,Object>> mapList=new ArrayList<>();
        if(conversationList!=null){
            for(Message message:conversationList){
                HashMap<String, Object> map = new HashMap<>();
                map.put("conversation",message);
                map.put("letterCount",messageService.findLettersCount(message.getConversationId()));
                map.put("unreadCount",messageService.findLettersUnreadCount(user.getId(),message.getConversationId()));
                int targetId=user.getId()==message.getFromId()?message.getToId():message.getFromId();
                map.put("target",userService.findUserById(targetId));
                mapList.add(map);
            }

        }
        model.addAttribute("conversations",mapList);
        int lettersUnreadCount = messageService.findLettersUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",lettersUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
        return "/site/letter";
    }
    @RequestMapping(path = "letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable ("conversationId") String conversationId,Page page,Model model){
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLettersCount(conversationId));
        List<Message> letters = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> mapList=new ArrayList<>();
        if(letters!=null){
            for(Message message:letters){
                HashMap<String, Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                mapList.add(map);
            }
        }
        model.addAttribute("letters",mapList);
        String[] split = conversationId.split("_");
        System.out.println(split[0]);
        System.out.println(split[1]);
        int id0 = Integer.parseInt(split[0]);
        System.out.println(id0);
        int id1 = Integer.parseInt(split[1]);
        System.out.println(id1);
        if(hostHolder.getUser().getId()==id0){
            model.addAttribute("target",userService.findUserById(id1));
        }else {
            model.addAttribute("target",userService.findUserById(id0));
        }
        List<Integer> ids = findReadLetters(letters);
        int i = messageService.readMessage(ids);
        return "/site/letter-detail";
    }
    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName,String content){
        User target = userService.findUserByName(toName);
        if(target==null){
            return CommunityUtil.getJsonString(1,"目标用户不存在");
        }
        Message message = new Message();
        message.setContent(content);
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if(message.getFromId()<message.getToId()){
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }else {
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }
        message.setCreateTime(new Date());
        System.out.println(message.toString());
        messageService.addMessage(message);
        return CommunityUtil.getJsonString(0,"发送私信成功!");
    }
    @RequestMapping(path = "/notice/list",method = RequestMethod.GET)
    public String getNoticeList(Model model){
        int userId = hostHolder.getUser().getId();
        Message latestCommentMsg = messageService.findLatestNotice(userId, TOPIC_COMMENT);
        if(latestCommentMsg!=null){

            HashMap<String, Object> messageVo = new HashMap<>();
            messageVo.put("message",latestCommentMsg);
            String content = HtmlUtils.htmlUnescape(latestCommentMsg.getContent());
            Map<String,Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVo.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("postId",data.get("postId"));
            int noticeCount = messageService.findNoticeCount(userId,  TOPIC_COMMENT);
            messageVo.put("count",noticeCount);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(userId, TOPIC_COMMENT);
            System.out.println(noticeUnreadCount);
            messageVo.put("unread",noticeUnreadCount);
            model.addAttribute("commentNotice",messageVo);
        }
        Message latestlikeMsg = messageService.findLatestNotice(userId, TOPIC_LIKE);
        if(latestlikeMsg!=null){
            HashMap<String, Object> messageVo = new HashMap<>();
            messageVo.put("message",latestlikeMsg);
            String content = HtmlUtils.htmlUnescape(latestlikeMsg.getContent());
            Map<String,Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVo.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("postId",data.get("postId"));
            int noticeCount = messageService.findNoticeCount(userId,  TOPIC_LIKE);
            messageVo.put("count",noticeCount);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(userId, TOPIC_LIKE);
            System.out.println(noticeUnreadCount);
            messageVo.put("unread",noticeUnreadCount);
            model.addAttribute("likeNotice",messageVo);
        }
        Message latestfollowMsg = messageService.findLatestNotice(userId, TOPIC_FOLLOW);
        if(latestfollowMsg!=null){
            HashMap<String, Object> messageVo = new HashMap<>();
            messageVo.put("message",latestfollowMsg);
            String content = HtmlUtils.htmlUnescape(latestfollowMsg.getContent());
            Map<String,Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVo.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("postId",data.get("postId"));
            int noticeCount = messageService.findNoticeCount(userId,  TOPIC_FOLLOW);
            messageVo.put("count",noticeCount);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(userId, TOPIC_FOLLOW);
            System.out.println(noticeUnreadCount);
            messageVo.put("unread",noticeUnreadCount);
            model.addAttribute("followNotice",messageVo);
        }
        int lettersUnreadCount = messageService.findLettersUnreadCount(userId, null);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(userId, null);

        model.addAttribute("letterUnreadCount",lettersUnreadCount);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
        return"/site/notice";
    }
    @RequestMapping(path = "/notice/detail/{topic}",method = RequestMethod.GET)
    public String getNotices(@PathVariable("topic") String topic,Model model,Page page){
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/notice/detail/"+topic);
        page.setRows(messageService.findNoticeCount(user.getId(),topic));
        List<Message> notiecs = messageService.findNoties(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String,Object>> noticeVoList=new ArrayList<>();
        for(Message message:notiecs){
            HashMap<String, Object> map = new HashMap<>();
            map.put("notice",message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap data = JSONObject.parseObject(content, HashMap.class);
            map.put("user",userService.findUserById((Integer) data.get("userId")));
            map.put("entityType",data.get("entityType"));
            map.put("entityId",data.get("entityId"));
            map.put("postId",data.get("postId"));
            map.put("fromUser",userService.findUserById(message.getFromId()));
            noticeVoList.add(map);
        }
        model.addAttribute("notices",noticeVoList);
        List<Integer> ids = findReadLetters(notiecs);
        int i = messageService.readMessage(ids);
        return "/site/notice-detail";

    }


    /**
     * 获取未读id
     * @param letterList
     * @return
     */
    private List<Integer> findReadLetters(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if(letterList!=null){
            for(Message message:letterList){
                if(message.getToId()==hostHolder.getUser().getId() && message.getStatus()==0){
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }
}
