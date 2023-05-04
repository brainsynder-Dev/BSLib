package lib.brainsynder.storage;

import lib.brainsynder.math.MathUtils;

import java.util.*;

public class RandomCollection<E> {
    private final NavigableMap<Double, Element<E>> map;
    private final Random random;
    private double total;

    public static <T> Element<T> randomize(Collection<T> list) {
        return randomize(list, 50);
    }

    public static <T> Element<T> randomize(Collection<T> list, double percent) {
        RandomCollection<T> collection = new RandomCollection<>();
        list.forEach(e -> collection.add(percent, e));
        return collection.next();
    }

    public static <E> RandomCollection<E> fromCollection(Collection<E> list) {
        return fromCollection(list, 50);
    }

    public static <E> RandomCollection<E> fromCollection(Collection<E> list, double percent) {
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

    public void add(E value) {
        add(50, value);
    }

    public void add(double percent, E value) {
        if (percent > 0.0D) {
            this.total += percent;
            this.map.put(this.total, new Element<>(value, percent));
        }
    }

    public Collection<Element<E>> values() {
        return map.values();
    }

    public Element<E> next() {
        double var1 = this.random.nextDouble() * this.total;
        return this.map.ceilingEntry(var1).getValue();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public int getSize() {
        return map.size();
    }

    // Will fetch the next random value, while also removing it from the selections
    public Element<E> nextRemove() {
        if (map.isEmpty()) return null;
        double var1 = this.random.nextDouble() * this.total;
        Map.Entry<Double, Element<E>> entry = this.map.ceilingEntry(var1);
        Element<E> value = entry.getValue();
        this.total -= entry.getKey();
        map.remove(entry.getKey());
        return value;
    }

    public static final class Element<O> {
        private final O value;
        private final double percent;

        private Element(O value, double percent) {
            this.value = value;
            this.percent = percent;
        }

        public double getPercent() {
            return percent;
        }

        public O getValue() {
            return value;
        }

        public double getProbableChance() {
            return MathUtils.trim(3, ((percent / 85) * 100));
        }

        @Override
        public String toString() {
            return "Element{value=" + value + ", percent=" + percent + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Element<?> element = (Element<?>) o;
            return Double.compare(element.percent, percent) == 0 && Objects.equals(value, element.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, percent);
        }
    }
}