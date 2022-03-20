package com.ruppyrup.bigfun.componenttests.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
    features = "classpath:features",
    glue = {"com.ruppyrup.bigfun.componenttests.stepdefs"}
)
public class ParallelTestRunner extends AbstractTestNGCucumberTests {

  @DataProvider(parallel = true)
  @Override
  public Object[][] scenarios() {
    return super.scenarios();
  }
}
