package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String address = "127.0.0.1";
        int port = 12345;
        System.out.println("Enter action (1 - get a file, 2 - create a file, 3 - delete a file): ");
        Scanner scanner = new Scanner(System.in);
        String action = scanner.nextLine();
        try (Socket socket = new Socket(InetAddress.getByName(address), port);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
            if (action.equals("2")) {
                System.out.println("Enter filename: ");
                String fileName = scanner.nextLine();
                System.out.println("Enter file content: ");
                String content = scanner.nextLine();
                String clientMessage = "PUT " + fileName + " " + content;
                output.writeUTF(clientMessage);
                System.out.println("The request was sent.");
                String receivedMsg = input.readUTF();
                if (receivedMsg.equals("200")) {
                    System.out.println("The response says that the file was created!");
                } else if (receivedMsg.equals("403")) {
                    System.out.println("The response says that creating the file was forbidden!");
                }

            } else if (action.equals("1")) {
                System.out.println("Enter filename: ");
                String fileName = scanner.nextLine();
                String clientMessage = "GET " + fileName;
                output.writeUTF(clientMessage);
                System.out.println("The request was sent.");
                String receivedMsg = input.readUTF();
                String code = receivedMsg.substring(0, 3);
                if (code.equals("200")) {
                    System.out.println("The content of the file is: " + receivedMsg.substring(4));
                } else if (code.equals("404")) {
                    System.out.println("The response says that the file was not found!");
                }
            } else if (action.equals("3")) {
                System.out.println("Enter filename: ");
                String fileName = scanner.nextLine();
                String clientMessage = "DELETE " + fileName;
                output.writeUTF(clientMessage);
                System.out.println("The request was sent.");
                String receivedMsg = input.readUTF();
                if (receivedMsg.equals("200")) {
                    System.out.println("The response says that the file was successfully deleted!");
                } else if (receivedMsg.equals("404")) {
                    System.out.println("The response says that the file was not found!");
                }
            } else if (action.equals("exit")) {
                output.writeUTF("EXIT");
                System.out.println("The request was sent.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}