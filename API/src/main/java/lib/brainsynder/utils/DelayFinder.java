package lib.brainsynder.utils;

import com.eclipsesource.json.JsonObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DelayFinder {
    private static final Map<String, Long> startTimeMap = new HashMap<>();
    private static final LinkedHashMap<String, LinkedHashMap<String, Result>> storedTimeMap = new LinkedHashMap<>();

    public static void runTask (Object instance, String name, Runnable runnable) {
        trackDelay(instance, name);
        runnable.run();
        trackDelay(instance, name);
    }

    /**
     * Finds how many Milli/Nano seconds it took to run a task
     *
     * @param instance  - Instance of class where the task is being called from
     * @param taskName - A name to give the task (use the same name for start/finish)
     */
    public static long trackDelay(Object instance, String taskName) {
        String className = instance.getClass().getSimpleName();

        LinkedHashMap<String, Result> map = storedTimeMap.getOrDefault(className, new LinkedHashMap<>());
        String key = className + "|" + taskName;
        if (startTimeMap.containsKey(key)) {
            long start = startTimeMap.get(key);
            long end = System.nanoTime();
            long diff = (end - start) / 1000000;

            if (diff <= 0) {
                long finalDiff = (end - start);
                map.put(taskName, new Result() {
                    @Override
                    public String suffix() {
                        return "ns";
                    }

                    @Override
                    public long diff() {
                        return finalDiff;
                    }
                });
            }else{
                map.put(taskName, new Result() {
                    @Override
                    public String suffix() {
                        return "ms";
                    }

                    @Override
                    public long diff() {
                        return diff;
                    }
                });
            }
            // Will log what class and how long the task(s) took.
            startTimeMap.remove(key);
            storedTimeMap.put(className, map);
            return diff;
        }
        map.put(taskName, new Result() {
            @Override
            public String suffix() {
                return "ms";
            }

            @Override
            public long diff() {
                return -1;
            }
        });
        storedTimeMap.put(className, map);
        startTimeMap.put(key, System.nanoTime());
        return 0;
    }

    /**
     * Will return a JsonObject that contains a list of all tasks that have not been ended
     */
    public static JsonObject fetchUnfinished () {
        JsonObject json = new JsonObject();
        startTimeMap.forEach((s, start) -> {
            long end = System.nanoTime();
            long diff = (end - start) / 1000000;
            String suffix = "ms";

            if (diff <= 0) {
                diff = (end - start);
                suffix = "ns";
            }

            String[] args = s.split("|");
            String className = args[0];

            JsonObject classJson = (json.names().contains(className) ? (JsonObject) json.get(className) : new JsonObject());
            String task = args[1];
            classJson.set(task, diff+suffix);
            json.set(className, classJson);
        });
        return json;
    }

    /**
     * Will return a JsonObject that contains a list of all completed tasks that have ended
     */
    public static JsonObject toJson () {
        JsonObject json = new JsonObject();
        storedTimeMap.forEach((className, map) -> {
            JsonObject object = new JsonObject();
            map.forEach((task, result) -> object.add(task, result.diff()+result.suffix()));
            json.set(className, object);
        });
        return json;
    }

    private interface Result {
        String suffix ();
        long diff();
    }
}