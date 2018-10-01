package demo.dropwizard.config;

/**
 * Created by sudhiry on 9/30/18.
 */
public class WebAssetConfig {
    private String webDistroDir;
    private String webURIPath;
    private boolean webDistroDirRelative;
    private String indexFile;

    public String getWebDistroDir() {
        return webDistroDir;
    }

    public void setWebDistroDir(String webDistroDir) {
        this.webDistroDir = webDistroDir;
    }

    public String getWebURIPath() {
        return webURIPath;
    }

    public void setWebURIPath(String webURIPath) {
        this.webURIPath = webURIPath;
    }

    public boolean isWebDistroDirRelative() {
        return webDistroDirRelative;
    }

    public void setWebDistroDirRelative(boolean webDistroDirRelative) {
        this.webDistroDirRelative = webDistroDirRelative;
    }

    public String getIndexFile() {
        return indexFile;
    }

    public void setIndexFile(String indexFile) {
        this.indexFile = indexFile;
    }
}
