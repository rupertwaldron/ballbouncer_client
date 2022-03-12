package com.ruppyrup.bigfun.common;

import com.ruppyrup.bigfun.utils.Position;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public abstract class GameObject {
  protected final Circle circle;
  protected final String id;

  public GameObject(String id, int radius, Color color) {
    this.circle = createCircle(radius, color, new Position(Math.random() * 600, Math.random() * 400));
    this.id = id;
  }

  public void setVisible() {
    circle.setVisible(true);
  }

  public void setInvisible() {
    circle.setVisible(false);
  }

  public void remove() {
    circle.setVisible(false);
    circle.setDisable(true);
  }

  public void move(double x, double y) {
    circle.setCenterX(x);
    circle.setCenterY(y);
  }

  public void addToAnchorPane(AnchorPane anchorPane) {
    anchorPane.getChildren().add(circle);
  }

  private Circle createCircle(int radius, Paint color, Position startPosition) {
    Circle circle = new Circle(radius, color);
    circle.setCenterX(startPosition.getX());
    circle.setCenterY(startPosition.getY());
    return circle;
  }
}
