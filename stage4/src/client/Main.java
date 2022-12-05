package client;

import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) {
        ClientLogic clientLogic = new ClientLogic("127.0.0.1", 23456);
        clientLogic.start();
    }
}