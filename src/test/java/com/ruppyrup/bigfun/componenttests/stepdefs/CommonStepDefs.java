package com.ruppyrup.bigfun.componenttests.stepdefs;

import org.junit.After;
import org.junit.Before;

public class CommonStepDefs {

  protected TestData testData = new TestData();

  @Before
  public void setup() {
    System.out.println("Common Before");
    testData.clearData();
  }

  @After
  public void cleanUp() {
    System.out.println("Common After");
    testData.clearData();
  }

}
