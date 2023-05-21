package com.ydl.test;

import com.ydl.sms.SmsManageApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)    //将普通的类变成一个Junit的测试类
@SpringBootTest(classes = SmsManageApplication.class)   // 将主类给到，当测试类当中的方法可以使用redis的模板类
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    /*通过通道发送短信*/
    @Test
    public void testSendToRedis(){
        redisTemplate.convertAndSend("MYTOPIC", "im itnanaoshi123");
    }

    @Test
    public void testOpsForList(){

    }

}
