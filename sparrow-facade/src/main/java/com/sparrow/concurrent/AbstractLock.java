package com.sparrow.concurrent;

import com.sparrow.utility.DateTimeUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by harry on 2018/1/26.
 */
public abstract class AbstractLock {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected abstract Boolean readLock(String key);

    public boolean retryAcquireLock(String key) {
        Boolean lock = this.readLock(key);
        int times = 1;
        int timeout = 0;
        while (lock != null) {
            lock = this.readLock(key);
            try {
                if (timeout < 1024) {
                    timeout = 1 << times++;
                }
                Thread.sleep(timeout);
                logger.debug("lock {} timeout {} at [{}] {}",key,timeout, DateTimeUtility.getFormatCurrentTime(),System.currentTimeMillis());
            } catch (InterruptedException ignore) {
            }
        }
        return true;
    }
}
