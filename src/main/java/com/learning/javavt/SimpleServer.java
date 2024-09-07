package com.learning.javavt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimpleServer {
//    ExecutorService executorService = Executors.newFixedThreadPool(5);
    ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private static SimpleServer simpleServer= null;
    private SimpleServer() {

    }

    public static SimpleServer getServer() {
        if (simpleServer == null) simpleServer = new SimpleServer();
        return simpleServer;
    }
    public void serve() {
        System.out.println("Server started");
        try(ServerSocket serverSocket = new ServerSocket(32000)) {
            while (true) {
                Socket client = serverSocket.accept();
                Thread.ofVirtual().start(() -> process(client));
//                executorService.submit(() -> process(client));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void process(Socket clientSocket) {
        String outText = "{\"StartThread\":  \"%s\",".formatted(Thread.currentThread().toString());
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            String line = bufferedReader.readLine();
            if (line != null && !line.isEmpty()) {
                System.out.println("inout: " + line );
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: application/json");
                out.println();
                outText += "\"EndThread\": \"%s\"}".formatted(Thread.currentThread().toString());
                out.println(outText);

                while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                    System.out.println(line);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        var server = SimpleServer.getServer();
        server.serve();
    }
}
