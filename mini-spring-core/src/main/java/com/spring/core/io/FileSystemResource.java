package com.spring.core.io;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * ClassName: FileSystemResource
 * Description:
 *
 * 文件系统资源实现
 *
 * @Author: csx
 * @Create: 2025/12/2 - 23:19
 * @version: v1.0
 */
@Slf4j
public class FileSystemResource implements Resource {

    private final File file;
    private final String path;

    public FileSystemResource(File file) {
        this.file = file;
        this.path = file.getPath();
    }

    public FileSystemResource(String path) {
        this.file = new File(path);
        this.path = path;
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public String getDescription() {
        return "file [" + file.getAbsolutePath() + "]";
    }

    @Override
    public URL getURL() throws IOException {
        return file.toURI().toURL();
    }

    @Override
    public URI getURI() throws IOException {
        return file.toURI();
    }

    @Override
    public File getFile() throws IOException {
        return file;
    }

    @Override
    public long contentLength() throws IOException {
        return file.length();
    }

    @Override
    public long lastModified() throws IOException {
        return file.lastModified();
    }

    @Override
    public String getFilename() {
        return file.getName();
    }

    @Override
    public boolean isFile() {
        return true;
    }
}
