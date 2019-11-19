import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.events.EventFiringWebDriver;

/**
 * Created by ArjunSahasranam on 2/12/15.
 */
public class DriverFactory {

    private static final String DOWNLOAD_FOLDER = "browser.download.folderList";
    private static final String DOWNLOAD_DIRECTORY = "browser.download.dir";
    private static final String NEVER_ASK_OPEN_FILE = "browser.helperApps.neverAsk.openFile";
    private static final String NEVER_ASK_SAVE_TO_DISK = "browser.helperApps.neverAsk.saveToDisk";
    private static final String TEST_TYPE = "--test-type";
    private static final String DISABLE_POPUP_BLOCKING = "disable-popup-blocking";
    private static final String SEPARATOR = ",";
    private static final String IGNORE_UNRESPONSIVE_SCRIPT_ERROR = "dom.max_chrome_script_run_time";
    private static final String IGNORE_UNRESPONSIVE_SCRIPT = "dom.max_script_run_time";
    private static final String PDFJS_DISABLED = "pdfjs.disabled";
    private static final String WEBSOCKET_URLS = "wss://echo.websocket.org/, wss://websocket-demos.wavemakeronline.com/, ws://echo.websocket.org/";

    public static synchronized WebDriver createDriver(String browser) {
        WebDriver webDriver = null;
        final Browser browserType = Browser.getBrowserByString(browser);
        switch (browserType) {
            case FIREFOX:
            case FIREFOX_JSCOVER:
                System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,
                        "target" + File.separator + "browser.log");
                System.setProperty("webdriver.http.factory", "apache");
                webDriver = new FirefoxDriver(getFirefoxOptions(browserType));
                break;
            case IE:
                webDriver = new InternetExplorerDriver(getIEOptions());
                break;
            case CHROME:
                System.setProperty("webdriver.http.factory", "apache");
                webDriver = new ChromeDriver(getChromeOptions());
                break;
            case SAFARI:
                webDriver = new SafariDriver(getSafariOptions());
                break;
            case EDGE:
                webDriver = new EdgeDriver(getDesiredCapabilties(Browser.EDGE));
                break;
            case REMOTE_WEBDRIVER:
                //TODO: To run tests in other machines remotely. Added this for running tests using safari browser in mac machine
                //TODO: selenium standalone server should be up where tests need to run. Replace host with machine ip
                try {
                    webDriver = new RemoteWebDriver(new URL("http://<host>:4444/wd/hub"), DesiredCapabilities.firefox());
                } catch (MalformedURLException e) {
                }
                break;

        }
        webDriver.manage().window().maximize();
        return new EventFiringWebDriver(webDriver);
    }

    private static DesiredCapabilities getDesiredCapabilties(final Browser browser) {
        DesiredCapabilities capabilities = null;
        switch (browser) {
            case FIREFOX:
                capabilities = DesiredCapabilities.firefox();
                capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
                capabilities.setCapability(FirefoxDriver.PROFILE, getFirefoxProfile());
                break;
            case IE:
                capabilities = DesiredCapabilities.internetExplorer();
                capabilities
                        .setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                break;
            case CHROME:
                capabilities = DesiredCapabilities.chrome();
                capabilities.setCapability(ChromeOptions.CAPABILITY, getChromeOptions());
                capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
                break;
            case SAFARI:
                SafariOptions safariOptions = new SafariOptions();
                capabilities = DesiredCapabilities.safari();
                capabilities.setJavascriptEnabled(false);
                capabilities.setCapability(SafariOptions.CAPABILITY, safariOptions);
                capabilities.setCapability(DISABLE_POPUP_BLOCKING, true);
                capabilities.setCapability("browserstack.safari.enablePopups", "false");
                capabilities.setCapability(DOWNLOAD_FOLDER, 2);
                break;
            case EDGE:
                capabilities = DesiredCapabilities.edge();
                capabilities.setCapability("pageLoadStrategy", "eager");
                break;
            case FIREFOX_JSCOVER:
                capabilities = DesiredCapabilities.firefox();
                capabilities.setCapability(FirefoxDriver.PROFILE, getFirefoxJsCoverProfile());
                capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
                break;

        }
        return capabilities;
    }

    private static Object getFirefoxProfile() {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference(DOWNLOAD_FOLDER, 2);
        profile.setPreference(PDFJS_DISABLED, true);
        profile.setPreference(IGNORE_UNRESPONSIVE_SCRIPT_ERROR, 0);
        profile.setPreference(IGNORE_UNRESPONSIVE_SCRIPT, 0);
        profile.setPreference("network.proxy.no_proxies_on", WEBSOCKET_URLS);
        profile.setAcceptUntrustedCertificates(true);
        profile.setAssumeUntrustedCertificateIssuer(true);
        return profile;
    }

    private static Object getFirefoxJsCoverProfile() {
        FirefoxProfile profile = new FirefoxProfile();
        org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
        // set the proxy location
        proxy.setHttpProxy("localhost:3128");
        // set Firefox's proxy to "Manual"
        profile.setPreference("network.proxy.type", 1);
        // set proxy domain host
        profile.setPreference("network.proxy.http", "localhost");
        // set proxy's port number
        profile.setPreference("network.proxy.http_port", 3128);
        // increase the javascript time out since code coverage is really intensive and slow
        profile.setPreference(IGNORE_UNRESPONSIVE_SCRIPT, 30000);
        // don't run coverage on scripts from the following networks:
        profile.setPreference("network.proxy.no_proxies_on",
                "phonegap, googleapis, adadvisor.net, intuit.com, doubleclick.net, doubleclick.com, google.com, webengage.com, demdex.net");
        profile.setPreference(DOWNLOAD_FOLDER, 2);
        profile.setPreference(IGNORE_UNRESPONSIVE_SCRIPT_ERROR, 0);
        profile.setPreference(IGNORE_UNRESPONSIVE_SCRIPT, 0);
        profile.setAcceptUntrustedCertificates(true);
        profile.setAssumeUntrustedCertificateIssuer(true);
        return profile;
    }

    private static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_settings.popups", 0);
        options.setExperimentalOption("prefs", prefs);
        options.addArguments(TEST_TYPE);
        options.addArguments(DISABLE_POPUP_BLOCKING);
        options.addArguments("--start-maximized");
        options.addArguments("--enable-automation");
        options.addArguments("--disable-save-password-bubble");
        LoggingPreferences perLogs = new LoggingPreferences();
        perLogs.enable(LogType.BROWSER, Level.ALL);
        options.setCapability(CapabilityType.LOGGING_PREFS, perLogs);
        return options;
    }

    private static SafariOptions getSafariOptions() {
        return SafariOptions.fromCapabilities(getDesiredCapabilties(Browser.SAFARI));
    }

    private static InternetExplorerOptions getIEOptions() {
        return new InternetExplorerOptions(getDesiredCapabilties(Browser.IE));
    }

    private static FirefoxOptions getFirefoxOptions(final Browser browser) {
        FirefoxOptions firefoxOptions = new FirefoxOptions(getDesiredCapabilties(browser));

        return firefoxOptions;
    }


}
