package com.ruppyrup.bigfun.common;

import javafx.scene.shape.Circle;

public class Ball implements Moveable {
  private final Circle circle;
  private final String id;

  public Ball(String id, Circle circle) {
    this.circle = circle;
    this.id = id;
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

  public Circle getCircle() {
    return circle;
  }

}
