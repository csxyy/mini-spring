package com.spring.core.io;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

/**
 * ClassName: ClassPathResource
 * Description:
 *
 * 类路径资源实现
 * 从类加载器路径中加载资源
 *
 * @Author: csx
 * @Create: 2025/12/2 - 23:09
 * @version: v1.0
 */
@Slf4j
public class ClassPathResource implements Resource {

    private final String path;
    private final ClassLoader classLoader;
    private final Class<?> clazz;

    public ClassPathResource(String path) {
        this(path, null, null);
    }

    public ClassPathResource(String path, ClassLoader classLoader) {
        this(path, classLoader, null);
    }

    public ClassPathResource(String path, Class<?> clazz) {
        this(path, null, clazz);
    }

    public ClassPathResource(String path, ClassLoader classLoader, Class<?> clazz) {
        this.path = path;
        this.classLoader = (classLoader != null) ? classLoader : getDefaultClassLoader();
        this.clazz = clazz;
    }

    private ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Exception ex) {
            // 忽略
        }
        if (cl == null) {
            cl = ClassPathResource.class.getClassLoader();
        }
        return cl;
    }

    @Override
    public boolean exists() {
        return resolveURL() != null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        URL url = resolveURL();
        if (url == null) {
            throw new IOException("Resource not found: " + getDescription());
        }
        return url.openStream();
    }

    @Override
    public String getDescription() {
        return "class path resource [" + path + "]";
    }

    @Override
    public URL getURL() throws IOException {
        URL url = resolveURL();
        if (url == null) {
            throw new IOException("Resource not found: " + getDescription());
        }
        return url;
    }

    @Override
    public URI getURI() throws IOException {
        try {
            return getURL().toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File getFile() throws IOException {
        URL url = getURL();
        if (!"file".equals(url.getProtocol())) {
            throw new IOException("Resource cannot be resolved to absolute file path: " + url);
        }
        return new File(url.getFile());
    }

    @Override
    public long contentLength() throws IOException {
        InputStream is = getInputStream();
        try {
            long size = 0;
            byte[] buf = new byte[4096];
            int read;
            while ((read = is.read(buf)) != -1) {
                size += read;
            }
            return size;
        } finally {
            try { is.close(); } catch (IOException ex) {}
        }
    }

    @Override
    public long lastModified() throws IOException {
        if (isFile()) {
            return getFile().lastModified();
        }
        URLConnection con = getURL().openConnection();
        long lastModified = con.getLastModified();
        try {
            con.getInputStream().close();
        } catch (IOException ex) {
            // 忽略
        }
        return lastModified;
    }

    @Override
    public String getFilename() {
        int separatorIndex = path.lastIndexOf('/');
        if (separatorIndex == -1) {
            return path;
        }
        return path.substring(separatorIndex + 1);
    }

    @Override
    public boolean isFile() {
        try {
            URL url = resolveURL();
            return url != null && "file".equals(url.getProtocol());
        } catch (Exception ex) {
            return false;
        }
    }

    private URL resolveURL() {
        if (clazz != null) {
            return clazz.getResource(path);
        } else if (classLoader != null) {
            return classLoader.getResource(path);
        } else {
            return ClassLoader.getSystemResource(path);
        }
    }
}
