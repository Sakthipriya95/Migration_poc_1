/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.junittestframework;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;


/**
 * @author bne4cob
 */
public class JUnitTestTest extends JUnitTest {

  /**
   * Instance to test
   */
  private final JUnitTest obj = new JUnitTest();

  /**
   * Test method for {@link com.bosch.calcomp.junittestframework.JUnitTest#logTestNameBefore()}.
   */
  @Test
  public void testLogTestNameBefore() {
    this.obj.logTestNameBefore();
    assertNotNull("Test instance created", this.obj);
  }

  /**
   * Test method for {@link com.bosch.calcomp.junittestframework.JUnitTest#logTestNameAfter()}.
   */
  @Test
  public void testLogTestNameAfter() {
    this.obj.logTestNameAfter();
    assertNotNull("Test instance created", this.obj);
  }

  /**
   * Test method for {@link com.bosch.calcomp.junittestframework.JUnitTest#getRunId()}.
   */
  @Test
  public void testGetRunId() {
    String runId = JUnitTest.getRunId();
    assertTrue("Run ID", (runId != null) && !runId.isEmpty());
  }

  /**
   * Test method for {@link com.bosch.calcomp.junittestframework.JUnitTest#TEMP_DIR }.
   */
  @Test
  public void testTempDirExists() {
    assertTrue("Temp Directory exists", new File(TEMP_DIR).exists());
  }

  /**
   * Test method for {@link com.bosch.calcomp.junittestframework.JUnitTest#TESTER_LOGGER }.
   */
  @Test
  public void testTesterLogger() {
    assertNotNull("TESTER_LOGGER created", TESTER_LOGGER);
    assertTrue("TESTER_LOGGER's log level is debug", AUT_LOGGER.isDebugEnabled());
  }

  /**
   * Test method for {@link com.bosch.calcomp.junittestframework.JUnitTest#AUT_LOGGER }.
   */
  @Test
  public void testAUTLogger() {
    assertNotNull("AUT_LOGGER created", AUT_LOGGER);
    assertTrue("AUT_LOGGER's log level is debug", AUT_LOGGER.isDebugEnabled());
  }

}
