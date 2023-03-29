package lib.brainsynder.workload;

/**
 * Code provided by https://www.spigotmc.org/threads/409003/
 * With slight modifications by brainsynder
 *
 * @author 7smile7
 * @author brainsynder
 */
public interface IScheduledWorkload extends IWorkload {
    default boolean shouldBeRescheduled () {
        return false;
    }
}
