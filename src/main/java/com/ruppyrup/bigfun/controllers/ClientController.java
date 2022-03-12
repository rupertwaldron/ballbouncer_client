package com.ruppyrup.bigfun.controllers;

import com.jfoenix.controls.JFXButton;
import com.ruppyrup.bigfun.client.EchoClient;
import com.ruppyrup.bigfun.clientcommands.CommandFactory;
import com.ruppyrup.bigfun.common.Ball;
import com.ruppyrup.bigfun.common.GameObject;
import com.ruppyrup.bigfun.common.Player;
import com.ruppyrup.bigfun.common.PlayerService;
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
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.ResourceBundle;

import static com.ruppyrup.bigfun.constants.BallConstants.BALL_RADIUS;
import static com.ruppyrup.bigfun.constants.BallConstants.PLAYER_RADIUS;
import static com.ruppyrup.bigfun.utils.CommonUtil.getRandomRGBColor;

public class ClientController implements Initializable {

  private Queue<Position> mouseEvents = new LinkedList<>();
  private EchoClient echoClient;
  private CommandFactory clientCommandFactory;
  private GameObject ball;
  private int counter = 0;
  private boolean connected = false;
  private PlayerService playerService;

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
    echoClient.start();
    echoClient.setOnSucceeded(e -> System.out.println("Succeeded :: " + echoClient.getValue()));
    connected = true;
  }

  @FXML
  public void disconnectFromServer() {
    echoClient.stopConnection();
    playerService.clearPlayers();
    ball.setInvisible();
    ball = null;
    hitLabel.setText("0");
  }

  @FXML
  void onMouseMoved(MouseEvent event) {
    if (!playerService.isPlayerAvailable() || !connected) return;
    makeObjectInvisibleOnMenu(playerService.getMyPlayer(), event.getY());
    sendBallPositionEveryNEvents(event, 2);
    buttonTransition();
  }

  @FXML
  void onMousePressed(MouseEvent event) {
    mouseEvents.add(new Position(event.getX(), event.getY()));
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    clientCommandFactory = new CommandFactory(this);
    playerService = new PlayerService(anchorPane);
    echoClient = new EchoClient(
        clientCommandFactory,
        ipAddress.getText(),
        Integer.parseInt(port.getText())
    );
    hitLabel.setText("0");
    ball = new Ball("0", BALL_RADIUS, Color.ORANGE);
    ball.addToAnchorPane(anchorPane);
  }

  public void addNewPlayer(String id) {
    playerService.addNewPlayer(id);
  }

  public void moveOtherPlayer(String id, double xValue, double yValue) {
    GameObject playerToMove = playerService.getPlayer(id);
    if (playerToMove == null) return;
    makeObjectInvisibleOnMenu(playerToMove, yValue);
    transitionNode(playerToMove, xValue, yValue, 150);
  }

  public void moveBall(Double xValue, Double yValue) {
    transitionNode(ball, xValue, yValue, 20);
  }

  private void transitionNode(GameObject toMove, double xValue, double yValue, int duration) {
    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(duration), t -> toMove.move(xValue, yValue)));
    timeline.setCycleCount(1);
    timeline.play();
  }

  public void removePlayer(String id) {
   playerService.removePlayer(id);
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

  private void makeObjectInvisibleOnMenu(GameObject gameObject, double yPosition) {
    if (yPosition > 400) {
      gameObject.setInvisible();
    } else {
      gameObject.setVisible();
    }
  }

  private void buttonTransition() {
    while (!mouseEvents.isEmpty()) {
      Position position = mouseEvents.remove();
      transitionNode(playerService.getMyPlayer(), position.getX(), position.getY(), 150);
    }
  }
}
