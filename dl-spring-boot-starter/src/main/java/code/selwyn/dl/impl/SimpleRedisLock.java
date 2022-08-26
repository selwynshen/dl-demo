
package code.selwyn.dl.impl;

import com.github.alturkovic.lock.AbstractSimpleLock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.util.Pair;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 简易redis锁实现类（底层：redisson）
 */
@Slf4j
public class SimpleRedisLock extends AbstractSimpleLock {

    private final RedissonClient redissonClient;

    private final Function<Pair<String, String>, String> redisKeyGenerator = pair -> pair.getFirst() + ":" + pair.getSecond();

    public SimpleRedisLock(Supplier<String> tokenSupplier, RedissonClient redissonClient) {
        super(tokenSupplier);
        this.redissonClient = redissonClient;
    }

    @SneakyThrows
    @Override
    protected String acquire(final String key, final String storeId, final String token, final long expiration) {
        String redisKey = redisKeyGenerator.apply(Pair.of(storeId, key));
        RLock rLock = redissonClient.getLock(redisKey);
        boolean lockFlag = rLock.tryLock(0, expiration, TimeUnit.MILLISECONDS);
        log.info("acquire(redisKey: '{}'): {}", redisKey, lockFlag);
        return lockFlag ? token : null;
    }

    @Override
    protected boolean release(final String key, final String storeId, final String token) {
        String redisKey = redisKeyGenerator.apply(Pair.of(storeId, key));
        RLock rLock = redissonClient.getLock(redisKey);
        if (rLock.isLocked()) {
            rLock.unlock();
            log.info("release(redisKey: '{}'): {}", redisKey, true);
        }
        return true;
    }

    @SneakyThrows
    @Override
    protected boolean refresh(final String key, final String storeId, final String token, final long expiration) {
        //使用redisson不好实现，放弃
        /*String redisKey = redisKeyGenerator.apply(Pair.of(storeId, key));
        RLock rLock = redissonClient.getLock(redisKey);
        if (rLock instanceof RedissonLock) {
            RedissonLock redissonLock = (RedissonLock) rLock;
            //锁着才需要进行刷新，否则会造成脏数据
            if (rLock.isLocked()) {
                boolean flag = redissonLock.expire(expiration, TimeUnit.MILLISECONDS);
                log.info("refresh(redisKey: {}): {}", redisKey, flag);
                return flag;
            }
        }*/
        return true;
    }
}
