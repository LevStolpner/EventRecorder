package ru.stolpner.task.yandex;

/**
 * Event recorder provides interface for collecting statistics for certain events
 * (e.g. it can be plugged in for monitoring message publications in news feed)
 * Events can be recorded asynchronously in any moment of time.
 * The load may be as high as 10 000 events in second, or only 2 events in hour.
 * Event recorder does not provide interface for event storage or detailed statistics on events.
 *
 * Records array structure has following logic rules:
 * 1) it is filled with entities, one for each second of "last" 24 hours
 * 2) each entity contains: counter for number of events happened in that second,
 *                          time when counter was reset last time,
 *                          to determine if counter was updated more than 24 hours ago
 * 3) if less than 24 hours has passed, events with same-second time increment certain entities counter
 * 4) if more than 24 hours has passed, old value in entities counter is reset
 * 5) thus, records always contain full picture for events in last 24 hours
 */
public class EventRecorder {

    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = 60 * SECONDS_IN_MINUTE;
    private static final int SECONDS_IN_DAY = 24 * SECONDS_IN_HOUR;

    private final long startTime;
    private final RecordEntity[] records = new RecordEntity[SECONDS_IN_DAY];

    public EventRecorder() {
        this.startTime = getCurrentTimeInSeconds();
        for (int i = 0; i < records.length; i++) {
            records[i] = new RecordEntity(this.startTime - records.length + i);
        }
    }

    /**
     * Records event time into records array.
     * If event time is past 24 hours or "in the future", it is not recorded.
     * If otherwise, few steps are performed:
     * 1) corresponding event time index is found in records array
     * 2) found element gets locked
     * 3) number of records in element gets incremented or reset, depending on how much time has passed
     *
     * @param milliseconds event time
     */
    public void recordEvent(long milliseconds) {
        long currentTime = getCurrentTimeInSeconds();
        long eventTime = milliseconds / 1000;
        long timeDifference = currentTime - eventTime;

        if (timeDifference >= SECONDS_IN_DAY || timeDifference < 0) return;

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
        return getNumberOfEventsByMinuteOrHour(true);
    }

    /**
     * Gets number of events recorded in last hour (60 minutes)
     *
     * @return number of events
     */
    public int getNumberOfLastHourEvents() {
        return getNumberOfEventsByMinuteOrHour(false);
    }

    /**
     * Gets number of events recorded in last day (24 hours) by
     * going through records array and counting elements records.
     *
     * @return number of events
     */
    public int getNumberOfLastDayEvents() {
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

    private long getCurrentTimeInSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * Gets number of events based on time period.
     * Few steps are performed:
     * 1) current time index is determined
     * 2) "previous" 60 or 3600 records are counted to result
     * 3) if not enough record elements are contained before current index,
     *    then elements are read from end of array
     *
     * @param isByMinute time period,
     *                   if true - events are counted for last minute (60 seconds),
     *                   if false - events are counted for last hour (60 minutes)
     * @return number of events
     */
    private int getNumberOfEventsByMinuteOrHour(boolean isByMinute) {
        int counter = 0;
        long currentTimeSeconds = getCurrentTimeInSeconds();
        long currentTimeIndex = (currentTimeSeconds - startTime) % SECONDS_IN_DAY;
        int timePeriod = isByMinute ? SECONDS_IN_MINUTE : SECONDS_IN_HOUR;

        if (currentTimeIndex + 1 - timePeriod >= 0) {
            for (int i = (int) currentTimeIndex; i > currentTimeIndex - timePeriod; i--) {
                synchronized (records[i]) {
                    if (currentTimeSeconds - records[i].getLastTimeResetCount() < SECONDS_IN_DAY) {
                        counter += records[i].getCount();
                    }
                }
            }
        } else {
            for (int i = (int) currentTimeIndex; i >= 0; i--) {
                synchronized (records[i]) {
                    if (currentTimeSeconds - records[i].getLastTimeResetCount() < SECONDS_IN_DAY) {
                        counter += records[i].getCount();
                    }
                }
            }

            for (int i = records.length - 1; i > records.length + currentTimeIndex - timePeriod; i--) {
                synchronized (records[i]) {
                    if (currentTimeSeconds - records[i].getLastTimeResetCount() < SECONDS_IN_DAY) {
                        counter += records[i].getCount();
                    }
                }
            }
        }

        return counter;
    }
}