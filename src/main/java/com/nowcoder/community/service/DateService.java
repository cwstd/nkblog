package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public interface DateService {


    void recordUV(String ip);
    long calcualteUV(Date start,Date end);
    void recordDAU(int userId);
    long caculateDAU(Date start,Date end);

}
