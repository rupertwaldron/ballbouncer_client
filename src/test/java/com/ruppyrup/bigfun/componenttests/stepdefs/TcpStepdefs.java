package com.ruppyrup.bigfun.componenttests.stepdefs;

import com.jfoenix.controls.JFXBadge;
import com.ruppyrup.bigfun.ClientApplication;
import com.ruppyrup.bigfun.Launcher;
import com.ruppyrup.bigfun.client.EchoClient;
import com.ruppyrup.bigfun.clientcommands.CommandFactory;
import com.ruppyrup.bigfun.componenttests.mockservers.TestServer;
import com.ruppyrup.bigfun.componenttests.mockservers.TestServer.EchoClientHandler;
import com.ruppyrup.bigfun.controllers.ClientController;
import com.sun.javafx.application.PlatformImpl;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;

public class TcpStepdefs {
  private CompletableFuture<EchoClientHandler> testServerFuture;
  private CompletableFuture<EchoClient> echoClientFuture;
  private CommandFactory commandFactory;
  private MockClientController clientController;

  @Before("@Tcp")
  public void setUp() {
    clientController = new MockClientController();
    commandFactory = new CommandFactory(clientController);
    PlatformImpl.startup(() -> {
      try {
        new ClientApplication().start(new Stage());
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  @After("@Tcp")
  public void cleanUp() {
    PlatformImpl.exit();
    testServerFuture.join();
    echoClientFuture.join();
  }

  @Given("The Bouncer Server has started on port {int}")
  public void theBoucerServerHasStartedOnPort(int port) {
    testServerFuture = CompletableFuture.supplyAsync(() -> new TestServer().startServer(port));
  }

  @And("The Bouncer Client is started on port {int}")
  public void theBouncerClientIsStartedOnPort(int port) {
    echoClientFuture = CompletableFuture.supplyAsync(() -> new EchoClient(commandFactory, "localhost", port));
    echoClientFuture = echoClientFuture.thenApplyAsync(echoClient -> {
      echoClient.startConnection();
      return echoClient;
    });
  }


  @When("The Bouncer Server sends the ball position command")
  public void theBouncerServerSendsTheBallPositionCommand() {
    String commandFromServer = "BALL_POSITION" + ">" + "all" + "%" + 150 + ":" + 180;
    testServerFuture.thenApplyAsync(echoClientHandler -> echoClientHandler.run(commandFromServer));
  }

  @Then("The Bouncer Client will receive the command")
  public void theBouncerClientWillReceiveTheCommand() {
    echoClientFuture.thenApplyAsync(EchoClient::receiveCommand);
  }

  @And("The ball position command is executed")
  public void theBallPositionCommandIsExecuted() {
    Assertions.assertEquals(150, clientController.getX());
    Assertions.assertEquals(180, clientController.getY());
  }
}

class MockClientController extends ClientController {
  private double x;
  private double y;

  @Override
  public void moveBall(Double xValue, Double yValue) {
    this.x = xValue;
    this.y = yValue;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }
}
