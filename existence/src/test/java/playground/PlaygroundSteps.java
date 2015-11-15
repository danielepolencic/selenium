package playground;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class PlaygroundSteps {
    public static final String WIDGET_URL = "WIDGET_URL";
    private WebDriver driver;
    private PageObject page;

    @Before
    public void setup() {
        driver = new FirefoxDriver();
        page = new PageObject(driver);

    }

    @After
    public void teardown() {
        driver.quit();
    }

    @Given("^I open the page$")
    public void iOpenThePage() throws Throwable {
        page.openUrl(System.getProperty(WIDGET_URL));
    }

    @Given("^I enter the year \"([^\"]*)\"$")
    public void iEnterTheYear(String year) throws Throwable {
        page.enterYear(year);
    }

    @When("^I submit the form$")
    public void iSubmitTheForm() throws Throwable {
        page.submitForm();
    }

    @Then("^I'm greeted with a success message$")
    public void iMGreetedWithASuccessMessage() throws Throwable {
        assertEquals("The year is valid", page.getSuccessMessage());
        assertFalse(page.isErrorMessageDisplayed());
    }

    @Then("^I see timeout$")
    public void iSeeTimeout() throws Throwable {
        assertTrue(page.hasTimedOut());
    }
}
