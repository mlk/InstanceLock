package uk.me.michaellloydlee.applicationlock;

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
    /** The file channel to be locked. */
    private FileChannel channel;
    /** The lock on the file. */
    private FileLock lock;
    /** The file to be locked. */
    private final File lockFile;
    private final PingMonitor pingMonitor;
    private final Logger log = Logger.getLogger(getClass().getName());

    public InstanceLock(final String applicationName) {
        this(applicationName, null);
    }

    public InstanceLock(String applicationName, ApplicationStartupListener applicationStartupListener) {
        lockFile = createFile(applicationName, "lock");

        if (applicationStartupListener == null) {
            pingMonitor = new NullPingMonitor();
        } else {
            pingMonitor = new FilePingMonitor(createFile(applicationName, "ping"), applicationStartupListener);
        }
    }
    
    private static File createFile(String applicationName, String type) {
        return new File(new File(System.getProperty("user.home")), "/." + applicationName + "." + type);
    }


    /**
     * Is this the only instance of this application currently executing.
     * @return should the application be allowed to start up.
     */
    public boolean onlyInstance() {
        return onlyInstance(null);
    }

    /**
     * Is this the only instance of this application currently executing.
     * @param message If the application has already been started then this message is sent to the other instance.
     * @return should the application be allowed to start up.
     */
    public boolean onlyInstance(final String message) {
        closeInstance();

        boolean hasLock = tryLock();

        if(!hasLock && message != null) {
            pingMonitor.writeFile(message);
        } else if (hasLock) {
            pingMonitor.start();
        }
        
        return hasLock;
    }

    /**
     * Unlocks the file.
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

            lockFile.delete();
            pingMonitor.stop();
        } catch (final IOException e) {
            log.log(Level.INFO, "Failed to unlock file.", e);
        }
    }

    public void forceCheck() {
        pingMonitor.forceCheck();
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

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    closeInstance();
                }
            });
        } catch (IOException e) {
            log.log(Level.WARNING, "Failed to lock file: " + lockFile, e);
            return false;
        }
        return true;
    }
}