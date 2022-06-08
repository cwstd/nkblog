package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MessageController {


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
