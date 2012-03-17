package com.github.mlk.instancelock;

/** Is informed when second instance of the application is started. */
public interface ApplicationStartupListener {
    /** @param message A message from the second application. */
    void applicationStartup(String message);
}
