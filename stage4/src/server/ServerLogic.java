package server;

import common.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerLogic {
    private final Storage storage;

    private final ExecutorService executor;
    private final String ADDRESS;
    private final int PORT;

    public ServerLogic(String ADDRESS, int PORT) {
        this.storage = new Storage();
        this.ADDRESS = ADDRESS;
        this.PORT = PORT;
        this.executor = Executors.newFixedThreadPool(2);
    }

    public void start() {

        // 1. Create server socket
        try (ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS))) {
            System.out.println("Server started!");

            while (true) {
                // 2. Handle connection request
                executor.submit(() -> {
                    try {
                        Socket socket = server.accept();
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                        // System.out.println("##DIAG## Client connected.");

                        // 3. Get request from client
                        Request requestFromClient = (Request) input.readObject();
                        // System.out.println("##DIAG## received: " + requestFromClient);

                        //4. Assemble + send response to client
                        Response messageToClient = handle(requestFromClient);
                        output.writeObject(messageToClient);
                        // System.out.println("##DIAG## sent: " + messageToClient);
                        output.close();
                        input.close();
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Response handle(Request requestFromClient) {
        // stop the server upon exit request from client
        if (requestFromClient.getHttpRequestMethod() == HttpRequestMethod.EXIT) {
            // System.out.println("##DIAG## server halting...");
            storage.serializeMetadata();
            executor.shutdownNow();
            System.exit(0);
        }

        return switch (requestFromClient.getHttpRequestMethod()) {
            case GET -> serveGet(requestFromClient);
            case PUT -> servePut(requestFromClient);
            case DELETE -> serveDelete(requestFromClient);
            default -> new Response(HttpResponseCode.HTTP_NOT_FOUND); // unknown request = resource not found
        };
    }

    public Response serveGet(Request requestFromClient) {
        if (requestFromClient.getOperationMode() == OperationMode.BY_NAME) {
            if (storage.doesFileExist(requestFromClient.getFileName())) {
                return new Response(HttpResponseCode.HTTP_OK, storage.getFileContent(requestFromClient.getFileName()));
            } else {
                return new Response(HttpResponseCode.HTTP_NOT_FOUND);
            }
        } else { // if requestFromClient.getOperationMode() == OperationMode.BY_ID)
            if (storage.doesFileExist(requestFromClient.getFileId())) {
                return new Response(HttpResponseCode.HTTP_OK, storage.getFileContent(requestFromClient.getFileId()));
            } else {
                return new Response(HttpResponseCode.HTTP_NOT_FOUND);
            }
        }
    }

    public Response servePut(Request requestFromClient) {
        int fileId = storage.persist(requestFromClient);
        return new Response(HttpResponseCode.HTTP_OK, fileId);
    }

    public Response serveDelete(Request requestFromClient) {
        if (requestFromClient.getOperationMode() == OperationMode.BY_NAME) {
            return storage.delete(requestFromClient.getFileName())
                    ? new Response(HttpResponseCode.HTTP_OK) : new Response(HttpResponseCode.HTTP_NOT_FOUND);
        } else { // if requestFromClient.getOperationMode() == OperationMode.BY_ID)
            return storage.delete(requestFromClient.getFileId())
                    ? new Response(HttpResponseCode.HTTP_OK) : new Response(HttpResponseCode.HTTP_NOT_FOUND);
        }
    }
}