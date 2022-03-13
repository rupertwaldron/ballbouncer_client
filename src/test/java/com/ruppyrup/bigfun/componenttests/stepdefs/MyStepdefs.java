package com.ruppyrup.bigfun.componenttests.stepdefs;

import com.ruppyrup.bigfun.TestServer;
import com.ruppyrup.bigfun.TestServer.EchoClientHandler;
import com.ruppyrup.bigfun.client.EchoClient;
import com.ruppyrup.bigfun.clientcommands.CommandFactory;
import com.ruppyrup.bigfun.controllers.ClientController;
import io.cucumber.java.Before;
import java.util.concurrent.CompletableFuture;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class MyStepdefs {

  private static final int PORT = 3333;
  private CompletableFuture<EchoClientHandler> testServerFuture;
  private EchoClient echoClient;
  private ClientController clientController;
  private CommandFactory commandFactory;

  @Before
  void setup() {
    testServerFuture = CompletableFuture.supplyAsync(() -> new TestServer().startServer(PORT));
    echoClient = new EchoClient(commandFactory, "localhost", 3333);
    clientController = new ClientController();
    commandFactory = new CommandFactory(clientController);
  }

  @After
  void pulldown() {
    echoClient.stopConnection();
    testServerFuture.join();
  }

}
