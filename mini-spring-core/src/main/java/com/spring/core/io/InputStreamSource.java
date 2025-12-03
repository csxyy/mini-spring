package com.spring.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * ClassName: InputStreamSource
 * Description:
 *
 * @Author: csx
 * @Create: 2025/12/2 - 23:06
 * @version: v1.0
 */
@FunctionalInterface
public interface InputStreamSource {

    /**
     * 获取资源的输入流
     */
    InputStream getInputStream() throws IOException;

}
