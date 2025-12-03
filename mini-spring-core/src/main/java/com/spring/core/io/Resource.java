package com.spring.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * ClassName: Resource
 * Description:
 *
 * 资源接口 - 对应Spring的Resource接口
 * 表示可以从类路径、文件系统、URL等位置获取的资源
 *
 * @Author: csx
 * @Create: 2025/11/4 - 14:26
 * @version: v1.0
 */
public interface Resource extends InputStreamSource {

    /**
     * 资源是否存在
     */
    boolean exists();

    /**
     * 获取资源描述符
     */
    String getDescription();

    /**
     * 获取资源URL
     */
    URL getURL() throws IOException;

    /**
     * 获取资源URI
     */
    URI getURI() throws IOException;

    /**
     * 获取资源文件
     */
    File getFile() throws IOException;

    /**
     * 获取资源内容长度
     */
    long contentLength() throws IOException;

    /**
     * 获取最后修改时间
     */
    long lastModified() throws IOException;

    /**
     * 获取资源文件名
     */
    String getFilename();

    /**
     * 判断是否是文件系统资源
     */
    boolean isFile();
}
