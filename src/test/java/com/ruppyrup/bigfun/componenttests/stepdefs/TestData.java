package com.ruppyrup.bigfun.componenttests.stepdefs;

import java.util.HashMap;
import java.util.Map;

public class TestData {

  private final Map<String, Object> scenarioData = new HashMap<>();

  public void setData(String key, Object value) {
    scenarioData.put(key, value);
  }

  public <T> T getData(String key, Class<T> clazz) {
    return clazz.cast(scenarioData.get(key));
  }

  public void clearData() {
    scenarioData.clear();
  }

}
