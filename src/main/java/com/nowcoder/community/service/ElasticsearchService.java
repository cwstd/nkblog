package com.nowcoder.community.service;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface ElasticsearchService {

    /**
     * 在 Elasticsearch增加帖子
     * @param discussPost
     */
    void saveDiscusspost(DiscussPost discussPost);

    /**
     * 在 Elasticsearch删除帖子
     * @param id
     */
    void deleteDiscusspost(int id);

    /**
     * 搜索
     * @param keword
     * @param current
     * @param limit
     * @return
     */
    Page<DiscussPost> searchDiscussPost(String keword,int current,int limit);
}
