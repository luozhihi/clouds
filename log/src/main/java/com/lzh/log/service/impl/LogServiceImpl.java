package com.lzh.log.service.impl;

import com.lzh.log.Entity.Log;
import com.lzh.log.dao.LogMapper;
import com.lzh.log.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {

    private Logger logger = LoggerFactory.getLogger(LogService.class);
    @Autowired
    private LogMapper logMapper;

    @Override
    public void addLog(Log log) {
        logMapper.addLog(log);
    }
}
