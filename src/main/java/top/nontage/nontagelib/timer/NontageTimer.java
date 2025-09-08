package top.nontage.nontagelib.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class NontageTimer {
    private final ScheduledExecutorService scheduler;
    private final long interval; // 秒
    private boolean running = false;

    public NontageTimer(long intervalSeconds) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.interval = intervalSeconds;
    }

    public void start() {
        if (running) return;
        running = true;
        scheduler.scheduleAtFixedRate(() -> {
            try {
                onTick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, interval, TimeUnit.SECONDS);
    }

    public void stop() {
        if (!running) return;
        running = false;
        scheduler.shutdownNow();
    }

    protected abstract void onTick();
}