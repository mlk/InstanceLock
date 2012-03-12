package uk.me.michaellloydlee.applicationlock;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestApplicationLock {

    @Test
    public void whenTheFirstInstanceIsStartedThenApplicationIsNotLocked() {
        ApplicationLock subject = new ApplicationLock("whenTheFirstInstanceIsStartedThenApplicationIsNotLocked");

        try {
        
            Assert.assertTrue(subject.onlyInstance());
        } finally {
            subject.closeInstance();
        }
    }

    @Test
    public void whenTheSecondApplicationIsStartedTheApplicationIsLocked() {
        ApplicationLock firstApplication = new ApplicationLock("whenTheSecondApplicationIsStartedTheApplicationIsLocked");
        ApplicationLock subject = new ApplicationLock("whenTheSecondApplicationIsStartedTheApplicationIsLocked");

        try {
            firstApplication.onlyInstance();

            Assert.assertFalse(subject.onlyInstance());
        } finally {
            firstApplication.closeInstance();
        }
    }

    @Test
    public void whenTheFirstApplicationIsClosedTheSecondApplicationCanStart() {
        ApplicationLock firstApplication = new ApplicationLock("whenTheFirstApplicationIsClosedTheSecondApplicationCanStart");
        ApplicationLock subject = new ApplicationLock("whenTheFirstApplicationIsClosedTheSecondApplicationCanStart");
        firstApplication.onlyInstance();
        firstApplication.closeInstance();
        try {

            Assert.assertTrue(subject.onlyInstance());
        } finally {
            subject.closeInstance();
        }
    }

    @Test
    public void whenTheApplicationSendsAMessageThenTheFirstMessageApplicationReceivesIt() {
        StoreApplicationStartupListener storage = new StoreApplicationStartupListener();
        ApplicationLock firstApplication = new ApplicationLock("whenTheApplicationSendsAMessageThenTheFirstMessageApplicationReceivesIt", storage);
        ApplicationLock secondApplication = new ApplicationLock("whenTheApplicationSendsAMessageThenTheFirstMessageApplicationReceivesIt", new NullApplicationStartupListener());
        try {
            firstApplication.onlyInstance();
            secondApplication.onlyInstance("commandLineArgument");
            firstApplication.forceCheck();

            Assert.assertEquals("commandLineArgument", storage.message);
        } finally {
            firstApplication.closeInstance();
            secondApplication.closeInstance();
        }
    }

    @Test
    public void whenTheApplicationDoesNotTakeAListenerThenMessageIsNotSent() {
        StoreApplicationStartupListener storage = new StoreApplicationStartupListener();
        ApplicationLock firstApplication = new ApplicationLock("whenTheApplicationDoesNotTakeAListenerThenMessageIsNotSent", storage);
        ApplicationLock secondApplication = new ApplicationLock("whenTheApplicationDoesNotTakeAListenerThenMessageIsNotSent");
        try {
            firstApplication.onlyInstance();
            secondApplication.onlyInstance("commandLineArgument");
            firstApplication.forceCheck();

            Assert.assertNull(storage.message);
        } finally {
            firstApplication.closeInstance();
            secondApplication.closeInstance();
        }
    }


    @Test
    public void whenTheApplicationSendsAMessageThenTheFirstMessageApplicationReceivesItWithinTenSeconds() throws Exception {
        StoreApplicationStartupListener storage = new StoreApplicationStartupListener();
        ApplicationLock firstApplication = new ApplicationLock("whenTheApplicationSendsAMessageThenTheFirstMessageApplicationReceivesItWithinTenSeconds", storage);
        ApplicationLock secondApplication = new ApplicationLock("whenTheApplicationSendsAMessageThenTheFirstMessageApplicationReceivesItWithinTenSeconds", new NullApplicationStartupListener());
        try {
            firstApplication.onlyInstance("");
            secondApplication.onlyInstance("commandLineArgument");
            Thread.sleep(10000);

            Assert.assertEquals("commandLineArgument", storage.message);
        } finally {
            firstApplication.closeInstance();
            secondApplication.closeInstance();
        }
    }
}

class NullApplicationStartupListener implements ApplicationStartupListener {
    @Override
    public void applicationStartup(String message) {
    }
}

class StoreApplicationStartupListener implements ApplicationStartupListener {
    String message = null;
    @Override
    public void applicationStartup(String message) {
        this.message = message;
    }
}

