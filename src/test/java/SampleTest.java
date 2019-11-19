import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

/**
 * @author Harish Vanama
 * @since 11/19/2019
 */
public class SampleTest {

    @Test(invocationCount = 100)
    public void sample(){
        System.out.println("Browser opened");
        WebDriver firefox = DriverFactory.createDriver("Firefox");
        firefox.get("https://www.google.com");
        firefox.close();
        System.out.println("Browser closed");
    }
}
