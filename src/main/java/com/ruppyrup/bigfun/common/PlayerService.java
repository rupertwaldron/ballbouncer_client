package com.ruppyrup.bigfun.common;

import static com.ruppyrup.bigfun.constants.BallConstants.PLAYER_RADIUS;
import static com.ruppyrup.bigfun.utils.CommonUtil.getRandomRGBColor;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class PlayerService {
  private final AnchorPane anchorPane;
  private GameObject myPlayer;
  private Map<String, GameObject> players = new HashMap<>();

  public PlayerService(AnchorPane anchorPane) {
    this.anchorPane = anchorPane;
    myPlayer = new Player("0", PLAYER_RADIUS, Color.RED);
    myPlayer.addToAnchorPane(anchorPane);
  }

  public void clearPlayers() {
    players.forEach((id, player) -> player.setInvisible());
    players.clear();
  }

  public boolean isPlayerAvailable() {
    return myPlayer != null;
  }

  public GameObject getMyPlayer() {
    return myPlayer;
  }

  public void addNewPlayer(String id) {
    System.out.println("Adding new button");
    String color = getRandomRGBColor();
    System.out.println("Color :: " + color);
    GameObject player = new Player(id, PLAYER_RADIUS, Color.valueOf(color));
    player.addToAnchorPane(anchorPane);
    players.put(id, player);
  }

  public GameObject getPlayer(String id) {
    return players.get(id);
  }

  public void removePlayer(String id) {
    getPlayer(id).remove();
    players.remove(id);
  }

}
