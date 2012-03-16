Instance Lock
================
Prevents two instances of the same application from starting.

Usage
-----

    ApplicationLock lock = new ApplicationLock("application_name");
    if (!lock.onlyInstance()) {
        // Already running.
        System.exist(-1);
    }

If you want the second application to send data to the running application:

    ApplicationLock lock = new ApplicationLock("application_name",
        new ApplicationStartupListener() {
            @Override
            public void applicationStartup(String message) {
                System.out.println(message);
            }
        });

    if (!lock.onlyInstance("message")) {
        // Already running.
        System.exist(-1);
    }
