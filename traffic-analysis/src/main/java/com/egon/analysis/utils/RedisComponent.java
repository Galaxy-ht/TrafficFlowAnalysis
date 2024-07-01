package com.egon.analysis.utils;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Tao
 * @since 2023/08/20
 */
@Component
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;

}
