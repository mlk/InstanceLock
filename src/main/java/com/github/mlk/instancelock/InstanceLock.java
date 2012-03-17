package com.github.mlk.instancelock;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Prevents two instances of the application from starting at the same time. */
public final class InstanceLock {
    private FileChannel channel;
    private FileLock lock;
    private final File lockFile;
    private final PingMonitor pingMonitor;
    private final Logger log = Logger.getLogger(getClass().getName());

    /** Controls access to this application.
     *
     * @param applicationName A unique name for the starting application. Use the fully qualified name for "main".
     */
    public InstanceLock(final String applicationName) {
        this(applicationName, null);
    }

    /** Controls access to this application.
     *
     * @param applicationName A unique name for the starting application. Use the fully qualified name for "main".
     * @param applicationStartupListener This is informed when second instances of the application is started if
     *                                   the second instance chooses to send a message.
     */
    public InstanceLock(String applicationName, ApplicationStartupListener applicationStartupListener) {
        lockFile = createFile(applicationName, "lock");

        if (applicationStartupListener == null) {
            pingMonitor = new NullPingMonitor();
        } else {
            pingMonitor = new FilePingMonitor(createFile(applicationName, "ping"), applicationStartupListener);
        }
    }

    /** Is this the only instance of this application currently executing.
     * If it is the only instance running it takes the locks.
     *
     * @return Is this the only instance of the application running?
     */
    public boolean onlyInstance() {
        return onlyInstance(null);
    }

    /** Is this the only instance of this application currently executing.
     * If it is the only instance running it takes the locks.
     *
     * @param message If this is not the only instance of the application running then this message is sent to the other instance.
     * @return Is this the only instance of the application running?
     */
    public boolean onlyInstance(final String message) {
        closeInstance();

        boolean hasLock = tryLock();

        if(!hasLock && message != null) {
            pingMonitor.sendMessage(message);
        } else if (hasLock) {
            addShutdownHook();
            pingMonitor.start();
        }
        
        return hasLock;
    }

    /** Releases the lock this instance has and tidies up. You do not need to call this as it will be called
     * on application shutdown using Runtime.addShutdownHook(...).
     *
     * NOTE: If this is not called before the application is terminated then the system will still unlock, but
     *       a 0k file will be left in the users home directory.
     */
    public void closeInstance() {
        try {
            if (lock != null) {
                lock.release();
                lock = null;
            }
            if (channel != null) {
                channel.close();
                channel = null;
            }

            if (!lockFile.delete()) {
                 log.finest("Failed to delete lock file: " + lockFile.getName());
            }
            pingMonitor.stop();
        } catch (final IOException e) {
            log.log(Level.INFO, "Failed to unlock file.", e);
        }
    }

    /** Forces a check for new messages from secondary applications. Should only be used during testing. */
    public void forceCheck() {
        pingMonitor.check();
    }

    /** Adds a shutdown hook which releases the lock and tidies up. */
    protected void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread("com.github.instancelock.closeInstance shutdown hook") {
            public void run() {
                closeInstance();
            }
        });
    }

    private boolean tryLock() {
        try {
            channel = new RandomAccessFile(lockFile, "rw").getChannel();

            try {
                lock = channel.tryLock();
            } catch (OverlappingFileLockException e) {
                return false;
            }
            if (lock == null) {
                return false;
            }

        } catch (IOException e) {
            log.log(Level.WARNING, "Failed to lock file: " + lockFile, e);
            return false;
        }
        return true;
    }

    private static File createFile(String applicationName, String type) {
        return new File(new File(System.getProperty("user.home")), "/." + applicationName + "." + type);
    }
}