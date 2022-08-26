
package code.selwyn.dl.annotation;

import code.selwyn.dl.impl.SimpleRedisLock;
import com.github.alturkovic.lock.Interval;
import com.github.alturkovic.lock.Locked;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Locked(type = SimpleRedisLock.class)
public @interface RedisLocked {

    @AliasFor(annotation = Locked.class)
    boolean manuallyReleased() default false;

    @AliasFor(annotation = Locked.class)
    String storeId() default "lock";

    @AliasFor(annotation = Locked.class)
    String prefix() default "";

    @AliasFor(annotation = Locked.class)
    String expression() default "#executionPath";

    @AliasFor(annotation = Locked.class)
    Interval expiration() default @Interval(value = "10", unit = TimeUnit.SECONDS);

    /**
     * 不能设置为0，否则没法执行主方法
     * @return
     */
    @AliasFor(annotation = Locked.class)
    Interval timeout() default @Interval(value = "1", unit = TimeUnit.SECONDS);

    /**
     * 默认没机会重试
     * @return
     */
    @AliasFor(annotation = Locked.class)
    Interval retry() default @Interval(value = "1000");

    /*@AliasFor(annotation = Locked.class)
    Interval refresh() default @Interval(value = "0");*/
}

