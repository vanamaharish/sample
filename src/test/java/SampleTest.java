import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Harish Vanama
 * @since 11/19/2019
 */
public class SampleTest {

    @DataProvider(parallel = true)
    public static Object[][] browser() {

        Object[][] objects = new Object[3][1];
        for (int i = 0; i < 2; i++) {
            objects[i][0] = "test";
        }
        return objects;
    }

    @Test(dataProvider = "browser")
    public void sample(String a) {
        System.out.println("Browser opened");
        WebDriver firefox = DriverFactory.createDriver("Firefox");
        firefox.get("https://www.google.com");
        firefox.close();
        System.out.println("Browser closed");
    }
}
