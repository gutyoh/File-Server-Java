package server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        ServerLogic serverLogic = new ServerLogic("127.0.0.1", 23456);
        serverLogic.start();
    }
}