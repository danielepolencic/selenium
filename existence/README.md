## Test if element exist

> TODO:
- make the widget an email validator
- remove cucumber
- ajax -> without page object first (fluent wait)

### Static elements

When you target and element in the page and that element doesn't exist, selenium
throws an exception. This is helpful because you can quickly spot typos and
errors in your code, but fails short when you want to check if an element is
present on the page.

Consider this simple widget that validates if the input is a valid year or not.
The requirment is rather simple at the moment: as long as the value is digits
only and 4 characters long, you can consider it to be a valid year.

[Widget]

The basic test scenario for this widget is:

```gerkin
Feature: Year Widget

  Background:
    Given I open the page

  Scenario: I can submit the form
    Given I enter the year "1990"
    When I submit the form
    Then I'm greeted with a success message
```

and can be easily test with the following code:

```java
public class PageObject {
    private WebDriver driver;

    public PageObject (WebDriver driver) {
        PageFactory.initElements(driver, this);
        this.driver = driver;
    }

    @FindBy(css = "form")
    private WebElement form;

    @FindBy(css = "input")
    private WebElement input;

    @FindBy(css = ".failure")
    private WebElement errorMessage;

    @FindBy(css = ".success")
    private WebElement successMessage;

    public void openUrl(String url) {
        driver.get(url);
    }

    public void enterYear (String year) {
        input.sendKeys(year);
    }

    public void submitForm () {
        form.submit();
    }

    public String getSuccessMessage () {
        return successMessage.getText();
    }
}
```

```java
Webdriver driver = new FireFoxDriver();
PageObject page = new PageObject(driver);

page.openUrl(System.getProperty(WIDGET_URL));
page.enterYear("1990");
page.submitForm();
assertEquals("The year is valid", page.getSuccessMessage());
```

or, in case you're into cucumber:

```java
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
}
```

The test above covers the acceptance criteria in full, but it doesn't prevent
the widget to display both successful and failure message. Indeed when you
submit the form twice - first unsuccessfully and secondly successfully - you can
clearly read both messages on the screen.

This is not what you would expect as a user. But you can amend the previous test
and assert on the error message not being present.

```java
public class PageObject () {
    ...

    public boolean isErrorMessageDisplayed() {
        return errorMessage.isDisplayed();
    }

}
```

```java
    assertEquals("The year is valid", page.getSuccessMessage());
    assertFalse(page.isErrorMessageDisplayed());
```

As soon as you run the test, you're greeted with a stack trace:

```
Scenario: I can submit the form  Time elapsed: 0.005 sec  <<< ERROR!
org.openqa.selenium.NoSuchElementException: Unable to locate element: {"method":"css selector","selector":".failure"}
Command duration or timeout: 7 milliseconds
```

This happens because Selenium can't find the element you need. And this is
ironically what we need to know to pass the test - that the element isn't there.

We could catch that `NoSuchElementException` exception and return a boolean to
indicate that element is not present. That would solve the problem of 1) the
exception breaking the program 2) finding the not existing element.

```java
public class PageObject {
    ...

    public boolean isErrorMessageDisplayed() {
        return isElementPresent(By.cssSelector(".failure"));
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }

}
```

While this seems like a good idea at first sight, using exceptions as flow
control has few shortcomings:

- the code is harder to read
- it's difficult to understand what should happen at first glance
- the compiler can't optimise your code

The other big drawback is time. Selenium keeps looking for your element till the
timeout elapses. At that point it throws an exceptions that it is caught by your
code and handled by the `isElementPresent` method. If your timeout is ten
seconds, your test will take __at least__ ten seconds to complete. You can test
it by yourself when you add this line just after the driver instantiation:

```
driver = new FirefoxDriver();
driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
```

When you rerun the test, it will some time for it to complete.

The alternative is not use exceptions and rely on Selenium to do the work for
you. The trick is to use `findElements` instead of `findElement`. `findElements` will return an empty list if no matching elements are found instead of throwing an exception. And if the list contains at least one element, you know there's at least an element on the page. But when the list is empty, you know for sure that the element is not present in the page.

```java
public class PageObject {

    @FindBy(css = ".failure")
    private List<WebElement> errorMessages;
    ...

    public boolean isErrorMessageDisplayed() {
         return errorMessages.size() > 0;
    }

}
```

The resulting code is simple and clean and convoy the original intent - making
sure the element doesn't exist.

But the issue with the timeout is still there. Selenium has to wait for the
implicit timeout to elapse before it can return the empty list. This is due to
the fact that the element could materialise any time, even just a fraction of
a second before the timer expires. For that very reason, Selenium will patiently
wait and finally return an empty list.

If there's something worse than no tests, that's surely a slow test. How do you
make the tast pass quicker?

There's no silver bullet in this case and every situation needs a slightly
different approach. In your case, the solution is straightforward: instead of
searching for the failure message, we could find __any__ message and make sure
there's only one at any single time. Doing so we can combine what we've learned
about `findElements` and the fact that Selenium returns immediately if there's
at least one item in the collection.

```java
public class PageObject {

    @FindBy(css = "p")
    private List<WebElement> messages;
    ...

    public boolean isErrorMessageDisplayed() {
         return messages.size() > 1;
    }

}
```

Great. The test is lighting fast while you still verified that the element
doesn't exist.

### Ajax

Some of the requirements changed and our little widget has now got server side
validation. Every time you enter a date a request is made to the server to check
if the date is valid. To do so, the developer decided to make an asyncrhonous
 (Ajax) request to the server and the error and success message appear only
 after the call is completed.

[Widget]

The old code fails because it expects a success message to appear immediately.

It would be nice if Selenium could just wait for the element to appear before
throwing any error. Indeed, you could increase the implicit time wait and the
test will simply pass:

```java
driver = new FirefoxDriver();
driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
```

Adding an implicit wait to your Selenium tests is simple enough and may seem
intuitive. However, this is a global setting and affects the entire span of the
Selenium instance. If any of your test has an element that cannot be located in
the DOM, Selenium will wait for the 10 seconds to elapse before throwing any
exception. Also, when implicit wait is more than zero, Selenium polls the DOM at
regular intervals to check the prensence of your element. The poll behavior
depends on the specific driver/browser implementation, but its usually like
every 0.5 seconds.

How could you be more selective and specific in polling the DOM?

There's a similar alternative to the implicit wait that is not global and can be
configured more granularly. It's `AjaxElementLocatorFactory`
a `PageFactory`-like object that implements polling on a page object level. In
other words, when you create your page object, instead of creating a regular
page object, you can create an `AjaxElementLocatorFactory` page object that
supports dynamic DOM elements and polling. Almost by magic your entire page
object is smart enough to recognise Javascript heavy application.

```java
public class PageObject {
    ...

    public PageObject(WebDriver driver) {
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 15), this);
        this.driver = driver;
    }

    ...
}
```

And with just a single line of code you get full ajax support for the entire
page object. Since now you can decide which page objects can be augmented with
`AjaxElementLocatorFactory`, you can easily customise the timeout on _ad-hoc
basis_.

### Longer waits

It's time to add more functionality to our widget and the latest addition is
a timeout feature. Since we now make a ajax request to validate the year, the
request may timeout after 30 seconds. We need to test that's the case.

You may be tempted to go back to your page object and change the timeout in the
`AjaxElementLocatorFactory` to 31 seconds. That's a very good idea, but it comes
with a cost: all your tests will have a timeout of 31 seconds.

If only there was a way to specify the timeout for this single test?

Explicit wait to the rescue!
You can tell Selenium to wait for a particular elements to appear by using the
_explicit wait_.

```java
public class PageObject {
    ...
    public PageObject(WebDriver driver) {
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 2), this);
        this.driver = driver;
    }

    ...

    public boolean hasTimedOut() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".timeout")));
        return element.isDisplayed();
    }
}
```

```java
Webdriver driver = new FireFoxDriver();
PageObject page = new PageObject(driver);

page.openUrl(System.getProperty(WIDGET_URL));
page.enterYear("1990");
page.submitForm();
assertTrue(page.hasTimedOut());
```

Notice how the timeout for `AjaxElementLocatorFactory` is less the timeout for
`WebDriverWait`, but Selenium still waits for the full 10 seconds before giving
up searching for the element.
