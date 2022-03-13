package com.ruppyrup.bigfun.componenttests.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;


@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "html:target/cucumber-reports.html"},
        features = "classpath:features",
        glue = {"com.ruppyrup.moviedemo.cucumber.stepdefs"}
)
public class SerialTestRunner {

}
