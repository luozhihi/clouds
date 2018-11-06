package com.lzh.log.controller;

import com.lzh.log.Entity.Log;
import com.lzh.log.Entity.User;
import com.lzh.log.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("log")
public class LogController {
    private Logger logger = LoggerFactory.getLogger(LogController.class);
    @Autowired
    private LogService logService;
    @RequestMapping(value = "insert",method = RequestMethod.POST)
    @ResponseBody
    public String addLog(@RequestBody Log log){
        logger.info("写入日志");
        log.setTime(new Date());
        User user = new User();
        user.setId(1L);
        log.setUser(user);
        logService.addLog(log);

        return "success";
    }
}
