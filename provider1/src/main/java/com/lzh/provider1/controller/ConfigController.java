package com.lzh.provider1.controller;

import com.lzh.annocation.OPSLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("config")
@RefreshScope
public class ConfigController {
    @Value("${config}")
    private String config;

    @RequestMapping("")
    @ResponseBody
    @OPSLog(operation = "调用provider1的接口",path = "test1/config",method = "get",marker = "8082端口")
    public String test(){
        return config+":8082";
    }
}
