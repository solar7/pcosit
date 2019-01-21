package com.collibra.pcos.session;

import com.collibra.pcos.utils.LoggerUtils;
import org.slf4j.Logger;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

import static com.collibra.pcos.properties.ApplicationProperties.SERVER_TIMEOUT;

/**
 * Session's watchdog timer remembers the thread which created it
 * and interrupts when session is inactive for more then specified timeout
 */
public class WatchDog {

    private static final Logger LOGGER = LoggerUtils.getLogger();

    private static final int RUN_EVERY_MS = 500;
    private final int TIMEOUT = SERVER_TIMEOUT.getIntValue();

    private final Timer timer;
    private final String sessionId;
    private final Thread creatorThread;
    private final Supplier<Long> idleMsSupplier;

    public WatchDog(String newSessionId, Supplier<Long> idleInMsSupplier) {
        sessionId = newSessionId;
        idleMsSupplier = idleInMsSupplier;
        creatorThread = Thread.currentThread();
        timer = createTimer();
    }

    private Timer createTimer() {
        return new Timer(Thread.currentThread().getName() + " watchdog", true);
    }

    public void schedule() {
        timer.schedule(new WatchDogTask(), RUN_EVERY_MS, RUN_EVERY_MS);
    }

    public void cancel() {
        timer.cancel();
        timer.purge();
    }

    class WatchDogTask extends TimerTask {
        @Override
        public void run() {
            long idleMs = idleMsSupplier.get();
            if (idleMs > TIMEOUT) {
                LOGGER.info("timeout inactive {}ms, killing connection {} [{}]", idleMs, sessionId, creatorThread.getName());
                this.cancel();
                creatorThread.interrupt();
            } else {
                LOGGER.trace("idle time {}ms, {}", idleMs, sessionId);
            }
        }
    }

}
