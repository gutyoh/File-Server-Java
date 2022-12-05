package client;

import common.HttpRequestMethod;
import common.Request;
import common.Response;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class ClientLogic {

    private final String ADDRESS;
    private final int PORT;
    private final String dataDir;

    public ClientLogic(String ADDRESS, int PORT) {
        this.ADDRESS = ADDRESS;
        this.PORT = PORT;
        dataDir = "./src/client/data"; // tests require this specific location
        new File(dataDir).mkdirs();
    }

    public void start() {
        final TextUserInterface ui = new TextUserInterface();
        ui.setClientLogic(this); // to facilitate bidirectional information flow between ui and client logic

        // 1. Get user action & create request
        Request requestToServer = ui.createRequest();
        HttpRequestMethod httpMethod = requestToServer.getHttpRequestMethod();

        // 2. Connect
        try (Socket socket = new Socket(InetAddress.getByName(ADDRESS), PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream()); // OOS before OIS, otherwise deadlock!
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())
        ) {
            // 3. Send request
            output.writeObject(requestToServer);
            System.out.println("The request was sent.");
            if (requestToServer.getHttpRequestMethod() == HttpRequestMethod.EXIT) { // terminate client after asking the server to terminate
                System.exit(0); // also prevents exceptions by interrupted socket communication
            }

            // 4. Get + show response
            Response responseFromServer = (Response) input.readObject();
            // System.out.printf("##DIAG## HTTP Method: %s Server Response: %s\n", httpMethod, responseFromServer);
            ui.handleResponse(httpMethod, responseFromServer); //request type is needed for correct response handling

        } catch (IOException | ClassNotFoundException e) {
            System.out.printf("Error creating client-side socket:\n%s\n", e.getMessage());
            e.printStackTrace();
        }
    }

    public String getDataDir() {
        return dataDir;
    }
}