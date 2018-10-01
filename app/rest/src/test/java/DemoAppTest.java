import demo.dropwizard.DemoApp;
import demo.dropwizard.config.DemoConfiguration;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.DropwizardTestSupport;
import io.dropwizard.testing.ResourceHelpers;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Created by sudhiry on 9/30/18.
 */
public class DemoAppTest {

    {
        System.setProperty("webdriver.chrome.driver","/Users/sudhiry/Downloads/chromedriver");
    }

    public static final DropwizardTestSupport<DemoConfiguration> SUPPORT =
            new DropwizardTestSupport<DemoConfiguration>(DemoApp.class,
                   "demo.yml");

    @BeforeClass
    public static void beforeClass() {
        SUPPORT.before();
    }
    

    @Test
    public void loginHandlerRedirectsAfterPost() throws MalformedURLException {
        System.out.println("Testing application");
        String url = "http://localhost:8080/apps/web";
        DesiredCapabilities desiredCapabilities = DesiredCapabilities.chrome();
        desiredCapabilities.setPlatform(Platform.IOS);
        desiredCapabilities.setVersion("43.0");

        WebDriver browser = new ChromeDriver();
        browser.get(url);
        int sum = 0;
        browser.get(url);
        assertEquals(browser.getTitle(),"My DreamApp");

    }
}
