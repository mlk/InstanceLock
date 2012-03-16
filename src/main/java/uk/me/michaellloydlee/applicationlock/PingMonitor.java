package uk.me.michaellloydlee.applicationlock;

interface PingMonitor {
    void start();

    void sendMessage(String message);

    void forceCheck();

    void stop();
}
