package uk.me.michaellloydlee.applicationlock;

public class NullPingMonitor implements PingMonitor {
    @Override
    public void start() {

    }

    @Override
    public void sendMessage(String message) { }

    @Override
    public void forceCheck() { }

    @Override
    public void stop() { }
}
