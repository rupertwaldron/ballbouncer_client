package com.ruppyrup.bigfun;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ruppyrup.bigfun.clientcommands.EchoCommands.ADD_PLAYER;
import static com.ruppyrup.bigfun.clientcommands.EchoCommands.BALL_POSITION;
import static com.ruppyrup.bigfun.clientcommands.EchoCommands.CO_ORD;
import static com.ruppyrup.bigfun.clientcommands.EchoCommands.HIT_COUNT;
import static com.ruppyrup.bigfun.clientcommands.EchoCommands.QUIT;
import static com.ruppyrup.bigfun.clientcommands.EchoCommands.REMOVE_PLAYER;
import static com.ruppyrup.bigfun.clientcommands.EchoCommands.TEST;

public class TestServer {

    private boolean enableServer = true;
    private final Map<String, PrintWriter> clients = new HashMap<>();
    private final ExecutorService executorService;

    public TestServer() {
        this.executorService = Executors.newFixedThreadPool(2);
    }

    public String startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Server is running");
            while (enableServer)
                new EchoClientHandler(serverSocket.accept()).run();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Server stopped");
            stop();
        }
        return "Success";
    }

    public void stop() {
        enableServer = false;
    }

    public void sendBallPosition(double newXPosition, double newYPosition) {
        clients.forEach((id, writer) -> writer.println(BALL_POSITION + ">" + "all" + "%" + newXPosition + ":" + newYPosition));
    }

    public void sendHitCount(String id, int count) {
        clients.get(id).println(HIT_COUNT+ ">" + count);
    }

    private class EchoClientHandler {
        private Socket clientSocket;
        private String clientId;

        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
            clientId = socket.getInetAddress() + "::" + socket.getPort();
            System.out.println("Connected to client " + clientId);
        }

        public String run() {
            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                addNewPlayerToServerAndExistingPlayers(out);
                System.out.println("Running echo handler");

                for (int i = 0; i < 2; i++) {
                    clients.forEach((id, writer) -> {
                        writer.println(TEST + ">" + clientId);
                        out.println(TEST + ">" + id);
                    });
                }

                clients.forEach((id, writer) -> {
                    writer.println(QUIT + ">" + clientId);
                    out.println(QUIT + ">" + id);
                });


//                String inputLine;
//                while ((inputLine = in.readLine()) != null) {
//                    if (".".equals(inputLine)) {
//                        out.println("bye");
//                        break;
//                    }
//                    updateServerAndPlayersWithUpdatedPlayerLocation(inputLine);
//                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                stop();
                System.out.println("Client closed:: " + clientId);
                clients.remove(clientId);
                clients.forEach((key, value) -> value.println(REMOVE_PLAYER + ">" + clientId));
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "Success";
        }

        private void updateServerAndPlayersWithUpdatedPlayerLocation(String inputLine) {
            String sendMessage = CO_ORD + ">" +  clientId + "%" + inputLine;
            String[] xyValues = inputLine.split(":");
            clients.forEach((id, writer) -> writer.println(sendMessage));
        }

        private void addNewPlayerToServerAndExistingPlayers(PrintWriter out) {
            clients.forEach((id, writer) -> {
                writer.println(ADD_PLAYER + ">" + clientId);
                out.println(ADD_PLAYER + ">" + id);
            });

            clients.put(clientId, out);
        }
    }
}
