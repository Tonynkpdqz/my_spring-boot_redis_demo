package com.nkpdqz.my_springboot_redis_demo.controller;

import com.nkpdqz.my_springboot_redis_demo.bean.User;
import com.nkpdqz.my_springboot_redis_demo.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/redis/")
public class Controller {

    @Autowired
    private RedisUtils redisUtils;

    @GetMapping("set")
    public User setUser(String key){
        User user = new User();
        user.setId(1L);
        user.setGuid("1");
        user.setName("nkpdqz");
        user.setAge("22");
        user.setCreateTime(new Date());
        redisUtils.setCache(key,user);
        return user;
    }

    @GetMapping("set2")
    public User setUser2(String key,String item){
        User user = new User();
        user.setId(2L);
        user.setGuid("2");
        user.setName("nl");
        user.setAge("22");
        user.setCreateTime(new Date());
        redisUtils.hashSet(key,item,user);
        return user;
    }

    @GetMapping("get")
    public User getUser(String key){
        Object cache = redisUtils.getCache(key);
        return (User) cache;
    }

    @GetMapping("get2")
    public User getUser2(String key,String item){
        Object cache = redisUtils.hashGet(key,item);
        return (User) cache;
    }

}
