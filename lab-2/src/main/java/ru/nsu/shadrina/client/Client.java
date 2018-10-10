package ru.nsu.shadrina.client;

import ru.nsu.shadrina.Commons;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Commons {

    private static void errorExit(String message) {
        System.err.println(message);
        System.exit(1);
    }

    public static void main(String args[]) {
        new Client();
    }

    private Client() {
        System.out.println("Connecting to host " + SERVER_HOST + " on port " + SERVER_PORT);

        Socket echoSocket = null;
        OutputStream out = null;
        BufferedReader in = null;
        BufferedReader userIn = null;
        try {
            echoSocket = new Socket(SERVER_HOST, SERVER_PORT);
            out = echoSocket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            userIn = new BufferedReader(new InputStreamReader(System.in));

            while (processRequest(out, in, userIn));

        } catch (UnknownHostException ex) {
            errorExit("Unknown host: " + SERVER_HOST);

        } catch (IOException ex) {
            errorExit("Unable to get streams from server");

        } finally {
            try {
                if (echoSocket != null) echoSocket.close();
                if (out != null) out.close();
                if (in != null) in.close();
                if (userIn != null) userIn.close();

            } catch (IOException ex) {
                errorExit("Unable to close resources");
            }
        }
    }

    private boolean processRequest(
            OutputStream out,
            BufferedReader in,
            BufferedReader userIn
    ) throws IOException {
        System.out.print("File path: ");

        String filePath = userIn.readLine();
        if (filePath.equals("q")) return false;

        File file = new File(filePath);
        sendHeader(out, file);
        sendFile(out, file);

        System.out.println("Response: " + in.readLine());
        return true;
    }

    private void sendHeader(OutputStream out, File file) throws IOException {
        long fileByteSize = file.length();

        String infoString = file.getName() + ":" + fileByteSize;
        byte[] info = infoString.getBytes();
        byte[] header = new byte[info.length + 1];
        header[0] = (byte)info.length;
        System.arraycopy(info, 0, header, 1, info.length);

        out.write(header);
        out.flush();
    }

    private void sendFile(OutputStream out, File file) throws IOException {
        FileInputStream fin = new FileInputStream(file);
        byte[] buffer = new byte[BUFFER_SIZE];

        long sentData = 0;
        while (sentData != file.length()) {
            int code = fin.read(buffer);
            if (code == -1) errorExit("FileInputStream.read() returned -1");
            out.write(buffer, 0, code);
            out.flush();

            sentData += code;
        }

        fin.close();
    }
}