package com.nkpdqz.my_springboot_redis_demo.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisUtils {

    private RedisTemplate<String,Object> redisTemplate;

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    //通用方法

    //指定缓存失效时间
    public boolean expire(String key,long time){
        if (time>0) {
            redisTemplate.expire(key,time, TimeUnit.SECONDS);
        }
        return true;
    }

    //获取缓存失效时间
    public long getExpire(String key){
        return redisTemplate.getExpire(key,TimeUnit.SECONDS);
    }

    //判断key是否存在
    public boolean hasKey(String key){
        try {
            return redisTemplate.hasKey(key);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //删除缓存
    public void deleteCache(String ... key){
        if (key!=null&&key.length>0){
            if (key.length==1){
                redisTemplate.delete(key[0]);
            }else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    //String操作

    //缓存获取
    public Object getCache(String key){
        return key==null?null:redisTemplate.opsForValue().get(key);
    }

    //set缓存
    public boolean setCache(String key,Object data){
        try {
            redisTemplate.opsForValue().set(key, data);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //set缓存并设置过期时间
    public boolean setCacheWithTime(String key,Object data,long time){
        try {
            if(time>0){
                redisTemplate.opsForValue().set(key,data,time);
            }else {
                setCache(key,data);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //递增
    public long incr(String key,long delta){
        if (delta<0){
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key,delta);
    }

    //递减
    public long decr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }


    //Map操作

    //HashGet
    public Object hashGet(String key,String item){
        return redisTemplate.opsForHash().get(key,item);
    }

    //获取hashkey对应的所有key-value对
    public Map<Object,Object> hashMapGet(String key){
        return redisTemplate.opsForHash().entries(key);
    }

    //put一个hash(多组键值对)
    public boolean hashMapSet(String key,Map<String,Object> map){
        try {
            redisTemplate.opsForHash().putAll(key,map);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //put多个键值对并设置过期时间
    public boolean hashMapSetWithTime(String key,Map<String,Object> map,long time){
        try {
            redisTemplate.opsForHash().putAll(key,map);
            if (time>0){
                expire(key,time);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //向一张hash表中放入数据，若不存在将创建
    public boolean hashSet(String key,String item,Object value){
        try {
            redisTemplate.opsForHash().put(key,item,value);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //向一张hash表中放入数据，若不存在将创建，同时设置（更新）过期时间
    public boolean hashSetWithTime(String key,String item,Object value,long time){
        try {
            redisTemplate.opsForHash().put(key,item,value);
            if (time>0){
                expire(key,time);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //删除hash表中的值
    public void hashDelete(String key,Object ... item){
        redisTemplate.opsForHash().delete(key,item);
    }

    //判断hash表中是否有该项的值
    public boolean hasHashKey(String key,String item){
        return redisTemplate.opsForHash().hasKey(key,item);
    }

    //hash递增，若不存在就会创建一个，并把新增后的值返回
    public double hashIncr(String key,String item,double by){
        return redisTemplate.opsForHash().increment(key,item,by);
    }

    //递减
    public double hdecr(String key, String item,double by){
        return redisTemplate.opsForHash().increment(key, item,-by);
    }


    //Set操作

    //根据Key获取Set中所有值
    public Set<Object> getSet(String key){
        return redisTemplate.opsForSet().members(key);
    }

    //根据value从set中查询，看是否存在
    public boolean hasSetKey(String key,Object value){
        try {
            return redisTemplate.opsForSet().isMember(key,value);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //把数据放入缓存,返回添加的个数
    public long setSet(String key,Object ... values){
        try{
            return redisTemplate.opsForSet().add(key,values);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    //把数据放入缓存,返回添加的个数,并设置过期时间
    public long setSetWithTime(String key,long time,Object ... values){
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time>0){
                expire(key,time);
            }
            return count;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    //获取set的长度
    public long setGetLength(String key){
        try {
            return redisTemplate.opsForSet().size(key);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    //删除值为value的元素
    public long setRemove(String key,Object ... values){
        try {
            return redisTemplate.opsForSet().remove(key,values);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }



    //List操作

    //获取list缓存
    public List<Object> listGet(String key,long start,long end){
        try {
            return redisTemplate.opsForList().range(key,start,end);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //获取list缓存长度
    public long listGetLength(String key){
        try{
            return redisTemplate.opsForList().size(key);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     * @param key 键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key,long index){
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //把list放进缓存(全部)
    public boolean listSet(String key,List<Object> list){
        try {
            redisTemplate.opsForList().rightPushAll(key,list);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存 (部分)
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean listElementSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存(部分)，并设置过期时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public boolean listElementSetWithTime(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存（全部），并设置过期时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public boolean listSetWithTime(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //根据索引修改list中的某条数据
    public boolean listElementUpdateWithIndex(String key,long index,Object value){
        try {
            redisTemplate.opsForList().set(key,index,value);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key,long count,Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
