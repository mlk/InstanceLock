package com.github.mlk.instancelock;

/** Monitors for notifications from the second instance of the application. */
interface PingMonitor {
    /** Starts monitoring for notifications. */
    void start();

    /** @param message Sends this message to the second application. */
    void sendMessage(String message);

    /** Forces a check for notifications. */
    void check();

    /** Stop monitoring for notifications. */
    void stop();
}
