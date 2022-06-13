package com.nowcoder.community.service.impl;

import com.nowcoder.community.service.DateService;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DateServiceImpl implements DateService {

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");


    @Override
    public void recordUV(String ip) {
        String uvKey = RedisKeyUtil.getUVKey(df.format(new Date()));
        System.out.println(uvKey);
        System.out.println(redisTemplate.opsForHyperLogLog().add(uvKey,ip));
    }
    @Override
    public long calcualteUV(Date start,Date end){
        if(start==null ||end==null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        List<String> keyList=new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while(!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getUVKey(df.format(calendar.getTime()));
            keyList.add(key);
            calendar.add(calendar.DATE,1);
        }
        String redisKey = RedisKeyUtil.getUVKey(df.format(start), df.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey,keyList.toArray());

        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }
    @Override
    public void recordDAU(int userId){
        String dauKey = RedisKeyUtil.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(dauKey,userId,true);
    }
    @Override
    public long caculateDAU(Date start,Date end){
        if(start==null ||end==null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        List<byte[]> keyList=new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while(!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getDAUKey(df.format(calendar.getTime()));
            keyList.add(key.getBytes());
            calendar.add(calendar.DATE,1);
        }
        //进行or
        return (long)redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                String dauKey = RedisKeyUtil.getDAUKey(df.format(start), df.format(end));
                redisConnection.bitOp(RedisStringCommands.BitOperation.OR,dauKey.getBytes(),keyList.toArray(new  byte[0][0]));
                return redisConnection.bitCount(dauKey.getBytes());
            }
        });
    }
}
