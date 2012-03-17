package com.github.mlk.instancelock;

import org.junit.Assert;
import org.junit.Test;

public class TestInstanceLock {

    @Test
    public void whenTheFirstInstanceIsStartedThenApplicationIsNotLocked() {
        InstanceLock subject = new InstanceLock("whenTheFirstInstanceIsStartedThenApplicationIsNotLocked");

        try {

            Assert.assertTrue(subject.onlyInstance());
        } finally {
            subject.closeInstance();
        }
    }

    @Test
    public void whenTheSecondApplicationIsStartedTheApplicationIsLocked() {
        InstanceLock firstInstance = new InstanceLock("whenTheSecondApplicationIsStartedTheApplicationIsLocked");
        InstanceLock subject = new InstanceLock("whenTheSecondApplicationIsStartedTheApplicationIsLocked");

        try {
            firstInstance.onlyInstance();

            Assert.assertFalse(subject.onlyInstance());
        } finally {
            firstInstance.closeInstance();
        }
    }

    @Test
    public void whenTheFirstApplicationIsClosedTheSecondApplicationCanStart() {
        InstanceLock firstInstance = new InstanceLock("whenTheFirstApplicationIsClosedTheSecondApplicationCanStart");
        InstanceLock subject = new InstanceLock("whenTheFirstApplicationIsClosedTheSecondApplicationCanStart");
        firstInstance.onlyInstance();
        firstInstance.closeInstance();
        try {

            Assert.assertTrue(subject.onlyInstance());
        } finally {
            subject.closeInstance();
        }
    }

    @Test
    public void whenTheApplicationSendsAMessageThenTheFirstMessageApplicationReceivesIt() {
        StoreApplicationStartupListener storage = new StoreApplicationStartupListener();
        InstanceLock firstInstance = new InstanceLock("whenTheApplicationSendsAMessageThenTheFirstMessageApplicationReceivesIt", storage);
        InstanceLock secondInstance = new InstanceLock("whenTheApplicationSendsAMessageThenTheFirstMessageApplicationReceivesIt", new NullApplicationStartupListener());
        try {
            firstInstance.onlyInstance();
            secondInstance.onlyInstance("commandLineArgument");
            firstInstance.forceCheck();

            Assert.assertEquals("commandLineArgument", storage.message);
        } finally {
            firstInstance.closeInstance();
            secondInstance.closeInstance();
        }
    }

    @Test
    public void whenTheApplicationDoesNotTakeAListenerThenMessageIsNotSent() {
        StoreApplicationStartupListener storage = new StoreApplicationStartupListener();
        InstanceLock firstInstance = new InstanceLock("whenTheApplicationDoesNotTakeAListenerThenMessageIsNotSent", storage);
        InstanceLock secondInstance = new InstanceLock("whenTheApplicationDoesNotTakeAListenerThenMessageIsNotSent");
        try {
            firstInstance.onlyInstance();
            secondInstance.onlyInstance("commandLineArgument");
            firstInstance.forceCheck();

            Assert.assertNull(storage.message);
        } finally {
            firstInstance.closeInstance();
            secondInstance.closeInstance();
        }
    }


    @Test
    public void whenTheApplicationSendsAMessageThenTheFirstMessageApplicationReceivesItWithinThreeSeconds() throws Exception {
        StoreApplicationStartupListener storage = new StoreApplicationStartupListener();
        InstanceLock firstInstance = new InstanceLock("whenTheApplicationSendsAMessageThenTheFirstMessageApplicationReceivesItWithinThreeSeconds", storage);
        InstanceLock secondInstance = new InstanceLock("whenTheApplicationSendsAMessageThenTheFirstMessageApplicationReceivesItWithinThreeSeconds", new NullApplicationStartupListener());
        try {
            firstInstance.onlyInstance("");
            secondInstance.onlyInstance("commandLineArgument");
            Thread.sleep(3000);

            Assert.assertEquals("commandLineArgument", storage.message);
        } finally {
            firstInstance.closeInstance();
            secondInstance.closeInstance();
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

