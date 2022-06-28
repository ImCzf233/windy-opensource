/*
 * Decompiled with CFR 0_132.
 */
package space.emptiness.utils;

public class TimerUtil {
    private long lastMS;

    private long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public boolean hasReached(double milliseconds) {
        if ((double)(this.getCurrentMS() - this.lastMS) >= milliseconds) {
            return true;
        }
        return false;
    }

    public void reset() {
        this.lastMS = this.getCurrentMS();
    }

    public boolean delay(float milliSec) {
        if ((float)(this.getTime() - this.lastMS) >= milliSec) {
            return true;
        }
        return false;
    }

    private long ms = getCurrentMS();

    public boolean hasTimeElapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - lastMS > time) {
            if (reset) reset();
            return true;
        }

        return false;
    }

    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - lastMS > time;
    }


    public double getLastDelay () {
        return getCurrentMS() - getLastMS();
    }
    public long getLastMS() {
        return lastMS;
    }
    public final long getDifference() {
        return getCurrentMS() - ms;
    }

    public final boolean hasPassed(long milliseconds) {
        return getCurrentMS() - ms > milliseconds;
    }


    public long getTime() {
        return System.nanoTime() / 1000000L;
    }
}

