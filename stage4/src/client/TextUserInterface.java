package client;

import common.HttpRequestMethod;
import common.HttpResponseCode;
import common.Request;
import common.Response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.UUID;

public class TextUserInterface {

    private final Scanner scan;
    private ClientLogic clientLogic;

    public TextUserInterface() {
        scan = new Scanner(System.in);
    }

    public Request createRequest() {
        System.out.print("Enter action (1 - get a file, 2 - save a file, 3 - delete a file): > ");
        String menuChoice = scan.nextLine();

        return switch (menuChoice) {
            case "1" -> composeGet();
            case "2" -> composePut();
            case "3" -> composeDelete();
            default -> new Request(HttpRequestMethod.EXIT);
        };
    }

    public Request composeGet() {
        System.out.print("Do you want to get the file by name or by id (1 - name, 2 - id): > ");
        boolean nameVsId = Integer.parseInt(scan.nextLine()) == 1;
        Request request;

        if (nameVsId) {
            System.out.print("Enter filename: > ");
            String fileName = scan.nextLine();
            request = new Request(HttpRequestMethod.GET, fileName);
        } else {
            System.out.print("Enter id: > ");
            int fileId = Integer.parseInt(scan.nextLine());
            request = new Request(HttpRequestMethod.GET, fileId);
        }

        return request;
    }

    public Request composePut() {
        System.out.print("Enter name of the file: > ");
        String fileName = scan.nextLine().replaceAll("\\s", ""); // remove all whitespace
        System.out.print("Enter name of the file to be saved on server: > ");
        String serverFileName = scan.nextLine().replaceAll("\\s", ""); // remove all whitespace;
        if (serverFileName.isEmpty()) {
            serverFileName = UUID.randomUUID().toString(); // generate unique server file name, if none specified
        }
        File file = new File(clientLogic.getDataDir() + "/" + fileName);
        byte[] fileContent = new byte[0];
        try {
            fileContent = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            System.out.printf("Exception during file operation:\n%s\n", e.getMessage());
        }
        return new Request(HttpRequestMethod.PUT, serverFileName, fileContent);
    }

    public Request composeDelete() {
        System.out.print("Do you want to delete the file by name or by id (1 - name, 2 - id): > ");
        boolean nameVsId = Integer.parseInt(scan.nextLine()) == 1;
        Request request;

        if (nameVsId) {
            System.out.print("Enter filename: > ");
            String fileName = scan.nextLine();
            request = new Request(HttpRequestMethod.DELETE, fileName);
        } else {
            System.out.print("Enter id: > ");
            int fileId = Integer.parseInt(scan.nextLine());
            request = new Request(HttpRequestMethod.DELETE, fileId);
        }

        return request;
    }

    public void handleResponse(HttpRequestMethod httpMethod, Response responseFromServer) {
        switch (httpMethod) {
            case GET:
                if (responseFromServer.getHttpResponseCode() == HttpResponseCode.HTTP_NOT_FOUND) {
                    System.out.println("The response says that this file is not found!");
                } else {
                    System.out.print("The file was downloaded! Specify a name for it: > ");
                    String fileName = scan.nextLine();

                    File file = new File(clientLogic.getDataDir() + "/" + fileName);

                    try {
                        Files.write(file.toPath(), responseFromServer.getFileContent());
                    } catch (IOException e) {
                        System.out.printf("Exception during file operation:\n%s\n", e.getMessage());
                    }
                    System.out.println("File saved on the hard drive!");
                }
                break;

            case PUT:
                if (responseFromServer.getHttpResponseCode() == HttpResponseCode.HTTP_OK) {
                    System.out.printf("Response says that file is saved! ID = %d\n", responseFromServer.getFileID());
                } else if (responseFromServer.getHttpResponseCode() == HttpResponseCode.HTTP_FORBIDDEN) {
                    System.out.println("The response says that creating the file was forbidden!");
                }
                break;

            case DELETE:
                if (responseFromServer.getHttpResponseCode() == HttpResponseCode.HTTP_OK) {
                    System.out.println("The response says that the file was successfully deleted!");
                } else if (responseFromServer.getHttpResponseCode() == HttpResponseCode.HTTP_NOT_FOUND) {
                    System.out.println("The response says that the file was not found!");
                }
                break;
            default:
        }
    }

    public void setClientLogic(ClientLogic clientLogic) {
        this.clientLogic = clientLogic;
    }

}