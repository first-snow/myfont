package work.cxlm.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 内存实现的缓存
 * created 2020/11/8 22:15
 *
 * @author johnniang
 * @author Chiru
 */
@Slf4j
public class InMemoryCacheStore extends AbstractStringCacheStore {

    /**
     * 清理器的工作周期
     */
    private final static long PERIOD = 60 * 1000;

    /**
     * 缓存实际存储的 map
     */
    private final static ConcurrentHashMap<String, CacheWrapper<String>> CACHE_CONTAINER = new ConcurrentHashMap<>();

    // 执行自动清理任务的定时器
    private final Timer timer;

    private final Lock lock = new ReentrantLock();

    public InMemoryCacheStore() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new CacheExpiryCleaner(), 0, PERIOD);
    }

    @PreDestroy
    public void preDestroy() {
        log.debug("取消定时任务");
        timer.cancel();
        CACHE_CONTAINER.clear();
    }

    @Override
    Optional<CacheWrapper<String>> gerInternal(String key) {
        Assert.hasText(key, "缓存键不能为空");

        return Optional.ofNullable(CACHE_CONTAINER.get(key));
    }

    @Override
    void putInternal(String key, CacheWrapper<String> value) {
        Assert.hasText(key, "缓存键不能为空");
        Assert.notNull(value, "缓存值不能为 null");

        CacheWrapper<String> cacheToPut = CACHE_CONTAINER.put(key, value);
        log.debug("已添加缓存：key: [{}], value: [{}], result: [{}]", key, value, cacheToPut);
    }

    @Override
    Boolean putInternalIfAbsent(String key, CacheWrapper<String> value) {
        Assert.hasText(key, "缓存键不能为空");
        Assert.notNull(value, "缓存值不能为 null");

        // 加锁范围疑似过大
        lock.lock();

        try {
            Optional<String> valueOptional = get(key);

            if (valueOptional.isPresent()) {
                log.warn("添加缓存失败，键 [{}] 已存在", key);
                return false;
            }

            putInternal(key, value);
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void delete(String key) {
        Assert.hasText(key, "缓存键不能为空");

        CACHE_CONTAINER.remove(key);
        log.debug("移除缓存：[{}]", key);
    }

    /**
     * Cache Cleaner
     */
    private class CacheExpiryCleaner extends TimerTask {

        @Override
        public void run() {
            CACHE_CONTAINER.keySet().forEach(k -> {
                if (InMemoryCacheStore.this.get(k).isEmpty()) {
                    log.debug("已删除缓存：[{}]", k);
                }
            });
        }
    }
}
