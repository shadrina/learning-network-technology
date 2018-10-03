package ru.nsu.shadrina.server;

public class Speed {
    private double kbInSecond;

    Speed(double timeInMilliseconds, long bytesCount) {
        double seconds = timeInMilliseconds / 1000;
        double bytesInSecond = bytesCount / seconds;
        kbInSecond = bytesInSecond / 1024;
    }

    @Override
    public String toString() {
        return "Speed: " + (long)kbInSecond + " kB/sec";
    }
}
