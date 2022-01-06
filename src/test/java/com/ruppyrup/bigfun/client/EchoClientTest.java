package com.ruppyrup.bigfun.client;

import com.ruppyrup.bigfun.TestServer;
import com.ruppyrup.bigfun.clientcommands.Command;
import com.ruppyrup.bigfun.clientcommands.CommandFactory;
import com.ruppyrup.bigfun.clientcommands.EchoCommands;
import com.ruppyrup.bigfun.controllers.ClientController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EchoClientTest {

  private static final int PORT = 3333;
  private CompletableFuture<String> testServerFuture;
  private EchoClient echoClient;

  @Mock
  CommandFactory commandFactory;

  @Mock
  private ClientController clientController;


  @BeforeEach
  void setup() {
    testServerFuture = CompletableFuture.supplyAsync(() -> new TestServer().startServer(PORT));
    echoClient = new EchoClient(commandFactory, "localhost", 3333);
  }

  @AfterEach
  void pulldown() {
    echoClient.stopConnection();
    String complete = testServerFuture.join();
    Assertions.assertEquals("Success", complete);
  }

  @Test
  void sendMessage() {

    Command mockCommand = mock(Command.class);

    when(commandFactory.getCommand(any(EchoCommands.class), anyString())).thenReturn(mockCommand);

    EchoClientResult echoClientResult = echoClient.startConnection();
    verify(mockCommand, times(2)).execute();

    Assertions.assertEquals(EchoClientResult.SUCCESS, echoClientResult);

  }
}
