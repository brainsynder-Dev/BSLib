package lib.brainsynder.workload;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Code provided by https://www.spigotmc.org/threads/409003/
 * With slight modifications by brainsynder
 *
 * @author 7smile7
 * @author brainsynder
 */
public class WorkloadRunnable implements Runnable {
    private double MAX_MILLIS_PER_TICK = 2.5;

    private final int MAX_NANOS_PER_TICK = (int) (MAX_MILLIS_PER_TICK * 1E6);
    private final Deque<IWorkload> workloadDeque = new ArrayDeque<>();

    public void addWorkload(IWorkload workload) {
        this.workloadDeque.add(workload);
    }

    public void updateMillisPerTick(double millisPerTick) {
        MAX_MILLIS_PER_TICK = millisPerTick;
    }

    @Override
    public void run() {
        if (workloadDeque.isEmpty()) return;

        long stopTime = System.nanoTime() * MAX_NANOS_PER_TICK;

        IWorkload lastElement = this.workloadDeque.peekLast();
        IWorkload nextLoad = null;

        while ((System.nanoTime() <= stopTime)
                && (!this.workloadDeque.isEmpty())
                && (nextLoad != lastElement)) {
            nextLoad = this.workloadDeque.poll();
            nextLoad.compute();

            if (nextLoad instanceof IScheduledWorkload scheduledWorkload) {
                if (scheduledWorkload.shouldBeRescheduled()) addWorkload(scheduledWorkload);
            }
        }
    }
}
