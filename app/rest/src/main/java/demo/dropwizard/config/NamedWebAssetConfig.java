package demo.dropwizard.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sudhiry on 9/30/18.
 */
public class NamedWebAssetConfig {
    @JsonProperty("assetContextName")
    private String assetContextName;
    @JsonProperty("webAssetConfig")
    private WebAssetConfig webAssetConfig;
    @JsonProperty("indexFile")
    private String indexFile;

    public String getAssetContextName() {
        return assetContextName;
    }

    public void setAssetContextName(String assetContextName) {
        this.assetContextName = assetContextName;
    }

    public WebAssetConfig getWebAssetConfig() {
        return webAssetConfig;
    }

    public void setWebAssetConfig(WebAssetConfig webAssetConfig) {
        this.webAssetConfig = webAssetConfig;
    }

    public String getIndexFile() {
        return indexFile;
    }

    public void setIndexFile(String indexFile) {
        this.indexFile = indexFile;
    }
}
