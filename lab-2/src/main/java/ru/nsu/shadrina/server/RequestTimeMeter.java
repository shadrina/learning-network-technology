package ru.nsu.shadrina.server;

public class RequestTimeMeter extends Thread {
    Speed lastSpeed = null;

    @Override
    public void run() {
        while (true) {
            try {
                outputLastRequestInfo();
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void outputLastRequestInfo() {
        System.out.print("TIME METER: ");
        if (lastSpeed == null) {
            System.out.println("There were no requests");
        } else {
            System.out.println(lastSpeed);
        }
    }
}
