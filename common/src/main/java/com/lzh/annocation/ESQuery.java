package com.lzh.annocation;

import com.lzh.utils.ESQueryBuilder.ESQueryType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @ClassName: ESQuery
 * @Description: 配置ES查询注解
 * @author chenxiaojian
 * @date 2018年1月9日 下午4:09:30
 *
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface ESQuery {

    /**
     * 配置ES document属性名
     * 不配置则默认字段名
     */
    public String key() default "";

    /**
     * 包括：
     *  term  默认精确匹配字段
     * 	terms 多值查询
     * 	rangeStart, rangeEnd 范围查询
     *  match 分词查询
     */
    public ESQueryType type() default ESQueryType.TERM;


    /**
     * type为terms和range时, 对值进行拆分
     * 默认,
     */
    public String split() default ",";

}

