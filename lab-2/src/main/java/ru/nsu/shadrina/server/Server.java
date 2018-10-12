package ru.nsu.shadrina.server;

import ru.nsu.shadrina.Commons;
import ru.nsu.shadrina.Timer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class Server extends Thread implements Commons {

    private static void errorExit(String message) {
        System.err.println(message);
        System.exit(1);
    }

    public static void main(String[] args) {
        System.out.println("Server started...");

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            while (true) new Server(serverSocket.accept());

        } catch (IOException ex) {
            errorExit("Unable to start server");

        } finally {
            try {
                if (serverSocket != null) serverSocket.close();

            } catch (IOException ex) {
                errorExit("Can't close server socket");
            }
        }
    }

    private Socket socket;
    private Timer timer = new Timer();

    private Server(Socket socket) {
        this.socket = socket;
        System.out.println(
                "New client connected from "
                        + socket.getInetAddress().getHostAddress()
                        + ":" + socket.getPort()
        );
        start();
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
                    errorExit("Error while handling request");
                }
            }

        } catch (IOException ex) {
            System.err.println("Unable to get streams from client");

        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                socket.close();

            } catch (IOException ex) {
                errorExit("Can't close resources");
            }
        }
    }

    private void handleRequest(InputStream inputStream) throws IOException {
        int headerSize = inputStream.read();
        if (headerSize == -1) errorExit("Invalid request");
        byte[] header = new byte[headerSize];
        int read = inputStream.read(header);
        if (read == -1) errorExit("Invalid request");

        String info[] = new String(header, Charset.forName("UTF-8")).split(":");
        String fileName = info[0];
        Long fileSize = Long.parseLong(info[1]);

        long start = System.currentTimeMillis();
        writeFile(inputStream, fileName, fileSize);
        long end = System.currentTimeMillis();
        double seconds = (end - start) * 1. / 1000;
        timer.setCurrentSeconds(seconds);
        timer.setMbCountCurrent(fileSize * 1. / 1024. / 1024.);
        timer.outputStatistics();
    }

    private void writeFile(
            InputStream inputStream,
            String fileName,
            Long fileSize
    ) throws IOException {

        String path = UPLOAD_PATH + fileName;
        FileOutputStream fos = new FileOutputStream(path);

        System.out.println("Start receiving...");

        long receivedData = 0;
        int receivedDataPercent = 0;
        int previousOutputByteSize = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        while (receivedData < fileSize) {
            int code = inputStream.read(buffer);
            if (code == -1) errorExit("Invalid request");

            receivedData += code;
            fos.write(buffer, 0, code);
            fos.flush();

            for (int i = 0; i < previousOutputByteSize; i++) {
                System.out.print('\r');
            }
            System.out.flush();

            receivedDataPercent = (int)(receivedData * 1. / fileSize * 100);
            String output = receivedDataPercent + "%";
            System.out.print(output);
            System.out.flush();
            previousOutputByteSize = output.getBytes().length;
        }
        System.out.println("\nSaved new file " + fileName + "!");

        fos.flush();
        fos.close();
    }
}
