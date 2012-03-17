package com.github.mlk.instancelock;

/** No op monitor. */
class NullPingMonitor implements PingMonitor {
    @Override
    public void start() { }

    @Override
    public void sendMessage(String message) { }

    @Override
    public void check() { }

    @Override
    public void stop() { }
}
