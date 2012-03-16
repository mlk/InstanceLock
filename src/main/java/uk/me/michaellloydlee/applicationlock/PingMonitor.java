package uk.me.michaellloydlee.applicationlock;

interface PingMonitor {
    void start();

    void writeFile(String message);

    void forceCheck();

    void stop();
}
