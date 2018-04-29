package ru.stolpner.task.yandex;

/**
 * Event recorder provides interface for collecting statistics for certain events
 * (e.g. it can be plugged in for monitoring message publications in news feed)
 * Events can be recorded asynchronously in any moment of time.
 * The load may be as high as 10 000 events in second, or only 2 events in hour.
 * Event recorder does not provide interface for event storage or detailed statistics on events.
 */
public class EventRecorder {

    private static final int NUMBER_OF_SECONDS_IN_DAY = 24 * 60 * 60;

    private RecordEntity[] recordEntities = new RecordEntity[NUMBER_OF_SECONDS_IN_DAY];
    //only reads from here?
    private long startTimeSeconds;

    //TODO lazy initialization of entities
    public EventRecorder() {
        this.startTimeSeconds = System.currentTimeMillis() * 1000;
        for (int i = 0; i < recordEntities.length; i++) {
            recordEntities[i] = new RecordEntity();
        }
    }

    /**
     * Record event
     *
     * @param milliseconds event time
     */
    public void recordEvent(long milliseconds) {
        long currentTimeSeconds = System.currentTimeMillis() * 1000;
        long eventSeconds = milliseconds * 1000;
        long difference = currentTimeSeconds - eventSeconds;

        //if event time is past 24 hours or "in the future", it is not recorded
        if (difference > NUMBER_OF_SECONDS_IN_DAY || difference < 0) {
            return;
        }

        //index of current second in array
        long currentSecondIndex = (currentTimeSeconds - startTimeSeconds) % NUMBER_OF_SECONDS_IN_DAY;

        int index;
        //if we don't need to count from end of the array backwards to find index
        if (currentSecondIndex - difference >= 0) {
            //we must go "difference" number of indexes "back" in time
            index = (int) (currentSecondIndex - difference);
        } else {
            //we must count from end of the array backwards to find index
            index = recordEntities.length - 1 - (int) (difference - currentSecondIndex);
        }

        //number of records in second is either incremented or reset and started again
        synchronized (recordEntities[index]) {
            if (currentTimeSeconds - recordEntities[index].getLastTimeResetCount()> NUMBER_OF_SECONDS_IN_DAY) {
                recordEntities[index].setCount(1);
                recordEntities[index].setLastTimeResetCount(currentTimeSeconds);
            } else {
                recordEntities[index].incrementCount();
            }
        }
    }

    /**
     * Gets number of events recorded in last minute (60 seconds)
     *
     * @return number of events
     */
    public int getNumberOfLastMinuteEvents() {
        return 0;
    }

    /**
     * Gets number of events recorded in last hour (60 minutes)
     *
     * @return number of events
     */
    public int getNumberOfLastHourEvents() {
        return 0;
    }

    /**
     * Gets number of events recorded in last day (24 hours)
     *
     * @return number of events
     */
    public int getNumberOfLastDayEvents() {
        return 0;
    }


}