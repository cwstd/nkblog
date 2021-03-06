package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;


    @RequestMapping(path = "/search",method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model){

        org.springframework.data.domain.Page<DiscussPost> sreachResult=
        elasticsearchService.searchDiscussPost(keyword,page.getCurrent()-1,page.getLimit());

        List<Map<String,Object>> discussPosts=new ArrayList<>();
        if(sreachResult!=null){

            for(DiscussPost discussPost:sreachResult){
                HashMap<String, Object> map = new HashMap<>();
                map.put("post",discussPost);
                map.put("user",userService.findUserById(discussPost.getUserId()));
                map.put("likeCount",likeService.likeCount(ENTITY_TYPE_POST,discussPost.getId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("keyword",keyword);
        page.setPath("/search?keyword="+keyword);
        page.setRows(sreachResult==null?0:(int) sreachResult.getTotalElements());
        page.setLimit(5);
        return "/site/search";
    }
}
