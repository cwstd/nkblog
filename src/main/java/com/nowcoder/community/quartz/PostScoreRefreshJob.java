package com.nowcoder.community.quartz;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import com.nowcoder.community.util.SensitiveFilter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostScoreRefreshJob implements Job, CommunityConstant {


    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticsearchService elasticsearchService;


    private static  final Logger logger= LoggerFactory.getLogger(PostScoreRefreshJob.class);

    //牛客纪元
    private static final Date parse;

    static {
        try {
            parse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("牛客纪元",e);
        }
    }
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        String postscoreKey = RedisKeyUtil.getPostscoreKey();
        BoundSetOperations boundSetOperations = redisTemplate.boundSetOps(postscoreKey);
        if(boundSetOperations.size()==0){
            logger.info("任务取消没有帖子");
            return;
        }
        logger.info("任务开始，正在刷新帖子分数："+boundSetOperations.size());
        while (boundSetOperations.size()!=0){
            this.refresh((Integer) boundSetOperations.pop());
        }
        logger.info("帖子分数刷新完毕");

    }
    private void refresh(int id){
        DiscussPost discussPost = discussPostService.selectDiscussPostOne(id);
        if(discussPost==null){
            logger.error("帖子不存在");
            return;
        }
        boolean b = discussPost.getStatus() == 1;
        int commentCount = discussPost.getCommentCount();
        long likeCount = likeService.likeCount(CommunityConstant.ENTITY_TYPE_POST, id);

        double w=(b?75:0)+commentCount*10+likeCount*2;
        //分数=帖子的权重+距离的天数
        double score = Math.log10(Math.max(w,1))+(discussPost.getCreateTime().getTime()-parse.getTime())/(1000*3600*24);
        discussPostService.updateScore(id,score);
        discussPost.setScore(score);
        elasticsearchService.saveDiscusspost(discussPost);


    }
}
