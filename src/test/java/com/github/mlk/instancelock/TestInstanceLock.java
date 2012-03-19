package com.github.mlk.instancelock;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class TestInstanceLock {
    @Rule
    public TestName name = new TestName();
    
    @Test
    public void whenTheFirstInstanceIsStartedThenApplicationIsNotLocked() {
        InstanceLock subject = new InstanceLock(name.getMethodName());

        try {

            Assert.assertTrue(subject.onlyInstance());
        } finally {
            subject.closeInstance();
        }
    }

    @Test
    public void whenTheSecondApplicationIsStartedTheApplicationIsLocked() {
        InstanceLock firstInstance = new InstanceLock(name.getMethodName());
        InstanceLock subject = new InstanceLock(name.getMethodName());

        try {
            firstInstance.onlyInstance();

            Assert.assertFalse(subject.onlyInstance());
        } finally {
            firstInstance.closeInstance();
        }
    }

    @Test
    public void whenTheFirstApplicationIsClosedTheSecondApplicationCanStart() {
        InstanceLock firstInstance = new InstanceLock(name.getMethodName());
        InstanceLock subject = new InstanceLock(name.getMethodName());
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
        InstanceLock firstInstance = new InstanceLock(name.getMethodName(), storage);
        InstanceLock secondInstance = new InstanceLock(name.getMethodName(), new StoreApplicationStartupListener());
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
        InstanceLock firstInstance = new InstanceLock(name.getMethodName(), storage);
        InstanceLock secondInstance = new InstanceLock(name.getMethodName());
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
        InstanceLock firstInstance = new InstanceLock(name.getMethodName(), storage);
        InstanceLock secondInstance = new InstanceLock(name.getMethodName(), new StoreApplicationStartupListener());
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

