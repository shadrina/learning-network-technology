package ru.nsu.shadrina;

public class Timer {
    private double mbCountCommon = 0;
    private double mbCountCurrent = 0;

    private double commonSeconds = 0;
    private double currentSeconds = 0;

    public void outputStatistics() {
        System.out.println("-----------------");
        System.out.println("Timer statistics:");
        System.out.format("Current speed: %.1f Mb/s", currentSpeed());
        System.out.println();
        System.out.format("Common speed: %.1f Mb/s", commonSpeed());
        System.out.println();
        System.out.println("-----------------");
    }

    private double currentSpeed() {
        if (currentSeconds != 0) {
            return mbCountCurrent / currentSeconds;
        } else {
            return 0;
        }
    }

    private double commonSpeed() {
        if (commonSeconds != 0) {
            return mbCountCommon / commonSeconds;
        } else {
            return 0;
        }
    }

    public void setCurrentSeconds(double currentSeconds) {
        this.currentSeconds = currentSeconds;
        this.commonSeconds += currentSeconds;
    }

    public void setMbCountCurrent(double mbCountCurrent) {
        this.mbCountCurrent = mbCountCurrent;
        this.mbCountCommon += mbCountCurrent;
    }
}
