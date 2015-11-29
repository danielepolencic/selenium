package playground;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class PageObjectAngular {
    public PageObjectAngular(WebDriver driver) {
        PageFactory.initElements(new AngularElementLocatorFactory(driver), this);
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
