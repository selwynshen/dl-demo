
package code.selwyn.dl.example.controller;

import code.selwyn.dl.annotation.RedisLocked;
import com.github.alturkovic.lock.Interval;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author Selwyn
 * @since 2021/5/6
 */
@RestController
@RequestMapping("/lock")
@Slf4j
public class LockController {

    @GetMapping("/{bizName}")
    @RedisLocked(expression = "#bizName", timeout = @Interval(value = "80", unit = TimeUnit.SECONDS), expiration = @Interval(value="80", unit = TimeUnit.SECONDS))
    public Integer lock(@PathVariable String bizName) throws Exception {
        Thread.sleep(1000 * 5);
        //if (1==1) throw new RuntimeException("surprise?");
        return 1;
    }

}
