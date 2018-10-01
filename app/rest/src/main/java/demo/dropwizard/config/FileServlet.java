package demo.dropwizard.config;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import com.google.common.net.MediaType;
import io.dropwizard.servlets.assets.ByteRange;
import io.dropwizard.servlets.assets.ResourceURL;

import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by sudhiry on 9/30/18.
 */
public class FileServlet extends HttpServlet {
    private static final long serialVersionUID = 6393345594784987908L;
    private static final CharMatcher SLASHES = CharMatcher.is('/');
    private static final MediaType DEFAULT_MEDIA_TYPE;
    private static final String DEFAULT_FILE = "/index.html";
    private final String resourcePath;
    private final String uriPath;
    @Nullable
    private final String indexFile;
    @Nullable
    private final Charset defaultCharset;

    public FileServlet(String resourcePath, String uriPath, @Nullable String indexFile, @Nullable Charset defaultCharset) {
        String trimmedPath = SLASHES.trimFrom(resourcePath);
        this.resourcePath = trimmedPath.isEmpty()?trimmedPath:trimmedPath + '/';
        String trimmedUri = SLASHES.trimTrailingFrom(uriPath);
        this.uriPath = trimmedUri.isEmpty()?"/":trimmedUri;
        this.indexFile = indexFile;
        this.defaultCharset = defaultCharset;
    }

    public URL getResourceURL() {
        return Resources.getResource(this.resourcePath);
    }

    public String getUriPath() {
        return this.uriPath;
    }

    @Nullable
    public String getIndexFile() {
        return this.indexFile;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            StringBuilder builder = new StringBuilder(req.getServletPath());
            if(req.getPathInfo() != null) {
                builder.append(req.getPathInfo());
            }

            FileServlet.CachedAsset cachedAsset = this.loadAsset(builder.toString());
            if(cachedAsset == null) {
                resp.sendError(404);
                return;
            }

            if(this.isCachedClientSide(req, cachedAsset)) {
                resp.sendError(304);
                return;
            }

            String rangeHeader = req.getHeader("Range");
            int resourceLength = cachedAsset.getResource().length;
            ImmutableList<ByteRange> ranges = ImmutableList.of();
            boolean usingRanges = false;
            String mimeTypeOfExtension;
            if(rangeHeader != null) {
                mimeTypeOfExtension = req.getHeader("If-Range");
                if(mimeTypeOfExtension == null || cachedAsset.getETag().equals(mimeTypeOfExtension)) {
                    try {
                        ranges = this.parseRangeHeader(rangeHeader, resourceLength);
                    } catch (NumberFormatException var26) {
                        resp.sendError(416);
                        return;
                    }

                    if(ranges.isEmpty()) {
                        resp.sendError(416);
                        return;
                    }

                    resp.setStatus(206);
                    usingRanges = true;
                    resp.addHeader("Content-Range", "bytes " + Joiner.on(",").join(ranges) + "/" + resourceLength);
                }
            }

            resp.setDateHeader("Last-Modified", cachedAsset.getLastModifiedTime());
            resp.setHeader("ETag", cachedAsset.getETag());
            mimeTypeOfExtension = req.getServletContext().getMimeType(req.getRequestURI());
            MediaType mediaType = DEFAULT_MEDIA_TYPE;
            if(mimeTypeOfExtension != null) {
                try {
                    mediaType = MediaType.parse(mimeTypeOfExtension);
                    if(this.defaultCharset != null && mediaType.is(MediaType.ANY_TEXT_TYPE)) {
                        mediaType = mediaType.withCharset(this.defaultCharset);
                    }
                } catch (IllegalArgumentException var25) {
                    ;
                }
            }

            if(mediaType.is(MediaType.ANY_VIDEO_TYPE) || mediaType.is(MediaType.ANY_AUDIO_TYPE) || usingRanges) {
                resp.addHeader("Accept-Ranges", "bytes");
            }

            resp.setContentType(mediaType.type() + '/' + mediaType.subtype());
            if(mediaType.charset().isPresent()) {
                resp.setCharacterEncoding(((Charset)mediaType.charset().get()).toString());
            }

            ServletOutputStream output = resp.getOutputStream();
            Throwable var12 = null;

            try {
                if(usingRanges) {
                    UnmodifiableIterator var13 = ranges.iterator();

                    while(var13.hasNext()) {
                        ByteRange range = (ByteRange)var13.next();
                        output.write(cachedAsset.getResource(), range.getStart(), range.getEnd() - range.getStart() + 1);
                    }
                } else {
                    output.write(cachedAsset.getResource());
                }
            } catch (Throwable var27) {
                var12 = var27;
                throw var27;
            } finally {
                if(output != null) {
                    if(var12 != null) {
                        try {
                            output.close();
                        } catch (Throwable var24) {
                            var12.addSuppressed(var24);
                        }
                    } else {
                        output.close();
                    }
                }

            }
        } catch (URISyntaxException | RuntimeException var29) {
            resp.sendError(404);
        }

    }

    @Nullable
    private FileServlet.CachedAsset loadAsset(String key) throws URISyntaxException, IOException {
        checkArgument(key.startsWith(this.uriPath));
        String requestedResourcePath = SLASHES.trimFrom(key.substring(this.uriPath.length()));
        String absoluteRequestedResourcePath = "/"+this.resourcePath + requestedResourcePath;
        FileInputStream fos = null;
        final byte[] file;
        File requestedResource = new File(absoluteRequestedResourcePath);
        if(requestedResource.isDirectory()){
            if(indexFile!= null){
                requestedResource = new File(absoluteRequestedResourcePath+ "/"+ indexFile);
            }else {
                requestedResource = new File(absoluteRequestedResourcePath+ DEFAULT_FILE);
            }
        }

        if(!requestedResource.exists()){
            requestedResource = new File(this.resourcePath+"/"+indexFile);
        }
        long lastModified = requestedResource.lastModified();
        if(lastModified < 1){
            lastModified = System.currentTimeMillis();
        }
        lastModified = (lastModified/ 1000) * 1000;
        try{
            fos = new FileInputStream(requestedResource);
            file = ByteStreams.toByteArray(fos);
        }finally {
            if(fos!=null) fos.close();
        }
        return new CachedAsset(file, lastModified);

    }

    protected URL getResourceUrl(String absoluteRequestedResourcePath) {
        return Resources.getResource(absoluteRequestedResourcePath);
    }

    protected byte[] readResource(URL requestedResourceURL) throws IOException {
        return Resources.toByteArray(requestedResourceURL);
    }

    private boolean isCachedClientSide(HttpServletRequest req, FileServlet.CachedAsset cachedAsset) {
        return cachedAsset.getETag().equals(req.getHeader("If-None-Match")) || req.getDateHeader("If-Modified-Since") >= cachedAsset.getLastModifiedTime();
    }

    private ImmutableList<ByteRange> parseRangeHeader(String rangeHeader, int resourceLength) {
        ImmutableList.Builder<ByteRange> builder = ImmutableList.builder();
        if(rangeHeader.contains("=")) {
            String[] parts = rangeHeader.split("=");
            if(parts.length > 1) {
                List<String> ranges = Splitter.on(",").trimResults().splitToList(parts[1]);
                Iterator var6 = ranges.iterator();

                while(var6.hasNext()) {
                    String range = (String)var6.next();
                    builder.add(ByteRange.parse(range, resourceLength));
                }
            }
        }

        return builder.build();
    }

    static {
        DEFAULT_MEDIA_TYPE = MediaType.HTML_UTF_8;
    }

    private static class CachedAsset {
        private final byte[] resource;
        private final String eTag;
        private final long lastModifiedTime;

        private CachedAsset(byte[] resource, long lastModifiedTime) {
            this.resource = resource;
            this.eTag = '"' + Hashing.murmur3_128().hashBytes(resource).toString() + '"';
            this.lastModifiedTime = lastModifiedTime;
        }

        public byte[] getResource() {
            return this.resource;
        }

        public String getETag() {
            return this.eTag;
        }

        public long getLastModifiedTime() {
            return this.lastModifiedTime;
        }
    }
}
