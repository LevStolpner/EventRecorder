package ru.stolpner.task.yandex;

/**
 * Event recorder provides interface for collecting statistics for certain events
 * (e.g. it can be plugged in for monitoring message publications in news feed)
 * Events can be recorded asynchronously in any moment of time.
 * The load may be as high as 10 000 events in second, or only 2 events in hour.
 * Event recorder does not provide interface for event storage or detailed statistics on events.
 */
public class EventRecorder {

    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = 60 * SECONDS_IN_MINUTE;
    private static final int SECONDS_IN_DAY = 24 * SECONDS_IN_HOUR;

    private final long startTime;
    //TODO explain data storage type and entities
    private final RecordEntity[] records = new RecordEntity[SECONDS_IN_DAY];

    public EventRecorder() {
        this.startTime = getCurrentTimeInSeconds();
        for (int i = 0; i < records.length; i++) {
            records[i] = new RecordEntity();
        }
    }

    //TODO comment on what is going on inside this method
    /**
     * Records event time into records array.
     * If event time is past 24 hours or "in the future", it is not recorded.
     * If otherwise, then
     * 1) event time index is found in records array
     * 2) needed element gets locked and number of records gets set
     *
     * @param milliseconds event time
     */
    public void recordEvent(long milliseconds) {
        long currentTime = getCurrentTimeInSeconds();
        long eventTime = milliseconds * 1000;
        long timeDifference = currentTime - eventTime;

        if (timeDifference > SECONDS_IN_DAY || timeDifference < 0) return;

        long currentTimeIndex = (currentTime - startTime) % SECONDS_IN_DAY;
        long indexDifference = currentTimeIndex - timeDifference;
        int eventTimeIndex = (int) (indexDifference >= 0 ? indexDifference : records.length + indexDifference);

        synchronized (records[eventTimeIndex]) {
            if (currentTime - records[eventTimeIndex].getLastTimeResetCount() > SECONDS_IN_DAY) {
                records[eventTimeIndex].setCount(1);
                records[eventTimeIndex].setLastTimeResetCount(currentTime);
            } else {
                records[eventTimeIndex].incrementCount();
            }
        }
    }

    /**
     * Gets number of events recorded in last minute (60 seconds)
     *
     * @return number of events
     */
    public int getNumberOfLastMinuteEvents() {
        //TODO rewind 60 indexes back from current time index
        //TODO synchronize on all counting indexes or not?
        synchronized (records) {
            int counter = 0;
            long currentTimeSeconds = getCurrentTimeInSeconds();
            long currentSecondIndex = (currentTimeSeconds - startTime) % SECONDS_IN_DAY;
            if (currentSecondIndex - SECONDS_IN_MINUTE >= 0) {
                for (int i = (int) currentSecondIndex; i > currentSecondIndex - SECONDS_IN_MINUTE; i--) {
                    synchronized (records[i]) {
                        if (currentTimeSeconds - records[i].getLastTimeResetCount() < SECONDS_IN_MINUTE) {
                            counter += records[i].getCount();
                        }
                    }
                }
            } else {
                long indexesToRewindFromEndOfArray = SECONDS_IN_MINUTE - currentSecondIndex;

                for (int i = (int) currentSecondIndex; i >= 0; i--) {
                    synchronized (records[i]) {
                        if (currentTimeSeconds - records[i].getLastTimeResetCount() < SECONDS_IN_MINUTE) {
                            counter += records[i].getCount();
                        }
                    }
                }

                for (int i = records.length - 1; i > records.length - 1 - indexesToRewindFromEndOfArray; i--) {
                    synchronized (records[i]) {
                        if (currentTimeSeconds - records[i].getLastTimeResetCount() < SECONDS_IN_MINUTE) {
                            counter += records[i].getCount();
                        }
                    }
                }
            }

            return counter;
        }
    }

    /**
     * Gets number of events recorded in last hour (60 minutes)
     *
     * @return number of events
     */
    public int getNumberOfLastHourEvents() {
        //TODO rewind 3600 indexes back from current time index
        //TODO synchronize on all counting indexes or not?
        //TODO synch on array itself or not?
        synchronized (records) {
            int counter = 0;
            long currentTimeSeconds = getCurrentTimeInSeconds();
            long currentSecondIndex = (currentTimeSeconds - startTime) % SECONDS_IN_DAY;
            if (currentSecondIndex - SECONDS_IN_HOUR >= 0) {
                for (int i = (int) currentSecondIndex; i > currentSecondIndex - SECONDS_IN_HOUR; i--) {
                    synchronized (records[i]) {
                        if (currentTimeSeconds - records[i].getLastTimeResetCount() < SECONDS_IN_HOUR) {
                            counter += records[i].getCount();
                        }
                    }
                }
            } else {
                long indexesToRewindFromEndOfArray = SECONDS_IN_HOUR - currentSecondIndex;

                for (int i = (int) currentSecondIndex; i >= 0; i--) {
                    synchronized (records[i]) {
                        if (currentTimeSeconds - records[i].getLastTimeResetCount() < SECONDS_IN_HOUR) {
                            counter += records[i].getCount();
                        }
                    }
                }

                for (int i = records.length - 1; i > records.length - 1 - indexesToRewindFromEndOfArray; i--) {
                    synchronized (records[i]) {
                        if (currentTimeSeconds - records[i].getLastTimeResetCount() < SECONDS_IN_HOUR) {
                            counter += records[i].getCount();
                        }
                    }
                }
            }

            return counter;
        }
    }

    /**
     * Gets number of events recorded in last day (24 hours)
     *
     * @return number of events
     */
    public int getNumberOfLastDayEvents() {
        synchronized (records) {
            int counter = 0;
            long currentTimeSeconds = getCurrentTimeInSeconds();
            for (int i = 0; i < records.length; i++) {
                synchronized (records[i]) {
                    if (currentTimeSeconds - records[i].getLastTimeResetCount() < SECONDS_IN_DAY) {
                        counter += records[i].getCount();
                    }
                }
            }

            return counter;
        }
    }

    private long getCurrentTimeInSeconds() {
        return System.currentTimeMillis() * 1000;
    }
}