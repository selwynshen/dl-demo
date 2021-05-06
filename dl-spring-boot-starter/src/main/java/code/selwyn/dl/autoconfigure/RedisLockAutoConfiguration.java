/**
 * Hrfax Inc.
 * Copyright (c) 2020-2066 All Rights Reserved.
 */
package code.selwyn.dl.autoconfigure;

import code.selwyn.dl.impl.SimpleRedisLock;
import com.github.alturkovic.lock.Lock;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

/**
 * @author Selwyn
 * @since 2021/5/6
 */
@ConditionalOnClass(value={RedissonAutoConfiguration.class, SimpleRedisLock.class})
@AutoConfigureAfter(RedissonAutoConfiguration.class)
public class RedisLockAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Lock simpleRedisLock(final RedissonClient redissonClient) {
        return new SimpleRedisLock(() -> UUID.randomUUID().toString(), redissonClient);
    }


}
