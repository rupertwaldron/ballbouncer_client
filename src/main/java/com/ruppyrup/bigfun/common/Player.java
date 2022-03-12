package com.ruppyrup.bigfun.common;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Player extends GameObject {

  public Player(String id, int radius, Color color) {
    super(id, radius, color);
  }

  @Override
  public void setVisible() {
    circle.setVisible(true);
  }

  @Override
  public void setInvisible() {
    circle.setVisible(false);
  }

  @Override
  public void remove() {
    circle.setVisible(false);
    circle.setDisable(true);
  }

  @Override
  public void move(double x, double y) {
    circle.setCenterX(x);
    circle.setCenterY(y);
  }

}
