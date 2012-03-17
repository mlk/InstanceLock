package com.github.mlk.instancelock;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

class FilePingMonitor implements PingMonitor {
    /** The file updated when a second application requests and update. */
    private final File pingFile;
    private final com.github.mlk.instancelock.ApplicationStartupListener applicationStartupListener;
    private final Logger log = Logger.getLogger(getClass().getName());
    private final Timer watcher = new Timer("InstanceLock.pingFileChecker", true);

    FilePingMonitor(File pingFile, com.github.mlk.instancelock.ApplicationStartupListener applicationStartupListener) {
        this.pingFile = pingFile;
        this.applicationStartupListener = applicationStartupListener;
    }

    @Override
    public void start() {
        watcher.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                forceCheck();
            }
        }, 2500, 2500);
    }

    @Override
    public void sendMessage(String message) {
        try {
            FileUtil.writeFile(pingFile, message);
        } catch (IOException e) {
            log.log(Level.INFO, "Unable to ping other application", e);
        }
    }


    @Override
    public void forceCheck() {
        if (pingFile.exists()) {
            String content = null;
            try {
                content = FileUtil.readFully(pingFile);
                pingFile.delete();
            } catch (final IOException ioe) {
                log.log(Level.INFO, "Failed to read ping file.");
            }
            applicationStartupListener.applicationStartup(content);
        }
    }

    @Override
    public void stop() {
        watcher.purge();
    }
}
