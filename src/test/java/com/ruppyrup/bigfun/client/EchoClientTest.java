package com.ruppyrup.bigfun.client;

import com.ruppyrup.bigfun.TestServer;
import com.ruppyrup.bigfun.controllers.ClientController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EchoClientTest {
    private static final int PORT = 3333;
    private static Thread testServerThread;
    private static EchoClient echoClient;

    @Mock
    private ClientController clientController;

    @BeforeEach
    void setup() {
        testServerThread = new Thread(() -> new TestServer().startServer(PORT));
        testServerThread.start();
    }

    @AfterEach
    void pulldown() throws InterruptedException {
        echoClient.stopConnection();
        testServerThread.join();
    }

    @Test
    void sendMessage() {

        echoClient = new EchoClient(clientController, "localhost", 3333);
        EchoClientResult echoClientResult = echoClient.startConnection();

        Assertions.assertEquals(EchoClientResult.SUCCESS, echoClientResult);

    }
}
