package ru.stolpner.task.yandex;

/**
 * Event recorder provides interface for collecting statistics for certain events
 * (e.g. it can be plugged in for monitoring message publications in news feed)
 * Events can be recorded asynchronously in any moment of time.
 * The load may be as high as 10 000 events in second, or only 2 events in hour.
 * Event recorder does not provide interface for event storage or detailed statistics on events.
 */
public class EventRecorder {

    private static final int NUMBER_OF_SECONDS_IN_MINUTE = 60;
    private static final int NUMBER_OF_SECONDS_IN_HOUR = 60 * NUMBER_OF_SECONDS_IN_MINUTE;
    private static final int NUMBER_OF_SECONDS_IN_DAY = 24 * NUMBER_OF_SECONDS_IN_HOUR;

    private final RecordEntity[] recordEntities = new RecordEntity[NUMBER_OF_SECONDS_IN_DAY];
    //only reads from here?
    private final long startTimeSeconds;

    //TODO lazy initialization of entities
    //TODO longs to ints?
    //check synchronization
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
        //TODO rewind 60 indexes back from current time index
        //TODO synchronize on all counting indexes or not?
        synchronized (recordEntities) {
            int counter = 0;
            long currentTimeSeconds = System.currentTimeMillis() * 1000;
            long currentSecondIndex = (currentTimeSeconds - startTimeSeconds) % NUMBER_OF_SECONDS_IN_DAY;
            if (currentSecondIndex - NUMBER_OF_SECONDS_IN_MINUTE >= 0) {
                for (int i = (int) currentSecondIndex; i > currentSecondIndex - NUMBER_OF_SECONDS_IN_MINUTE; i--) {
                    synchronized (recordEntities[i]) {
                        if (currentTimeSeconds - recordEntities[i].getLastTimeResetCount() < NUMBER_OF_SECONDS_IN_MINUTE) {
                            counter += recordEntities[i].getCount();
                        }
                    }
                }
            } else {
                int indexesToRewindFromEndOfArray = (int) (NUMBER_OF_SECONDS_IN_MINUTE - currentSecondIndex);

                for (int i = (int) currentSecondIndex; i >= 0; i--) {
                    synchronized (recordEntities[i]) {
                        if (currentTimeSeconds - recordEntities[i].getLastTimeResetCount() < NUMBER_OF_SECONDS_IN_MINUTE) {
                            counter += recordEntities[i].getCount();
                        }
                    }
                }

                for (int i = recordEntities.length - 1; i > recordEntities.length - 1 - indexesToRewindFromEndOfArray; i--) {
                    synchronized (recordEntities[i]) {
                        if (currentTimeSeconds - recordEntities[i].getLastTimeResetCount() < NUMBER_OF_SECONDS_IN_MINUTE) {
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
            long currentTimeSeconds = System.currentTimeMillis() * 1000;
            long currentSecondIndex = (currentTimeSeconds - startTimeSeconds) % NUMBER_OF_SECONDS_IN_DAY;
            if (currentSecondIndex - NUMBER_OF_SECONDS_IN_HOUR >= 0) {
                for (int i = (int) currentSecondIndex; i > currentSecondIndex - NUMBER_OF_SECONDS_IN_HOUR; i--) {
                    synchronized (recordEntities[i]) {
                        if (currentTimeSeconds - recordEntities[i].getLastTimeResetCount() < NUMBER_OF_SECONDS_IN_HOUR) {
                            counter += recordEntities[i].getCount();
                        }
                    }
                }
            } else {
                int indexesToRewindFromEndOfArray = (int) (NUMBER_OF_SECONDS_IN_HOUR - currentSecondIndex);

                for (int i = (int) currentSecondIndex; i >= 0; i--) {
                    synchronized (recordEntities[i]) {
                        if (currentTimeSeconds - recordEntities[i].getLastTimeResetCount() < NUMBER_OF_SECONDS_IN_HOUR) {
                            counter += recordEntities[i].getCount();
                        }
                    }
                }

                for (int i = recordEntities.length - 1; i > recordEntities.length - 1 - indexesToRewindFromEndOfArray; i--) {
                    synchronized (recordEntities[i]) {
                        if (currentTimeSeconds - recordEntities[i].getLastTimeResetCount() < NUMBER_OF_SECONDS_IN_HOUR) {
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
            long currentTimeSeconds = System.currentTimeMillis() * 1000;
            for (int i = 0; i < recordEntities.length; i++) {
                synchronized (recordEntities[i]) {
                    if (currentTimeSeconds - recordEntities[i].getLastTimeResetCount() < NUMBER_OF_SECONDS_IN_DAY) {
                        counter += recordEntities[i].getCount();
                    }
                }
            }

            return counter;
        }
    }
}