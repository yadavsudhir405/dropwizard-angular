package demo.dropwizard;

import demo.dropwizard.config.DemoConfiguration;
import demo.dropwizard.config.MyConfiguredBundle;
import io.dropwizard.Application;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.servlets.assets.AssetServlet;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.nio.charset.StandardCharsets;


/**
 * Created by sudhiry on 9/30/18.
 */
public class DemoApp extends Application<DemoConfiguration>  {

    public static void main(String[] args) throws Exception{
        new DemoApp().run(args);
    }
    public void initialize(Bootstrap<DemoConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(new ResourceConfigurationSourceProvider());
        bootstrap.addBundle(new MyConfiguredBundle(this.getClass()));

    }

    public void run(DemoConfiguration demoConfiguration, Environment environment) throws Exception {
        environment.servlets().addServlet("assets", new AssetServlet("../../web/dist/", "/web","index.html", StandardCharsets.UTF_8)).addMapping("/*");
        environment.jersey().packages("demo.dropwizard.rest");
    }

}
