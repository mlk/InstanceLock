Instance Lock
================
Prevents two instances of the same application from starting.

Usage
-----
Grab the JAR file either [manually][download] or via Maven.

    <dependencies>
        <dependency>
            <artifactId>InstanceLock</artifactId>
            <groupId>com.github.mlk</groupId>
            <version>1.0</version>
        </dependency>
    </dependencies>

Then during application start up create an [`InstanceLock`][InstanceLock.api][¹][InstanceLock.java] and check `onlyInstance()`.

    InstanceLock lock = new InstanceLock("application_name");
    if (!lock.onlyInstance()) {
        // Already running.
        System.exist(-1);
    }

If you want the second application to send data to the running application create an [`ApplicationStartupListener`][ApplicationStartupListener.api][²][ApplicationStartupListener.java] and pass it into the constructor of the InstanceLock. `applicationStartup(String)` will be called when a second instance of the application is started.
Note: You need to pass a string.

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


[download]: https://github.com/mlk/InstanceLock/downloads "Download JAR, source and docs from GitHub"
[InstanceLock.api]: http://mlk.github.com/InstanceLock/apidocs/com/github/mlk/instancelock/InstanceLock.html "API docs"
[InstanceLock.java]: https://github.com/mlk/InstanceLock/blob/master/src/main/java/com/github/mlk/instancelock/InstanceLock.java "InstanceLock Source"
[ApplicationStartupListener.api]: http://mlk.github.com/InstanceLock/apidocs/com/github/mlk/instancelock/ApplicationStartupListener.html "API docs"
[ApplicationStartupListener.java]: https://github.com/mlk/InstanceLock/blob/master/src/main/java/com/github/mlk/instancelock/ApplicationStartupListener.java "ApplicationStartupListener Source"
