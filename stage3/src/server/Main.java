package server;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String address = "127.0.0.1";
        int port = 12345;
        System.out.println("Server started!");
        boolean condition = true;
        try (ServerSocket server = new ServerSocket(port, 50, InetAddress.getByName(address))) {
            while (condition) {
                try (Socket socket = server.accept();
                     DataInputStream input = new DataInputStream(socket.getInputStream());
                     DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
                    String receivedMsg = input.readUTF();
                    String[] received = receivedMsg.split(" ");
//                    String dataDir = System.getProperty("user.dir") + File.separator
//                            + "src" + File.separator + "server" + File.separator + "data" + File.separator;
                    String dataDir = "C:\\Users\\mrgut\\Documents\\Rock Paper Scissors\\File Server\\stage3\\src\\server\\data";
                    if (received[0].equals("PUT")) {
                        File file = new File(dataDir + received[1]);
                        if (file.createNewFile()) {
                            int index = receivedMsg.indexOf(" ", 7);
                            String content = receivedMsg.substring(index + 1);
                            FileWriter writer = new FileWriter(file);
                            writer.write(content);
                            writer.close();
                            output.writeUTF("200");
                        } else {
                            output.writeUTF("403");
                        }
                    } else if (received[0].equals("GET")) {
                        File file = new File(dataDir + received[1]);
                        if (file.exists()) {
                            Scanner scanner = new Scanner(file);
                            String content = "";
                            while (scanner.hasNext()) {
                                content += scanner.nextLine() + "\n";
                            }
                            output.writeUTF("200 " + content);
                        } else {
                            output.writeUTF("404");
                        }
                    } else if (received[0].equals("DELETE")) {
                        File file = new File(dataDir + received[1]);
                        if (file.delete()) {
                            output.writeUTF("200");
                        } else {
                            output.writeUTF("404");
                        }
                    } else if (received[0].equalsIgnoreCase("exit")) {
                        condition = false;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}