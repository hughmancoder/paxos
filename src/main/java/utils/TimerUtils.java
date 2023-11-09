package utils;

import java.util.Timer;
import java.util.TimerTask;

/* TimerUtils is a utility class for scheduling tasks to run after a delay
and at fixed rate. */
public class TimerUtils {
    private Timer timer;
    private long delay;
    private long period;
    private TimerTask currentTask;

    public TimerUtils(long delay, long period) {
        this.timer = new Timer();
        this.delay = delay;
        this.period = period;
    }

    public void start(Runnable task) {
        stop(); // Stop any existing tasks
        currentTask = new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        };
        timer.scheduleAtFixedRate(currentTask, delay, period);
    }

    public void stop() {
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }
        if (timer != null) {
            timer.purge(); // Remove cancelled tasks from the timer's task queue
        }
    }

    public void setDelay(long delay) {
        if (this.delay != delay) {
            this.delay = delay;
            restartLastTask();
        }
    }

    public void setPeriod(long period) {
        if (this.period != period) {
            this.period = period;
            restartLastTask();
        }
    }

    private void restartLastTask() {
        if (currentTask != null) {
            Runnable lastRunnable = currentTask.scheduledExecutionTime() > 0 ? currentTask : null;
            stop(); // Stop the current task
            if (lastRunnable != null) {
                start(lastRunnable); // Restart the task with the new values
            }
        }
    }

    public long getDelay() {
        return delay;
    }

    public long getPeriod() {
        return period;
    }
}