package com.ruppyrup.bigfun.controllers;

import com.jfoenix.controls.JFXButton;
import com.ruppyrup.bigfun.client.EchoClient;
import com.ruppyrup.bigfun.clientcommands.CommandFactory;
import com.ruppyrup.bigfun.common.Ball;
import com.ruppyrup.bigfun.common.Moveable;
import com.ruppyrup.bigfun.common.Player;
import com.ruppyrup.bigfun.utils.Position;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.ResourceBundle;

import static com.ruppyrup.bigfun.constants.BallConstants.BALL_RADIUS;
import static com.ruppyrup.bigfun.constants.BallConstants.PLAYER_RADIUS;
import static com.ruppyrup.bigfun.utils.CommonUtil.getRandom;
import static com.ruppyrup.bigfun.utils.CommonUtil.getRandomRGBColor;

public class ClientController implements Initializable {

  private Queue<Position> mouseEvents = new LinkedList<>();
  private EchoClient echoClient;
  private Map<String, Player> players = new HashMap<>();
  private CommandFactory clientCommandFactory;
  private Ball ball;
  private Player myPlayer;
  private int counter;

  @FXML
  private Pane ballPane;

  @FXML
  private ImageView image;

  @FXML
  private AnchorPane anchorPane;

  @FXML
  private Label hitLabel;

  @FXML
  private JFXButton connectButton;

  @FXML
  private TextField ipAddress;

  @FXML
  private TextField port;

  @FXML
  private JFXButton quitButton;

  @FXML
  void connectToServer(ActionEvent event) {
    clientCommandFactory = new CommandFactory(this);
    echoClient = new EchoClient(
        clientCommandFactory,
        ipAddress.getText(),
        Integer.parseInt(port.getText())
    );
    echoClient.start();
    echoClient.setOnSucceeded(e -> System.out.println("Succeeded :: " + echoClient.getValue()));
    myPlayer = new Player("0", createCircle(PLAYER_RADIUS, Color.RED, new Position(200, 200)));
    ball = new Ball("0", createCircle(BALL_RADIUS, Color.ORANGE, new Position(100, 100)));
  }

  @FXML
  public void disconnectFromServer() {
    echoClient.stopConnection();
    ball.setInvisible();
    myPlayer.setInvisible();
    players.forEach((id, player) -> player.setInvisible());
    players.clear();
    ball = null;
    myPlayer = null;
    hitLabel.setText("0");
  }

  @FXML
  void onMouseMoved(MouseEvent event) {
    if (myPlayer == null) return;
    makePlayerInvisibleOnMenu(myPlayer, event.getY());
    sendBallPositionEveryNEvents(event, 2);
    buttonTransition();
  }

  @FXML
  void onMousePressed(MouseEvent event) {
    mouseEvents.add(new Position(event.getX(), event.getY()));
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    hitLabel.setText("0");
  }

  private Circle createCircle(int radius, Paint color, Position startPosition) {
    Circle circle = new Circle(radius, color);
    circle.setCenterX(startPosition.getX());
    circle.setCenterY(startPosition.getY());
    anchorPane.getChildren().add(circle);
    return circle;
  }

  private void buttonTransition() {
    while (!mouseEvents.isEmpty()) {
      Position position = mouseEvents.remove();
      transitionNode(myPlayer, position.getX(), position.getY(), 150);
    }
  }

  public void addNewPlayer(String id) {
    System.out.println("Adding new button");
    String name = id.substring(15);
    String color = getRandomRGBColor();
    System.out.println("Color :: " + color);
    Circle newPlayer = createCircle(PLAYER_RADIUS,
        Color.valueOf(color),
        new Position(
            getRandom().nextDouble() * 400.0,
            getRandom().nextDouble() * 400.0));

    players.put(id, new Player(id, newPlayer));
  }

  public void moveOtherPlayer(String id, double xValue, double yValue) {
    Player playerToMove = players.get(id);

    if (playerToMove == null) {
      return; // if own button or button doesn't exist
    }

    makePlayerInvisibleOnMenu(playerToMove, yValue);
    transitionNode(playerToMove, xValue, yValue, 150);
  }

  public void moveBall(Double xValue, Double yValue) {
    transitionNode(ball, xValue, yValue, 20);
  }

  private void transitionNode(Moveable toMove, double xValue, double yValue, int duration) {
    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(duration), t -> {
      toMove.move(xValue, yValue);
    }));
    timeline.setCycleCount(1);
    timeline.play();
  }

  public void removePlayer(String id) {
    players.get(id).remove();
    players.remove(id);
  }

  public void updateHitCount(String count) {
    hitLabel.setText(count);
  }

  private void sendBallPositionEveryNEvents(MouseEvent event, int frequency) {
    if (counter++ == frequency) {
      double adjustedX = event.getX();
      double adjustedY = event.getY();
      mouseEvents.add(new Position(adjustedX, adjustedY));
      echoClient.sendMessage(adjustedX + ":" + adjustedY);
      counter = 0;
    }
  }

  private void makePlayerInvisibleOnMenu(Player player, double yPosition) {
    if (yPosition > 400) {
      player.setInvisible();
    } else {
      player.setVisible();
    }
  }
}
