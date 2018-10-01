package demo.dropwizard.config;

import com.google.common.base.Charsets;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.Optional;

/**
 * Created by sudhiry on 9/30/18.
 */
public class MyConfiguredBundle<T extends MultiBundleConfig> implements ConfiguredBundle<T> {

    private Class appClass;

    public MyConfiguredBundle(Class appClass) {
        this.appClass = appClass;
    }

    public void run(T myWebConfig, Environment environment) throws Exception {
        for(NamedWebAssetConfig config : myWebConfig.getBundleSpecificConfig()){
            runBundle(config, environment);
        }
    }

    private void runBundle(NamedWebAssetConfig config, Environment environment) {
        final String assetContextName = config.getAssetContextName();
        final WebAssetConfig webAssetConfig = config.getWebAssetConfig();
        String distroDir = webAssetConfig.getWebDistroDir();
        String uriPath = webAssetConfig.getWebURIPath();
        if(webAssetConfig.isWebDistroDirRelative()){
            String fullPath = Optional.ofNullable(appClass.getClassLoader().getResource(appClass.getName().replace(".","/")+".class")).map(URL::getFile).orElse(null);
            if(fullPath!= null && fullPath.contains("test-classes")){
                fullPath = fullPath.substring(0, fullPath.indexOf("/target/test-classes"));
            }else if(fullPath != null && fullPath.contains("/target/classes/")){
                fullPath = fullPath.substring(0, fullPath.indexOf("/target/classes/"));
            }else{
                throw new RuntimeException("Unable to deduce the Path");
            }
            distroDir = fullPath+"/"+ distroDir;

        }

        if(!distroDir.startsWith("/")){
            distroDir = System.getProperty("app.home.dir") + File.pathSeparator + distroDir;
        }
        if(!distroDir.endsWith("/")){
            distroDir = distroDir+ "/";
        }
        if(!new File(distroDir).exists()){
            throw new RuntimeException("Distro Dir doesn't exist "+ distroDir);
        }

        final String normalizedUriPath = removeProcessdingAndTrailingSlashes(uriPath);
        final String fullUriPath = "/"+removeProcessdingAndTrailingSlashes(assetContextName) + ((StringUtils.isEmpty(normalizedUriPath)) ? "/" : ("/"+ normalizedUriPath+"/"));
        environment.servlets().addServlet(assetContextName, new FileServlet(distroDir, fullUriPath, webAssetConfig.getIndexFile(), Charsets.UTF_8)).addMapping(fullUriPath+"*");
    }

    private String removeProcessdingAndTrailingSlashes(String uriPath) {
        String result = null ==uriPath ? "" : uriPath.trim();
        if(result.startsWith("/")){
            result = result.substring(1, result.length());
        }
        if(result.endsWith("/")){
            result = result.substring(0, result.length()-1);
        }
        return result;
    }

    public void initialize(Bootstrap<?> bootstrap) {

    }
}
