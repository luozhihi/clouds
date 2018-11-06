package com.lzh.annocation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OPSLog {
    // 操作名称
    String operation();

    // 操作的接口路径
    String path();

    // 备注
    String marker();

    //请求方式
    String method();
}
