package ru.stolpner.task.yandex;

//synchronize methods in this class instead of in code of recorder
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

    public long getLastTimeResetCount() {
        return lastTimeResetCount;
    }

    public void setLastTimeResetCount(long lastTimeResetCount) {
        this.lastTimeResetCount = lastTimeResetCount;
    }
}
