package com.lzh.log;

import com.lzh.log.Entity.Log;
import com.lzh.log.Entity.User;
import com.lzh.log.dao.LogMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.ws.rs.HttpMethod;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LogApplicationTests {
	@Autowired
	private LogMapper logMapper;
	@Test
	public void contextLoads() {
        Log log =new Log();
        User user = new User();
        user.setId(1L);
        log.setUser(user);
        log.setTime(new Date());
        log.setMarker("123");
        log.setMethod(HttpMethod.GET);
        log.setOperation("operation");
        log.setPath("/log");
        logMapper.addLog(log);
    }

}
