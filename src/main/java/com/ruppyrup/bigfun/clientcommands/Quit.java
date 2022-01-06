package com.ruppyrup.bigfun.clientcommands;

import com.ruppyrup.bigfun.controllers.ClientController;

public class Quit implements Command {

  private final ClientController clientController;

  public Quit(ClientController clientController) {
    this.clientController = clientController;
  }

  @Override
  public void execute() {
    System.out.println("Been asked to quit by server");
    clientController.disconnectFromServer();
  }
}
