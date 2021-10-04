package lib.brainsynder.utils;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class TaskTimer {
    private static final LinkedHashMap<String, JsonArray> STORAGE = new LinkedHashMap<>();
    private static final LinkedHashMap<String, LinkedList<Record>> STORED_TIME = new LinkedHashMap<>();

    /**
     * This is the targeted class name (will also include any additional text specified in the other constructor)
     */
    private final String CLASS_NAME;
    private String previousName = "";

    /**
     * Initiates the timer for the instance
     *
     * @param instance instance of class
     */
    public TaskTimer (Object instance) {
        this(instance, "");
    }

    /**
     * Initiates the timer for the instance
     *
     * @param instance instance of class
     * @param addition additional data (Ex: method name or a specific task that you want to time separately)
     */
    public TaskTimer (Object instance, String addition) {
        Class<?> clazz = instance.getClass();
        if (instance instanceof Class) clazz = (Class<?>) instance;

        CLASS_NAME = clazz.getSimpleName()+(addition.isEmpty() ? "" : " - "+addition);
        STORED_TIME.put(CLASS_NAME, new LinkedList<>());
    }

    /**
     * Will record the start time
     */
    public void start () {
        // The task timer was already started, or has a break...
        if (previousName.equals("start") || previousName.equals("break")) return;
        label("start");
    }

    /**
     * Marks the current time
     *
     * @param name Name of the task that ran
     */
    public void label (String name) {
        LinkedList<Record> records = STORED_TIME.get(CLASS_NAME);
        if (records.isEmpty() && !name.equals("start")) // Ensures that the task was actually started
            throw new RuntimeException("The TaskTimer instance for '"+CLASS_NAME+"' was not started please run the TaskTimer#start() method first");

        this.previousName = name;
        records.addLast(new Record(name, System.nanoTime()));
        STORED_TIME.put(CLASS_NAME, records);
    }

    /**
     * Will clear all data for the task timer instance
     *
     * Will need to be restarted
     */
    public void clearTaskTimer () {
        STORAGE.remove(CLASS_NAME);
        STORED_TIME.put(CLASS_NAME, new LinkedList<>());
    }

    /**
     * Creates a break from the currently running timer
     * (resets the previous time to the current time)
     *
     * Use this if you are not ready for the task timer to end
     */
    public void newStart () {
        label("break");
    }

    /**
     * Will end the current timer
     *
     * @return Will return a {@link JsonArray} that will contain the duration of each task
     */
    public JsonArray stop () {
        // The timer already was ended, no need to end it again
        if (previousName.equals("end")) return STORAGE.get(CLASS_NAME);

        label("end");
        JsonArray array = new JsonArray();
        LinkedList<Record> records = STORED_TIME.get(CLASS_NAME);
        DecimalFormat decimalFormat = new DecimalFormat("#0.000", DecimalFormatSymbols.getInstance(Locale.US));
        long previous = 0;

        while (records.peekFirst() != null) {
            Record record = records.pollFirst();
            if (record.label.equals("start") || record.label.equals("break")) {
                previous = record.time;
                continue;
            }
            long stored = record.time;
            double duration = (stored - previous) / 1000000.0;

            JsonObject value = new JsonObject();
            value.add("name", record.label);
            value.add("duration", duration);
            value.add("formatted", decimalFormat.format(duration)+"ms");
            array.add(value);
        }

        STORAGE.put(CLASS_NAME, array);
        return array;
    }

    /**
     * Will fetch and return all the completed timers
     */
    public static JsonObject fetchAllCompletedTimers () {
        JsonObject json = new JsonObject();

        List<String> unfinished = new ArrayList<>();
        STORAGE.forEach(json::add);

        // Checks if there are any timers that have not finished
        if (STORAGE.size() != STORED_TIME.size()) {
            STORED_TIME.forEach((s, records) -> {
                if (!STORAGE.containsKey(s)) unfinished.add(s);
            });
        }

        // Checks if there are any unfinished timers, if there are add their names to their own array
        if (!unfinished.isEmpty()) {
            JsonArray array = new JsonArray();
            unfinished.forEach(array::add);
            json.add("unfinished-timers", array);
        }
        return json;
    }

    // This was to test the method
    public static void main(String[] args) throws InterruptedException {
        TaskTimer timer = new TaskTimer(TaskTimer.class);
        timer.start();
        Thread.sleep(500);
        timer.label("Delay - 500");
        Thread.sleep(150);
        System.out.println("Output: "+timer.stop());

        TaskTimer newTimer = new TaskTimer(TaskTimer.class, "second");
        newTimer.start();
        Thread.sleep(200);
        newTimer.label("Delay - 200 - 1");
        Thread.sleep(200);
        newTimer.label("Delay - 200 - 2");
        newTimer.stop();
        System.out.println("Final Output: "+TaskTimer.fetchAllCompletedTimers());
    }


    private static class Record {
        private final String label;
        private final long time;

        Record(String label, long time) {
            this.label = label;
            this.time = time;
        }
    }
}
