package ru.nsu.shadrina.client;

import ru.nsu.shadrina.Commons;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;

public class Client implements Commons {

    public static void main(String args[]) {
        new Client();
    }

    private Client() {
        try {
            System.out.println("Connecting to host " + SERVER_HOST + " on port " + SERVER_PORT);

            Socket echoSocket = null;
            DataOutputStream out = null;
            BufferedReader in = null;

            try {
                echoSocket = new Socket(SERVER_HOST, SERVER_PORT);

                out = new DataOutputStream(echoSocket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            } catch (UnknownHostException ex) {
                System.err.println("Unknown host: " + SERVER_HOST);
                System.exit(1);
            } catch (IOException ex) {
                System.err.println("Unable to get streams from server");
                System.exit(1);
            }

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.print("File path: ");
                String filePath = userInput.readLine();
                /* Exit on 'q' char sent */
                if ("q".equals(filePath)) {
                    break;
                }
                byte[] myPacket = generateMyPacket(filePath);
                out.write(myPacket);
                out.flush();
                System.out.println("Response: " + in.readLine());
            }

            out.close();
            in.close();
            userInput.close();
            echoSocket.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static byte[] generateMyPacket(String filePath) {
        File file = new File(filePath);
        byte[] myHeader = generateMyHeader(file);
        byte[] payload = getFileContent(file);
        byte[] packet = new byte[myHeader.length + payload.length];
        System.arraycopy(myHeader, 0, packet, 0, myHeader.length);
        System.arraycopy(payload, 0, packet, myHeader.length, payload.length);

        return packet;
    }

    private static byte[] generateMyHeader(File file) {
        String infoString = file.getName() + ":" + file.length();
        byte[] info = infoString.getBytes();
        byte[] header = new byte[info.length + 1];
        header[0] = (byte)info.length;
        System.arraycopy(info, 0, header, 1, info.length);

        return header;
    }

    private static byte[] getFileContent(File file) {
        byte[] result = null;
        try {
            result = Files.readAllBytes(file.toPath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }
}