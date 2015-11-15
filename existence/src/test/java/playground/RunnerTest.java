package playground;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

import static cucumber.api.SnippetType.CAMELCASE;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources", format = {"html:target/cucumber", "json:target/cucumber.json"}, snippets = CAMELCASE)
public class RunnerTest {
}