package com.spring.context;

/**
 * ClassName: Lifecycle
 * Description: 生命周期管理
 *
 * @Author: csx
 * @Create: 2025/11/4 - 15:26
 * @version: v1.0
 */
public interface Lifecycle {
    /**
     * 启动
     */
    void start();

    /**
     * 停止
     */
    void stop();

    /**
     * 运行状态
     * @return
     */
    boolean isRunning();
}
