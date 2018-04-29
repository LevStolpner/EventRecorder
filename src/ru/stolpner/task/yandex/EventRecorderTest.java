package ru.stolpner.task.yandex;

public class EventRecorderTest {
    public static void main(String[] args) {
        EventRecorder recorder = new EventRecorder();
        System.out.println("Empty recorder get last minute stats: " + recorder.getNumberOfLastMinuteEvents());
        System.out.println("Empty recorder get last hour stats: " + recorder.getNumberOfLastHourEvents());
        System.out.println("Empty recorder get last day stats: " + recorder.getNumberOfLastDayEvents());

        recorder.recordEvent(System.currentTimeMillis());
        recorder.recordEvent(System.currentTimeMillis());
        recorder.recordEvent(System.currentTimeMillis());

        System.out.println("3 records last minute stats: " + recorder.getNumberOfLastMinuteEvents());
        System.out.println("3 records last hour stats: " + recorder.getNumberOfLastHourEvents());
        System.out.println("3 records last day stats: " + recorder.getNumberOfLastDayEvents());

        recorder.recordEvent(System.currentTimeMillis() - 50 * 1000);
        recorder.recordEvent(System.currentTimeMillis() - 50 * 60 * 1000);
        recorder.recordEvent(System.currentTimeMillis() - 20 * 60 * 60 * 1000);

        System.out.println("4 records last minute stats: " + recorder.getNumberOfLastMinuteEvents());
        System.out.println("5 records last hour stats: " + recorder.getNumberOfLastHourEvents());
        System.out.println("6 records last day stats: " + recorder.getNumberOfLastDayEvents());
    }
}
