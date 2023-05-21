package com.ydl.sms.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * @Author：CM
 * @Package：com.ydl.sms.listener
 * @Project：ydl-sms-backend
 * @name：MyListener
 * @Date：2023/5/21 18:00
 * @Filename：MyListener
 */

@Component
@Slf4j
public class MyListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] bytes) {
        log.info("收到了消息：" + message);
    }
}
