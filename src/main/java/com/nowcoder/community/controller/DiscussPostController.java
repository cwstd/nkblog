package com.nowcoder.community.controller;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
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

import static com.nowcoder.community.util.CommunityConstant.ENTITY_TYPE_COMMENT;
import static com.nowcoder.community.util.CommunityConstant.ENTITY_TYPE_POST;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;
    @ResponseBody
    @RequestMapping(path = "/add",method = RequestMethod.POST)
    public String addDiscussPost(String title,String content){

        System.out.println(title);
        System.out.println(content);
        User user = hostHolder.getUser();
        if(user==null){
            return CommunityUtil.getJsonString(403,"用户还没登录");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setContent(content);
        discussPost.setTitle(title);
        discussPost.setCreateTime(new Date());
        discussPost.setUserId(user.getId());
        discussPostService.addDiscussPost(discussPost);
        return CommunityUtil.getJsonString(0,"发布成功！");
    }
    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        DiscussPost discussPost = discussPostService.selectDiscussPostOne(discussPostId);
        model.addAttribute("post",discussPost);
        //Redis查询帖子点赞数量
        long likeCount = likeService.likeCount(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("discusslikeCount",likeCount);
        int userLikeStatus=0;
        if(hostHolder.getUser()==null){
            userLikeStatus=0;
        }else {
            userLikeStatus = likeService.findUserLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, discussPostId);
        }
        model.addAttribute("discussLikeStatus",userLikeStatus);
        ///////////
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user",user);
        //分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(discussPost.getCommentCount());

        List<Comment> commentsList = commentService.findComments(ENTITY_TYPE_POST, discussPost.getId(), page.getOffset(), page.getLimit());

        List<Map<String,Object>> mapList=new ArrayList<>();
        if(commentsList!=null){

            for(Comment comment:commentsList){
                Map<String,Object> commentVo=new HashMap<>();
                commentVo.put("comment",comment);
                commentVo.put("user",userService.findUserById(comment.getUserId()));
                //回复列表
                List<Comment> replyList = commentService.findComments(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                //Redis查询帖子评论点赞数量
                long commentlikeCount = likeService.likeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("commentlikeCount",commentlikeCount);
                int commentLikeStatus=0;
                if(hostHolder.getUser()==null){
                    commentLikeStatus=0;
                }else {
                    commentLikeStatus = likeService.findUserLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                }
                commentVo.put("commentLikeStatus",commentLikeStatus);
                ////////////
                List <Map<String,Object>> replyListVo=new ArrayList<>();
                    for(Comment reply:replyList){
                        HashMap<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply",reply);
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);
                        replyListVo.add(replyVo);
                        //Redis查询帖子评论点赞数量
                        long replylikeCount = likeService.likeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("replylikeCount",replylikeCount);
                        int replyLikeStatus=0;
                        if(hostHolder.getUser()==null){
                            replyLikeStatus=0;
                        }else {
                            replyLikeStatus = likeService.findUserLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        }
                        replyVo.put("replyLikeStatus",replyLikeStatus);
                        ////////////
                    }
                    commentVo.put("replys",replyListVo);
                int commentRows = commentService.findCommentRows(ENTITY_TYPE_COMMENT, comment.getId());
                    commentVo.put("replyCount",commentRows);
                    mapList.add(commentVo);
            }
        }
        model.addAttribute("comments",mapList);

        return "/site/discuss-detail";
    }
}
