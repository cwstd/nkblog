package com.nowcoder.community.controller;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private CommentService commentService;
    @Autowired
    private DiscussPostService discussPostService;
    @RequestMapping(value = "add/{disscussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("disscussPostId") int disscussPostId, Comment comment){
        comment.setCreateTime(new Date());
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        int i = commentService.addComment(comment);
        Event event = new Event();
        event.setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityId(comment.getEntityId())
                .setEntityType(comment.getEntityType())
                .setData("postId",disscussPostId);
        if(comment.getEntityType()==ENTITY_TYPE_POST){
            DiscussPost discussPost = discussPostService.selectDiscussPostOne(comment.getEntityId());
            event.setEntityUserId(discussPost.getUserId());
        }else if(comment.getEntityType()==ENTITY_TYPE_COMMENT){
            Comment comment1 = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(comment1.getUserId());
        }
        eventProducer.fireEvent(event);
        if(comment.getEntityType()==ENTITY_TYPE_POST){
            Event event1 = new Event().setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getId()).setEntityId(ENTITY_TYPE_POST).setEntityId(disscussPostId);
            eventProducer.fireEvent(event1);

        }
        return "redirect:/discuss/detail/"+disscussPostId;

    }
}
