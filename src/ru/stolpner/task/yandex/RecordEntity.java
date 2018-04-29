package ru.stolpner.task.yandex;

class RecordEntity {
    private int count;
    private long lastTimeResetCount;

    RecordEntity() {
        this.count = 0;
        this.lastTimeResetCount = System.currentTimeMillis() * 1000;
    }

    int getCount() {
        return count;
    }

    void setCount(int count) {
        this.count = count;
    }

    void incrementCount() {
        this.count += 1;
    }

    long getLastTimeResetCount() {
        return lastTimeResetCount;
    }

    void setLastTimeResetCount(long lastTimeResetCount) {
        this.lastTimeResetCount = lastTimeResetCount;
    }
}
