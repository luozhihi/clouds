package com.lzh.log.utils;

import com.alibaba.fastjson.JSON;
import com.lzh.log.Entity.Log;
import com.lzh.log.Entity.User;
import com.lzh.log.service.LogService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
public class LogUtils {
    private static final Logger logger = LoggerFactory.getLogger(LogUtils.class);
    @Autowired
    private LogService logService;

    @KafkaListener(topics = {"log"})
    public void listen(ConsumerRecord<?,?> record){
        logger.info("监听到消息");
        Optional<?> kafkaMessage = Optional.ofNullable(record);

        if(kafkaMessage.isPresent()){
            ConsumerRecord message = (ConsumerRecord)kafkaMessage.get();
            String logJson = (String) message.value();
            Log log = JSON.parseObject(logJson, Log.class);

            log.setTime(new Date());

            User user = new User();
            user.setId(2L);

            log.setUser(user);

            logService.addLog(log);
            logger.info("添加日志成功");
        }
    }
}
