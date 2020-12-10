package lib.brainsynder.storage;

import java.util.*;

public class RandomCollection<E> {
    private final NavigableMap<Double, E> map;
    private final Random random;
    private double total;

    public static <E>E randomize (Collection<E> list) {
        return randomize(list, 50);
    }

    public static <E>E randomize (Collection<E> list, int percent) {
        RandomCollection<E> collection = new RandomCollection<>();
        list.forEach(e -> collection.add(percent, e));
        return collection.next();
    }

    public static <E>RandomCollection<E> fromCollection (Collection<E> list) {
        return fromCollection(list, 50);
    }

    public static <E>RandomCollection<E> fromCollection (Collection<E> list, int percent) {
        RandomCollection<E> collection = new RandomCollection<>();
        list.forEach(e -> collection.add(percent, e));
        return collection;
    }

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random var1) {
        this.map = new TreeMap<>();
        this.total = 0.0D;
        this.random = var1;
    }

    public void add(double percent, E value) {
        if (percent > 0.0D) {
            this.total += percent;
            this.map.put(this.total, value);
        }
    }

    public Collection<E> values () {
        return map.values();
    }

    public E next() {
        double var1 = this.random.nextDouble() * this.total;
        return this.map.ceilingEntry(var1).getValue();
    }

    // Will fetch the next random value, while also removing it from the selections
    public E nextRemove() {
        if (map.isEmpty()) return null;
        double var1 = this.random.nextDouble() * this.total;
        Map.Entry<Double, E> entry = this.map.ceilingEntry(var1);
        E value = entry.getValue();
        this.total -= entry.getKey();
        map.remove(entry.getKey());
        return value;
    }
}