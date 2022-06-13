package com.nowcoder.community.service.impl;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public int addDiscussPost(DiscussPost discussPost) {
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        return discussPostMapper.insertDiscussPost(discussPost);
    }

    @Override
    public DiscussPost selectDiscussPostOne(int id) {
        return discussPostMapper.selectDiscussPostByid(id);
    }

    @Override
    public int addCommentCount(int id, int commentCount) {
        return discussPostMapper.updateDiscussCommentCount(id, commentCount);
    }

    @Override
    public int ReStatus(int id, int status) {
        return discussPostMapper.updateStatus(id,status);
    }

    @Override
    public int ReType(int id, int tyoe) {
        return discussPostMapper.updateType(id,tyoe);
    }

    @Override
    public int updateScore(int id, double score) {

        return discussPostMapper.updateScore(id, score);
    }
}
