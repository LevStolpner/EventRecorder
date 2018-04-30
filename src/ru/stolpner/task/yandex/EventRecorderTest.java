package ru.stolpner.task.yandex;

import java.util.concurrent.TimeUnit;

public class EventRecorderTest {
    public static void main(String[] args) throws InterruptedException {
        EventRecorder recorder = new EventRecorder();

        recorder.recordEvent(System.currentTimeMillis() - 57 * 1000);
        recorder.recordEvent(System.currentTimeMillis() - 50 * 60 * 1000);
        recorder.recordEvent(System.currentTimeMillis() - 20 * 60 * 60 * 1000);

        System.out.println("records last minute stats: " + recorder.getNumberOfLastMinuteEvents());
        System.out.println("records last hour stats: " + recorder.getNumberOfLastHourEvents());
        System.out.println("records last day stats: " + recorder.getNumberOfLastDayEvents());

        TimeUnit.SECONDS.sleep(5);
        System.out.println("After 5 seconds sleep last minute stats: " + recorder.getNumberOfLastMinuteEvents());
        System.out.println("After 5 seconds sleep last hour stats: " + recorder.getNumberOfLastHourEvents());
        System.out.println("After 5 seconds sleep last day stats: " + recorder.getNumberOfLastDayEvents());

        recorder.recordEvent(System.currentTimeMillis() - 86398000);

        System.out.println("After 1 day expired last minute stats: " + recorder.getNumberOfLastMinuteEvents());
        System.out.println("After 1 day expired last hour stats: " + recorder.getNumberOfLastHourEvents());
        System.out.println("After 1 day expired last day stats: " + recorder.getNumberOfLastDayEvents());


        TimeUnit.SECONDS.sleep(5);
        System.out.println("After 5 seconds sleep last minute stats: " + recorder.getNumberOfLastMinuteEvents());
        System.out.println("After 5 seconds sleep last hour stats: " + recorder.getNumberOfLastHourEvents());
        System.out.println("After 5 seconds sleep last day stats: " + recorder.getNumberOfLastDayEvents());
    }
}
