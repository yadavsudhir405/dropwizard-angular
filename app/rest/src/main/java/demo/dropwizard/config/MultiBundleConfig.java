package demo.dropwizard.config;

import java.util.List;

/**
 * Created by sudhiry on 9/30/18.
 */
public interface MultiBundleConfig {
    List<NamedWebAssetConfig> getBundleSpecificConfig();
}
