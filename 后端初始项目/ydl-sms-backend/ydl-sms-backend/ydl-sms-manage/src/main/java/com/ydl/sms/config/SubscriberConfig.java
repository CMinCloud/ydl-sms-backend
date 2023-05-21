package com.ydl.sms.config;

import com.ydl.sms.listener.MyListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * 订阅发布模式的容器配置
 */
@Configuration    //配置类，把一些bean对象在容器启动时new出来放到容器中
@AutoConfigureAfter({MyListener.class})   //表示在哪一个类下进行配置呢
public class SubscriberConfig {

    @Autowired
    private MyListener myListener;

    /**
     * 创建消息监听容器
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisMessageListenerContainer getRedisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        //写死的
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);

        //可以添加多个监听订阅频道
        //当前监听的是通道：MYTOPIC
        redisMessageListenerContainer.
                addMessageListener(new MessageListenerAdapter(myListener), new PatternTopic("MYTOPIC"));//表示往MYTOPIC通道内发布一个消息就可被监听得到

        return redisMessageListenerContainer;
    }
}
