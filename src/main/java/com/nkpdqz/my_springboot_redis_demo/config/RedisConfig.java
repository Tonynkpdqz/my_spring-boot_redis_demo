package com.nkpdqz.my_springboot_redis_demo.config;
import com.nkpdqz.my_springboot_redis_demo.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

@Configuration
@PropertySource("classpath:config/redis.properties")
public class RedisConfig {

    @Value("${redis.hostname}")
    private String host;
    @Value("${redis.port}")
    private Integer port;
    @Value("${redis.timeout}")
    private Integer timeout;

    @Value("${redis.maxIdle}")
    private Integer maxIdle;
    @Value("${redis.maxTotal}")
    private Integer maxTotal;

    @Value("${redis.maxWaitMillis}")
    private Integer maxWaitMillis;

    @Value("${redis.minEvictableIdleTimeMillis}")
    private Integer minEvictableIdleTimeMillis;

    @Value("${redis.numTestsPerEvictionRun}")
    private Integer numTestsPerEvictionRun;

    @Value("${redis.timeBetweenEvictionRunsMillis}")
    private long timeBetweenEvictionRunsMillis;

    @Value("${redis.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${redis.testWhileIdle}")
    private boolean testWhileIdle;

    //集群配置属性
    @Value("${spring.redis.cluster.nodes}")
    private String clusterNodes;
    @Value("${spring.redis.cluster.max-redirects}")
    private Integer maxRedirectsac;


    //哨兵配置属性
    @Value("${redis.sentinel.host1}")
    private String senHost1;
    @Value("${redis.sentinel.port1}")
    private Integer senPort1;


    //JedisPoolConfig连接池
    @Bean
    public JedisPoolConfig jedisPoolConfig(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 最大空闲数
        jedisPoolConfig.setMaxIdle(maxIdle);
        // 连接池的最大数据库连接数
        jedisPoolConfig.setMaxTotal(maxTotal);
        // 最大建立连接等待时间
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        // 逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
        jedisPoolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        // 每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
        jedisPoolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        // 逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        // 是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        // 在空闲时检查有效性, 默认false
        jedisPoolConfig.setTestWhileIdle(testWhileIdle);
        return jedisPoolConfig;
    }

    /*


    //集群配置
    @Bean
    public RedisClusterConfiguration redisClusterConfiguration(){
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        //Set<RedisNode> clusterNodes;
        String [] servers = clusterNodes.split(",");
        Set<RedisNode> nodes = new HashSet<>();
        for (String ipPort:servers) {
            String[] ipAndPort = ipPort.split(":");
            nodes.add(new RedisNode(ipAndPort[0].trim(),Integer.parseInt(ipAndPort[1])));
        }
        redisClusterConfiguration.setClusterNodes(nodes);
        redisClusterConfiguration.setMaxRedirects(maxRedirectsac);
        return redisClusterConfiguration;
    }
    //集群配置
    public JedisConnectionFactory jedisClusterConnectionFactory(JedisPoolConfig jedisPoolConfig,RedisClusterConfiguration redisClusterConfiguration){
        return new JedisConnectionFactory(redisClusterConfiguration,jedisPoolConfig);
    }


    //哨兵配置
    @Bean
    public RedisSentinelConfiguration sentinelConfiguration(){
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
        //配置master
        RedisNode redisNode = new RedisNode(host,port);
        redisNode.setName("myMaster");
        redisSentinelConfiguration.master(redisNode);
        //配置哨兵
        RedisNode senRedisNode = new RedisNode(senHost1,senPort1);
        Set<RedisNode> redisNodeSet = new HashSet<>();
        redisNodeSet.add(senRedisNode);
        redisSentinelConfiguration.setSentinels(redisNodeSet);
        return redisSentinelConfiguration;
    }

    @Bean
    public JedisConnectionFactory jedisSentinelConnectionFactory(JedisPoolConfig jedisPoolConfig,RedisSentinelConfiguration sentinelConfig) {
        return new JedisConnectionFactory(sentinelConfig,jedisPoolConfig);
    }

     */

    //单机版配置
    @Bean
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig){
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig);
        jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
        jedisConnectionFactory.setHostName(host);
        jedisConnectionFactory.setPort(port);
        //jedisConnectionFactory.setPassword("password");
        jedisConnectionFactory.setTimeout(timeout);
        return jedisConnectionFactory;
    }


    @Bean
    public RedisTemplate<String,Object> functionDomainRedisTemplate(JedisConnectionFactory jedisConnectionFactory){
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        initDomainRedisTemplate(redisTemplate,jedisConnectionFactory);
        return redisTemplate;
    }


    //设置数据存入redis的序列化方式，并开启事务
    private void initDomainRedisTemplate(RedisTemplate<String, Object> redisTemplate, RedisConnectionFactory factory) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        //开启事务
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.setConnectionFactory(factory);
    }

    @Bean
    public RedisUtils redisUtil(RedisTemplate<String,Object> template){
        RedisUtils redisUtils = new RedisUtils();
        redisUtils.setRedisTemplate(template);
        return redisUtils;
    }
}
