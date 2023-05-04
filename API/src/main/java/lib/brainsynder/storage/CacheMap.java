package lib.brainsynder.storage;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Ticker;
import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class CacheMap<K, V> {
    private final Map<K, ExpireEntry> keyLookup;
    private final PriorityQueue<ExpireEntry> expireQueue;
    private final Map<K, V> valueView;
    private final Ticker ticker;

    public CacheMap() {
        this(Ticker.systemTicker());
    }

    public CacheMap(Ticker ticker) {
        this.keyLookup = new HashMap();
        this.expireQueue = new PriorityQueue();
        this.valueView = Maps.transformValues(this.keyLookup, new Function<ExpireEntry, V>() {
            public V apply(CacheMap<K, V>.ExpireEntry entry) {
                return entry.value;
            }
        });
        this.ticker = ticker;
    }

    /**
     * @param key The Key you are searching for
     * @return
     */
    public V get(K key) {
        this.evict();
        CacheMap.ExpireEntry entry = this.keyLookup.get(key);
        return entry != null ? (V) entry.value : null;
    }

    public V put(K key, V value, long expireDelay, TimeUnit expireUnit) {
        Preconditions.checkNotNull(expireUnit, "expireUnit cannot be NULL");
        Preconditions.checkState(expireDelay > 0L, "expireDelay cannot be equal or less than zero.");
        this.evict();
        CacheMap.ExpireEntry entry = new CacheMap.ExpireEntry(this.ticker.read() + TimeUnit.NANOSECONDS.convert(expireDelay, expireUnit), key, value);
        CacheMap.ExpireEntry previous = this.keyLookup.put(key, entry);
        this.expireQueue.add(entry);
        return previous != null ? (V) previous.value : null;
    }

    public boolean containsKey(K key) {
        this.evict();
        return this.keyLookup.containsKey(key);
    }

    public boolean containsValue(V value) {
        this.evict();
        Iterator var2 = this.keyLookup.values().iterator();

        CacheMap.ExpireEntry entry;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            entry = (CacheMap.ExpireEntry) var2.next();
        } while (!Objects.equal(value, entry.value));

        return true;
    }

    public V removeKey(K key) {
        this.evict();
        CacheMap.ExpireEntry entry = this.keyLookup.remove(key);
        return entry != null ? (V) entry.value : null;
    }

    public int size() {
        this.evict();
        return this.keyLookup.size();
    }

    public Set<K> keySet() {
        this.evict();
        return this.keyLookup.keySet();
    }

    public Collection<V> values() {
        this.evict();
        return this.valueView.values();
    }

    public Set<Entry<K, V>> entrySet() {
        this.evict();
        return this.valueView.entrySet();
    }

    public Map<K, V> asMap() {
        this.evict();
        return this.valueView;
    }

    public void collect() {
        this.evict();
        this.expireQueue.clear();
        this.expireQueue.addAll(this.keyLookup.values());
    }

    public void clear() {
        this.keyLookup.clear();
        this.expireQueue.clear();
    }

    protected void evict() {
        long current = this.ticker.read();

        while (this.expireQueue.size() > 0 && this.expireQueue.peek().time <= current) {
            CacheMap.ExpireEntry entry = this.expireQueue.poll();
            if (entry == this.keyLookup.get(entry.key)) {
                this.keyLookup.remove(entry.key);
            }
        }
    }

    public String toString() {
        return keyLookup.toString();
    }

    private class ExpireEntry implements Comparable<ExpireEntry> {
        public final long time;
        public final K key;
        public final V value;

        public ExpireEntry(long time, K key, V value) {
            this.time = time;
            this.key = key;
            this.value = value;
        }

        public int compareTo(CacheMap<K, V>.ExpireEntry o) {
            return Longs.compare(this.time, o.time);
        }

        public String toString() {
            return "ExpireEntry [time=" + this.time + ", key=" + this.key + ", value=" + this.value + "]";
        }
    }
}