package com.lzh.client2.controller;

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
    @OPSLog(operation = "查看配置",path = "/config",method = "get",marker = "暂无")
    public String test(){
        return config+":8084";
    }
}
