package com.ruppyrup.bigfun.clientcommands;

import com.ruppyrup.bigfun.client.EchoClient;

public class Quit implements Command{
    private final EchoClient echoClient;

    public Quit(EchoClient echoClient) {
        this.echoClient = echoClient;
    }

    @Override
    public void execute() {
        System.out.println("Been asked to quit by server");
        echoClient.stopConnection();
    }
}
