package com.github.mlk.instancelock;

interface PingMonitor {
    void start();

    void sendMessage(String message);

    void forceCheck();

    void stop();
}
