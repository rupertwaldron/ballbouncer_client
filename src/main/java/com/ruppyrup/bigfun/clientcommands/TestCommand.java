package com.ruppyrup.bigfun.clientcommands;

public class TestCommand implements Command {

  @Override
  public void execute() {
    System.out.println("In test mode");
  }
}
