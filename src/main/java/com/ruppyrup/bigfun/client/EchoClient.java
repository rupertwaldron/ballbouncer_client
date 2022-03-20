package com.ruppyrup.bigfun.client;

import com.ruppyrup.bigfun.clientcommands.Command;
import com.ruppyrup.bigfun.clientcommands.CommandFactory;
import com.ruppyrup.bigfun.clientcommands.EchoCommands;
import com.ruppyrup.bigfun.controllers.ClientController;
import java.io.Writer;
import java.net.UnknownHostException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoClient extends Service<EchoClientResult> {

  private boolean connected = false;
  private PrintWriter out;
  private final String ipAddress;
  private final int port;
  private final CommandFactory commandFactory;
  private Socket clientSocket;
  private BufferedReader in;

  public EchoClient(
      CommandFactory clientCommandFactory,
      String ipAddress,
      int port
  ) {
    this.ipAddress = ipAddress;
    this.port = port;
    this.commandFactory = clientCommandFactory;
  }

  public EchoClientResult startConnection() {
    try {
      clientSocket = new Socket(ipAddress, port);
      if (clientSocket.isConnected()) {
        System.out.println("Connected to server :: " + clientSocket.getInetAddress());
        connected = true;
      }

      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      out = new PrintWriter(clientSocket.getOutputStream(), true);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return EchoClientResult.SUCCESS;
  }

  public EchoClient receiveCommand() {
    String commandFromServer;
    try {
      commandFromServer = in.readLine();
      if (commandFromServer == null) return this;
      String[] serverInput = commandFromServer.split(">");
      System.out.println(serverInput[0] + ">" + serverInput[1]);
      Command command = commandFactory.getCommand(EchoCommands.valueOf(serverInput[0]),
          serverInput[1]);
      command.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return this;
  }

  public String sendMessage(String msg) {
    out.println(msg);
    return msg;
  }

  public void stopConnection() {
    connected = false;
    try {
      clientSocket.close();
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    out.close();
  }

  public boolean isConnected() {
    return connected;
  }

  @Override
  protected Task<EchoClientResult> createTask() {
    return new Task<>() {
      @Override
      protected EchoClientResult call() throws Exception {
        startConnection();
        while (isConnected()) {
          receiveCommand();
        }
        return EchoClientResult.SUCCESS;
      }
    };
  }
}
