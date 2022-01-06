package com.ruppyrup.bigfun.clientcommands;

import com.ruppyrup.bigfun.controllers.ClientController;

public class CommandFactory {

  private final ClientController animationController;

  public CommandFactory(ClientController animationController) {
    this.animationController = animationController;
  }

  public Command getCommand(EchoCommands echoCommands, String input) {
    return switch (echoCommands) {
      case ADD_PLAYER -> new AddPlayer(animationController, input);
      case CO_ORD -> new MovePosition(animationController, input);
      case BALL_POSITION -> new BallPosition(animationController, input);
      case REMOVE_PLAYER -> new RemovePlayer(animationController, input);
      case HIT_COUNT -> new AddHit(animationController, input);
      case QUIT -> new Quit(animationController);
      case TEST -> new TestCommand();
      default -> throw new IllegalStateException("Unexpected value: " + echoCommands);
    };
  }
}
