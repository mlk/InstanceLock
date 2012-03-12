package uk.me.michaellloydlee.applicationlock;

interface IPingFileMonitor {
    void start();

    void writeFile(String message);

    void forceCheck();

    void stop();
}
