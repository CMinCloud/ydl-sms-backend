package com.ydl.test;

import com.ydl.sms.SmsManageApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

/**
 * @Author：CM
 * @Package：com.ydl.test
 * @Project：ydl-sms-backend
 * @name：redisApi
 * @Date：2023/5/21 19:36
 * @Filename：redisApi
 */
@RunWith(SpringRunner.class)    //将普通的类变成一个Junit的测试类
@SpringBootTest(classes = SmsManageApplication.class)
public class redisApi {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;





    @org.junit.jupiter.api.Test
    void set1(){
        redisTemplate.opsForValue().set("cm:user:name01","弗洛伊德");
//
//        Object o = template.opsForValue().get("cm:user:name01");
//        System.out.println(o);

//        template.opsForValue().set("cm:user:05",new User("虎哥",22));

    }

    @org.junit.jupiter.api.Test
    void test4Hash(){

        stringRedisTemplate.opsForHash().put("cm:user:01", "name", "虎哥");
        stringRedisTemplate.opsForHash().put("cm:user:01", "age", "21");

        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries("cm:user:01");
        System.out.println(entries);
    }



    @org.junit.jupiter.api.Test
    void set() {
        ValueOperations<String,String> ops4String = redisTemplate.opsForValue();  //操作String类型数据
        HashOperations ops4Hash = redisTemplate.opsForHash();  //操作Hash类型数据
        ListOperations ops4List = redisTemplate.opsForList();   //操作List类型数据
        SetOperations ops4Set = redisTemplate.opsForSet();    //操作Set类型数据
        ZSetOperations ops4ZSet = redisTemplate.opsForZSet();  //操作SortedSet类型数据
        ops4String.set("testString","ttttt");
        ops4Hash.put("testHash","HashKey1","hhhhh");
    }


    @org.junit.jupiter.api.Test
    void get() {
        ValueOperations ops4String = redisTemplate.opsForValue();  //操作String类型数据
        HashOperations ops4Hash = redisTemplate.opsForHash();  //操作Hash类型数据
        ListOperations ops4List = redisTemplate.opsForList();   //操作List类型数据
        SetOperations ops4Set = redisTemplate.opsForSet();    //操作Set类型数据
        ZSetOperations ops4ZSet = redisTemplate.opsForZSet();  //操作SortedSet类型数据

        String testString = (String) ops4String.get("cm:userList:2");
        String o = (String) ops4Hash.get("testHash", "HashKey1");
        System.out.println(testString);
        System.out.println(o);

    }

    @org.junit.jupiter.api.Test
    void putHash() {
        HashOperations hash = redisTemplate.opsForHash();
        hash.put("Key", "hashKey1", 123456);
        hash.put("Key", "hashKey2", 456789);
        hash.put("Key", "hashKey3", 888888);
    }

    @Test
    void getHash(){
        HashOperations ops = redisTemplate.opsForHash();
        System.out.println(ops.get("Key","hashKey1"));
    }
}
