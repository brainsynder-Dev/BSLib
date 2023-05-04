package lib.brainsynder.storage;

import com.google.common.base.Ticker;

import java.util.concurrent.TimeUnit;

public class Cache<E> {
    private final Ticker ticker = Ticker.systemTicker();
    private long targetTime = -1;
    private E item;

    /**
     * "Set the item to be cached, and set the time at which it will expire."
     *
     * @param item        The item to be cached.
     * @param expireDelay The amount of time to wait before the item expires.
     * @param expireUnit  The time unit of the expireDelay parameter.
     */
    public void setCacheItem(E item, long expireDelay, TimeUnit expireUnit) {
        targetTime = this.ticker.read() + TimeUnit.NANOSECONDS.convert(expireDelay, expireUnit);
        this.item = item;
    }

    /**
     * If the current time is greater than the target time, then the item is null and the target time is -1. Otherwise, the
     * item is not null
     *
     * @return The boolean value of whether or not the item is null.
     */
    public boolean hasCacheItem() {
        long current = this.ticker.read();
        if (targetTime <= current) {
            item = null;
            targetTime = -1;
            return false;
        }

        return (item != null);
    }

    /**
     * Returns the item stored in the cache.
     *
     * @return The item that is being cached.
     */
    public E getCacheItem() {
        return item;
    }
}
