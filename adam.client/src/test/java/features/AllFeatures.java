package features;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@CucumberOptions(features = "classpath:features")
@RunWith(Cucumber.class)
public class AllFeatures {
}
