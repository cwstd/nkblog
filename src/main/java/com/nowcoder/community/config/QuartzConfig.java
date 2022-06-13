package com.nowcoder.community.config;

import com.nowcoder.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzConfig {
    @Bean
    public JobDetailFactoryBean scoreRefreshJobDetail(){
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(PostScoreRefreshJob.class);
        jobDetailFactoryBean.setName("scoreRefresh");
        jobDetailFactoryBean.setGroup("communityGroup");
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setRequestsRecovery(true);

        return jobDetailFactoryBean;
    }
    @Bean
    public SimpleTriggerFactoryBean scoreRefreshTrigger(JobDetail alphaJobDetail){
        SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
        simpleTriggerFactoryBean.setJobDetail(alphaJobDetail);
        simpleTriggerFactoryBean.setName("scoreRefreshTrigger");
        simpleTriggerFactoryBean.setGroup("communityGroup");
        simpleTriggerFactoryBean.setRepeatInterval(1000*60*5);
        simpleTriggerFactoryBean.setJobDataMap(new JobDataMap());
        return simpleTriggerFactoryBean;
    }
}
