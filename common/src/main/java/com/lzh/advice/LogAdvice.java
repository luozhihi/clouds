package com.lzh.advice;

import com.alibaba.fastjson.JSONObject;
import com.lzh.annocation.OPSLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Aspect
@Component
public class LogAdvice {

    private static final Logger logger = LoggerFactory.getLogger(LogAdvice.class);
    @Autowired
    public RestTemplate restTemplate;
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;
    @Around("@annotation(opsLog)")
    public Object doAfterReturning(ProceedingJoinPoint pjp, OPSLog opsLog) {
//        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        JSONObject jsonObject = new JSONObject();
        logger.info("执行AOP");

        String marker = opsLog.marker();
        String operation = opsLog.operation();
        String method = opsLog.method();
        String path = opsLog.path();

        jsonObject.put("marker", marker);
        jsonObject.put("operation", operation);
        jsonObject.put("method", method);
        jsonObject.put("path", path);

      /*  HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), httpHeaders);

        String url = "http://localhost:8085/log/insert";
        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
            HttpStatus statusCode = responseEntity.getStatusCode();
            String body = responseEntity.getBody();
            logger.info(statusCode.toString());
            logger.info(body);
        } catch (Exception e) {
            logger.error(e.toString());
        }*/
      // 通过kafka发送消息，插入日志
        try{
            kafkaTemplate.send("log",jsonObject.toJSONString());
        }catch (Exception e){
            logger.error(e.toString());
        } finally {
            try {
                return pjp.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return null;
    }

}
