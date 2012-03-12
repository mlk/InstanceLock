package uk.me.michaellloydlee.applicationlock;

public class NullPingFileMonitor implements IPingFileMonitor{
    @Override
    public void start() {

    }

    @Override
    public void writeFile(String message) { }

    @Override
    public void forceCheck() { }

    @Override
    public void stop() { }
}
