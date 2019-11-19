/**
 * @author Harish Vanama
 * @since 4/1/2019
 */
public enum Browser {
    FIREFOX("Firefox"),
    IE("IE"),
    CHROME("Chrome"),
    SAFARI("Safari"),
    EDGE("Edge"),
    FIREFOX_JSCOVER("Firefox_JsCover"),
    REMOTE_WEBDRIVER("Remote_WebDriver");
    String name;

    Browser(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Browser getBrowserByString(String name) {
        final Browser[] browsers = Browser.values();
        for (Browser browser : browsers) {
            if (browser.getName().equals(name)) {
                return browser;
            }
        }
        return FIREFOX;
    }
}