package ru.stolpner.task.yandex;

public class EventRecorderTest {
    public static void main(String[] args) {
        EventRecorder recorder = new EventRecorder();
        System.out.println("Empty recorder get last minute stats: " + recorder.getNumberOfLastMinuteEvents());
        System.out.println("Empty recorder get last hour stats: " + recorder.getNumberOfLastHourEvents());
        System.out.println("Empty recorder get last day stats: " + recorder.getNumberOfLastDayEvents());
    }
}
