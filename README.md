Instance Lock
================
Prevents two instances of the same application from starting.

Usage
-----
Grab the JAR file either [manually](https://github.com/mlk/InstanceLock/downloads) or via Maven.

    <dependencies>
        <dependency>
            <artifactId>InstanceLock</artifactId>
            <groupId>com.github.mlk</groupId>
            <version>1.0</version>
        </dependency>
    </dependencies>

Then during application start up create an `[InstanceLock](https://github.com/mlk/InstanceLock/blob/master/src/main/java/com/github/mlk/instancelock/InstanceLock.java)` and check `onlyInstance()`.

    InstanceLock lock = new InstanceLock("application_name");
    if (!lock.onlyInstance()) {
        // Already running.
        System.exist(-1);
    }

If you want the second application to send data to the running application create an [`ApplicationStartupListener`](https://github.com/mlk/InstanceLock/blob/master/src/main/java/com/github/mlk/instancelock/ApplicationStartupListener.java) and pass it into the constructor of the InstanceLock. `applicationStartup(String)` will be called when a second instance of the application is started.

    InstanceLock lock = new InstanceLock("application_name",
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
