package com.ruppyrup.bigfun.client;

import com.ruppyrup.bigfun.TestServer;
import com.ruppyrup.bigfun.TestServer.EchoClientHandler;
import com.ruppyrup.bigfun.clientcommands.BallPosition;
import com.ruppyrup.bigfun.clientcommands.Command;
import com.ruppyrup.bigfun.clientcommands.CommandFactory;
import com.ruppyrup.bigfun.clientcommands.EchoCommands;
import com.ruppyrup.bigfun.clientcommands.Quit;
import com.ruppyrup.bigfun.controllers.ClientController;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static com.ruppyrup.bigfun.clientcommands.EchoCommands.BALL_POSITION;
import static com.ruppyrup.bigfun.clientcommands.EchoCommands.QUIT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EchoClientTest {

  private static final int PORT = 3333;
  private CompletableFuture<EchoClientHandler> testServerFuture;
  private EchoClient echoClient;

  @Mock
  CommandFactory commandFactory;

  @BeforeEach
  void setup() {
    testServerFuture = CompletableFuture.supplyAsync(() -> new TestServer().startServer(PORT));
    echoClient = new EchoClient(commandFactory, "localhost", 3333);
  }

  @AfterEach
  void pulldown() {
    echoClient.stopConnection();
  }

  @Test
  void sendBallPositionMessage() {

    Command mockBallPositionCommand = mock(BallPosition.class);
    Command mockQuitCommand = mock(Quit.class);

    String commandFromServer = "BALL_POSITION" + ">" + "all" + "%" + 150 + ":" + 150;

    when(commandFactory.getCommand(eq(BALL_POSITION), anyString())).thenReturn(mockBallPositionCommand);
    when(commandFactory.getCommand(eq(QUIT), anyString())).thenReturn(mockQuitCommand);

    CompletableFuture<Integer> handlerFuture = testServerFuture.thenApplyAsync(
        handler -> handler.run(commandFromServer));

    EchoClientResult echoClientResult = echoClient.startConnection();
    verify(mockBallPositionCommand, times(1)).execute();
    verify(mockQuitCommand, times(1)).execute();

    Assertions.assertEquals(1, handlerFuture.join());
    Assertions.assertEquals(EchoClientResult.SUCCESS, echoClientResult);
  }
}
