package ru.stolpner.task.yandex;

class RecordEntity {
    private volatile int count;
    private volatile long lastTimeReset;

    RecordEntity(long time) {
        this.count = 0;
        this.lastTimeReset = time;
    }

    int getCount() {
        return count;
    }

    void setCount(int count) {
        this.count = count;
    }

    void incrementCount() {
        this.count++;
    }

    long getLastTimeReset() {
        return lastTimeReset;
    }

    void setLastTimeReset(long lastTimeReset) {
        this.lastTimeReset = lastTimeReset;
    }
}