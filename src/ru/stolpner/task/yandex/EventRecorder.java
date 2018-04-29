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

    private final int startTimeSeconds;
    private final RecordEntity[] recordEntities = new RecordEntity[SECONDS_IN_DAY];

    public EventRecorder() {
        this.startTimeSeconds = getCurrentTimeInSeconds();
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
        int currentTimeSeconds = getCurrentTimeInSeconds();
        int eventSeconds = (int) milliseconds * 1000;
        int difference = currentTimeSeconds - eventSeconds;

        //if event time is past 24 hours or "in the future", it is not recorded
        if (difference > SECONDS_IN_DAY || difference < 0) {
            return;
        }

        //index of current second in array
        int currentSecondIndex = (currentTimeSeconds - startTimeSeconds) % SECONDS_IN_DAY;

        int index;
        //if we don't need to count from end of the array backwards to find index
        if (currentSecondIndex - difference >= 0) {
            //we must go "difference" number of indexes "back" in time
            index = currentSecondIndex - difference;
        } else {
            //we must count from end of the array backwards to find index
            index = recordEntities.length - 1 - difference + currentSecondIndex;
        }

        //number of records in second is either incremented or reset and started again
        synchronized (recordEntities[index]) {
            if (currentTimeSeconds - recordEntities[index].getLastTimeResetCount()> SECONDS_IN_DAY) {
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
        //TODO rewind 60 indexes back from current time index
        //TODO synchronize on all counting indexes or not?
        synchronized (recordEntities) {
            int counter = 0;
            int currentTimeSeconds = getCurrentTimeInSeconds();
            int currentSecondIndex = (currentTimeSeconds - startTimeSeconds) % SECONDS_IN_DAY;
            if (currentSecondIndex - SECONDS_IN_MINUTE >= 0) {
                for (int i = currentSecondIndex; i > currentSecondIndex - SECONDS_IN_MINUTE; i--) {
                    synchronized (recordEntities[i]) {
                        if (currentTimeSeconds - recordEntities[i].getLastTimeResetCount() < SECONDS_IN_MINUTE) {
                            counter += recordEntities[i].getCount();
                        }
                    }
                }
            } else {
                int indexesToRewindFromEndOfArray = SECONDS_IN_MINUTE - currentSecondIndex;

                for (int i = currentSecondIndex; i >= 0; i--) {
                    synchronized (recordEntities[i]) {
                        if (currentTimeSeconds - recordEntities[i].getLastTimeResetCount() < SECONDS_IN_MINUTE) {
                            counter += recordEntities[i].getCount();
                        }
                    }
                }

                for (int i = recordEntities.length - 1; i > recordEntities.length - 1 - indexesToRewindFromEndOfArray; i--) {
                    synchronized (recordEntities[i]) {
                        if (currentTimeSeconds - recordEntities[i].getLastTimeResetCount() < SECONDS_IN_MINUTE) {
                            counter += recordEntities[i].getCount();
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
        synchronized (recordEntities) {
            int counter = 0;
            int currentTimeSeconds = getCurrentTimeInSeconds();
            int currentSecondIndex = (currentTimeSeconds - startTimeSeconds) % SECONDS_IN_DAY;
            if (currentSecondIndex - SECONDS_IN_HOUR >= 0) {
                for (int i = currentSecondIndex; i > currentSecondIndex - SECONDS_IN_HOUR; i--) {
                    synchronized (recordEntities[i]) {
                        if (currentTimeSeconds - recordEntities[i].getLastTimeResetCount() < SECONDS_IN_HOUR) {
                            counter += recordEntities[i].getCount();
                        }
                    }
                }
            } else {
                int indexesToRewindFromEndOfArray = SECONDS_IN_HOUR - currentSecondIndex;

                for (int i = currentSecondIndex; i >= 0; i--) {
                    synchronized (recordEntities[i]) {
                        if (currentTimeSeconds - recordEntities[i].getLastTimeResetCount() < SECONDS_IN_HOUR) {
                            counter += recordEntities[i].getCount();
                        }
                    }
                }

                for (int i = recordEntities.length - 1; i > recordEntities.length - 1 - indexesToRewindFromEndOfArray; i--) {
                    synchronized (recordEntities[i]) {
                        if (currentTimeSeconds - recordEntities[i].getLastTimeResetCount() < SECONDS_IN_HOUR) {
                            counter += recordEntities[i].getCount();
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
        synchronized (recordEntities) {
            int counter = 0;
            int currentTimeSeconds = getCurrentTimeInSeconds();
            for (int i = 0; i < recordEntities.length; i++) {
                synchronized (recordEntities[i]) {
                    if (currentTimeSeconds - recordEntities[i].getLastTimeResetCount() < SECONDS_IN_DAY) {
                        counter += recordEntities[i].getCount();
                    }
                }
            }

            return counter;
        }
    }

    private int getCurrentTimeInSeconds() {
        return (int) System.currentTimeMillis() * 1000;
    }
}