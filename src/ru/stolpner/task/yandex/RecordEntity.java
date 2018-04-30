package ru.stolpner.task.yandex;

class RecordEntity {
    private int count;
    private long lastTimeResetCount;

    RecordEntity(long time) {
        this.count = 0;
        this.lastTimeResetCount = time;
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

    long getLastTimeResetCount() {
        return lastTimeResetCount;
    }

    void setLastTimeResetCount(long lastTimeResetCount) {
        this.lastTimeResetCount = lastTimeResetCount;
    }
}