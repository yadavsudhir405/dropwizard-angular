package demo.dropwizard.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * Created by sudhiry on 9/30/18.
 */
public class DemoConfiguration extends Configuration implements MultiBundleConfig {
    @JsonProperty("webconfigs")
    private List<NamedWebAssetConfig> webconfigs;

    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";

    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String name) {
        this.defaultName = name;
    }


    @Override
    public List<NamedWebAssetConfig> getBundleSpecificConfig() {
        return webconfigs;
    }
}
