package ru.nsu.shadrina.server;

import ru.nsu.shadrina.Commons;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class Server extends Thread implements Commons {

    private static RequestTimeMeter myTimer = new RequestTimeMeter();

    public static void main(String[] args) {
        System.out.println("Server started...");
        myTimer.start();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            while (true) new Server(serverSocket.accept());
        } catch (IOException ex) {
            System.err.println("Unable to start server");
            ex.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            while (true) {
                try {
                    handleRequest(in);
                    out.write("File successfully saved!\n".getBytes());
                    out.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    break;
                }
            }

        } catch (IOException ex) {
            System.err.println("Unable to get streams from client");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private Socket socket;

    private Server(Socket socket) {
        this.socket = socket;
        System.out.println(
                "New client connected from "
                        + socket.getInetAddress().getHostAddress()
                        + ":" + socket.getPort()
        );
        start();
    }

    private void handleRequest(InputStream inputStream) throws IOException {
        int headerSize = inputStream.read();
        if (headerSize == -1) invalidRequestError();
        byte[] header = new byte[headerSize];
        int read = inputStream.read(header);
        if (read == -1) invalidRequestError();

        String info[] = new String(header, Charset.forName("UTF-8")).split(":");
        String fileName = info[0];
        long fileSize = Long.parseLong(info[1]);

        String path = "uploads\\" + fileName;
        FileOutputStream outputStream = new FileOutputStream(path);

        long startTime = System.currentTimeMillis();
        writeData(inputStream, outputStream, fileSize);
        long stopTime = System.currentTimeMillis();
        double elapsedTime = stopTime - startTime;
        myTimer.lastSpeed = new Speed(elapsedTime, fileSize);

        outputStream.flush();
        outputStream.close();
    }

    private void writeData(InputStream inputStream, OutputStream outputStream, long dataSize) throws IOException {
        int data;
        for (int i = 0; i < dataSize; i++) {
            data = inputStream.read();
            if (data == -1) invalidRequestError();
            outputStream.write(data);
        }
    }

    private void invalidRequestError() {
        System.err.println("Invalid request");
        System.exit(1);
    }
}
