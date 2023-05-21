package com.ydl.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydl.sms.dto.ConfigDTO;
import com.ydl.sms.entity.ConfigEntity;
import com.ydl.sms.mapper.ConfigMapper;
import com.ydl.sms.model.ServerTopic;
import com.ydl.sms.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

import static com.ydl.sms.model.ServerTopic.INIT_CONNECT;

/**
 * 通道配置表
 */
@Service
@Slf4j
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, ConfigEntity> implements ConfigService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public ConfigEntity getByName(String name) {
        LambdaUpdateWrapper<ConfigEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ConfigEntity::getName, name);
        return this.getOne(wrapper);
    }

    @Override
    public void getNewLevel(ConfigDTO entity) {
        LambdaUpdateWrapper<ConfigEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ConfigEntity::getIsEnable, 1);
        wrapper.eq(ConfigEntity::getIsActive, 1);
        wrapper.orderByDesc(ConfigEntity::getLevel);
        wrapper.last("limit 1");
        ConfigEntity configEntity = this.getOne(wrapper);
        if (configEntity == null) {
            entity.setLevel(1);
        } else {
            entity.setLevel(configEntity.getLevel() + 1);
        }
    }

    @Override
    public void sendUpdateMessage() {
        // TODO 发送消息，通知短信发送服务更新内存中的通道优先级
        //1 获取存活发送端
        Map map = redisTemplate.opsForHash().entries("SERVER_ID_HASH");
        log.info("发送端有：" + map);
//        获取当前时间
        long currentTimeMillis = System.currentTimeMillis();
        for (Object key : map.keySet()) {
            // 获取存放的ttl并比较
            long lastTime = Long.parseLong(map.get(key).toString());
//            Duration duration = Duration.between(lastTime,currentTimeMillis);
            if (currentTimeMillis - lastTime < (1000 * 60 * 5)) {       //说明该服务未下线，可以发送
                redisTemplate.delete("listForConnect");
//                发布消息
                redisTemplate.convertAndSend("TOPIC_HIGH_SERVER",
                        ServerTopic.builder()       //使用建造者模式显示注入参数
                                .option(INIT_CONNECT)
                                .value(key.toString())
                                .build().toString());
                log.info("发送消息通知短信发送服务重新构建通道");
                return; //只要有一个可以发送的客户端（短信通信商）就跳出服务
            }
//            超过五分钟没有更新TTL：说明该服务可能下线了,可以剔除
        }
    }
}
