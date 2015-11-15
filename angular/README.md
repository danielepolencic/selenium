# Testing dynamic pages

## jQuery

When you make an AJAX request with jQuery, that request is recorded into an
internal queue for future reference. When the request is completed, the same
request is also removed from the queue.

Wouldn't be nice if you could click on the button and ask Selenium to wait for
that call to finish before moving on with the rest of the test?

It turns out that you can access that queue programatically and ask jQuery if
there's any request outstanding. jQuery exposes a property named `active` on the
main object just for that.

You could instruct selenium to click on the button - which in turn triggers the
ajax request - and then wait till the variable `$.active` goes to 0.

In this example, we use the explicit wait:

```java
new WebDriverWait(driver, 10).until(new Function<WebDriver, Boolean>() {
    public Boolean apply(WebDriver webDriver) {
        Long activeAjaxRequests = (Long) ((JavascriptExecutor) webDriver).executeScript("return $.active");
        return activeAjaxRequests == 0;
    }
});
```

Webdriver will wait up to 10 seconds before givin up.

This solution works well, but it's quite verbose if it has to be repeated for
each element we want to wait for.

A more interesting approach consist of asking the browser to let you know when
the request is completed and then resume testing. jQuery has an official API for
interacting with Ajax request. Without going in too much detail, the method
you're interested in is `ajaxStop`.

> $.ajaxStop(handler)
> Register a handler to be called when all Ajax requests have completed.

In other words, this Javascript method will trigger a callback in
Javascript-land when all the outgoing requests are completed.

You could combine that method with `executeAsyncScript` to let webdriver wait till
jQuery is ready. `executeAsyncScript` is a convenient way to inject Javascript
in the page and wait. The injected Javascript receives a callback as an
argument and it has to call that same callback to let Webdriver resume
operations.

So this is the plan:

1. you save the callback provided by `executeAsyncScript`. this is necessary to
   resume testing
2. `ajaxStop` receives a callback that is called when there're no outstading
   Ajax request
3. you pass the callback from `executeAsyncScript` into `ajaxStop`, so that
   Webdriver will continue executing your tests only when jQuery told you that
   there're no Ajax calls:

```java
driver.manage().timeouts().setScriptTimeout(2, TimeUnit.SECONDS);
((JavascriptExecutor) driver).executeAsyncScript(
  "var callback = arguments[arguments.length - 1];" + // receive callback from
  webdriver
  "$(document).ajaxStop(callback)" // trigger callback when Ajax is done
);
```

The first line is necessary to wait for the script to be executed, otherwise
Selenium will throw an error simlar to this:

```
org.openqa.selenium.TimeoutException: Timed out waiting for async script result after 0ms
```

This solution is good, but it'd be even better if all elements could benefit
from the same functionality - i.e waiting for Ajax calls to be completed before
moving on.

You could refactor that into a helper, but you'd still need to call it after
every action.

Instead, you could use a page object and program the Ajax waiting behaviour
within the page itself.

A basic page object for this page looks like this:

```java
public class PageObject {
    public PageObject(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "button")
    private WebElement button;

    @FindBy(css = "li")
    private List<WebElement> posts;

    public void click() {
        button.click();
    }

    public List<WebElement> getPosts() {
        return posts;
    }
}
```

All the logic that has to do with finding elements is encapsulated and
initialised in the page object with `initElements`.

Fortunately, you can provide an `ElementLocator` that is able to locate elements
in the DOM and is Ajax aware.

```java
public class AjaxElementLocator extends DefaultElementLocator {
    private WebDriver driver;

    public AjaxElementLocator(WebDriver driver, Field field) {
        super(driver, field);
        this.driver = driver;
    }

    @Override
    public WebElement findElement() {
        waitForAjaxToComplete();
        return AjaxElementLocator.super.findElement();
    }

    @Override
    public List<WebElement> findElements() {
        waitForAjaxToComplete();
        return AjaxElementLocator.super.findElements();
    }

    private void waitForAjaxToComplete() {
        new WebDriverWait(driver, 10).until(new Function<WebDriver, Boolean>() {
            public Boolean apply(WebDriver webDriver) {
                Long activeAjaxRequests = (Long) ((JavascriptExecutor) webDriver).executeScript("return $.active");
                return activeAjaxRequests == 0;
            }
        });
    }
}
```

You need to create one more class, since `initElements` is expecting a factory of
`ElementLocator`:

```java
public class AjaxElementLocatorFactory implements ElementLocatorFactory {
    private WebDriver driver;

    public AjaxElementLocatorFactory(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public ElementLocator createLocator(Field field) {
        return new AjaxElementLocator(driver, field);
    }
}
```

It's time to test if all the hard work you put in is working:

```java
@Test
public void three() {
    driver.get(System.getProperty("url"));
    driver.manage().timeouts().setScriptTimeout(2, TimeUnit.SECONDS);
    page.click();
    assertTrue(page.getPosts().size() > 0);
}
```

And it should fail because you're still not waiting for the Ajax call to be
completed. In the page object, pass the newly create Ajax-aware page object like
this:

```java
public class PageObject {

    public PageObject(WebDriver driver) {
        PageFactory.initElements(new AjaxElementLocatorFactory(driver), this);
    }

    ...
}
```

Rerun and test and voila, test is green! And best of all, all elements annotated
by `@FindBy` are automatically jQuery Ajax aware for free.

## Angular

The same technique can be applied to Angular as well. You can tell Webdriver to
wait for angular to finish all Ajax calls before interacting with the page.

However this time you can't rely on `$.active` or `ajaxStop`, since those where
specific to jQuery.

Angular exposes a similar mechanism on the `$browser` object through the method
`notifyWhenNoOutstandingRequests`. This method accepts a callback that

- is fired immediately if there're no Ajax request
- will fire some time in the future when all requests are processed

You can write a simple test to prove the idea:

```java
@Test
public void four() {
    driver.get(System.getProperty("url"));
    driver.manage().timeouts().setScriptTimeout(3, TimeUnit.SECONDS);
    driver.findElement(By.cssSelector("button")).click();
    ((JavascriptExecutor) driver).executeAsyncScript(
            "var callback = arguments[arguments.length - 1];" +
            "var $injector = angular.element(document).injector();" +
            "var $browser = $injector.get('$browser');" +
            "$browser.notifyWhenNoOutstandingRequests(callback);"
    );
    assertTrue(driver.findElements(By.cssSelector("li")).size() > 0);
}
```

When you run the tests, Webdriver waits for the http request to complete before
moving to the assertion. The Javascript code at the core of this long wait is
the following:

```js
var callback = arguments[arguments.length - 1];
var $injector = angular.element(document).injector();
var $browser = $injector.get('$browser');
$browser.notifyWhenNoOutstandingRequests(callback);
```

This code:

- saves the `executeAsyncScript` callback in the callback variable
- retrieves the injector from Angular's Dependency Injection
- injects the `$browser` module and saves it temporarely
- register the callback for when all the request are processed

This works for a single element and can easily extended to page objects as well.

If you want all your elements to wait for Angular to process all Ajax calls
before executing more code, you can extend the `ElementLocator` class in
a similar manner as for what you did with jQuery earlier.

```java
public class AngularElementLocator extends DefaultElementLocator {
    private WebDriver driver;

    public AngularElementLocator(WebDriver driver, Field field) {
        super(driver, field);
        this.driver = driver;
    }

    @Override
    public WebElement findElement() {
        waitForAjaxToComplete();
        return AngularElementLocator.super.findElement();
    }

    @Override
    public List<WebElement> findElements() {
        waitForAjaxToComplete();
        return AngularElementLocator.super.findElements();
    }

    private void waitForAjaxToComplete() {
        ((JavascriptExecutor) driver).executeAsyncScript(
                "var callback = arguments[arguments.length - 1];" +
                        "angular.element(document).injector().get('$browser').notifyWhenNoOutstandingRequests(callback);"
        );
    }
}
```

Also this time, you need an `ElementLocatorFactory`:

```java
public class AngularElementLocatorFactory implements ElementLocatorFactory {
    private WebDriver driver;

    public AngularElementLocatorFactory(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public ElementLocator createLocator(Field field) {
        return new AngularElementLocator(driver, field);
    }
}
```

You can finally write your test, where all the elements in your page object are
Angular Ajax aware:

```java
public class PageObject {
    public PageObjectAngular(WebDriver driver) {
        PageFactory.initElements(new AngularElementLocatorFactory(driver), this);
    }

  ...
}
```

```java
@Test
public void five() {
    driver.get(System.getProperty("url"));
    driver.manage().timeouts().setScriptTimeout(2, TimeUnit.SECONDS);
    page.click();
    assertTrue(page.getPosts().size() > 0);
}
```
