/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.pwdservice;


import static org.junit.Assert.assertNotNull;

import org.apache.logging.log4j.LogManager;
import org.junit.Test;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.junittestframework.JUnitTest;
import com.bosch.caltool.pwdservice.exception.PasswordNotFoundException;


/**
 * @author bne4cob
 */
public class PasswordServiceTest extends JUnitTest {

  /**
   * Test method for {@link com.bosch.caltool.pwdservice.PasswordService#getPassword(java.lang.String)}.
   *
   * @throws PasswordNotFoundException error while retrieving password
   */
  @Test
  public void testGetPassword() throws PasswordNotFoundException {
    PasswordService service = new PasswordService();
    String result = service.getPassword("ICDM.LDAP_URL");
    TESTER_LOGGER.debug("Result is {}", result);
    assertNotNull("Response from password service", result);

  }

  /**
   * Test method for {@link com.bosch.caltool.pwdservice.PasswordService#getPassword(java.lang.String)}.
   *
   * @throws PasswordNotFoundException error while retrieving password
   */
  @Test
  public void testGetPasswordInvalidKey() throws PasswordNotFoundException {
    PasswordService service = new PasswordService();

    this.thrown.expect(PasswordNotFoundException.class);
    this.thrown.expectMessage("PasswordName is not valid. Please Check");

    service.getPassword("SOME_INVALID_KEY");
  }

  /**
   * Test method for getting password from multiple thread
   *
   * @throws PasswordNotFoundException error while retrieving password
   */
  @Test
  public void testMultipleGetPassword() throws PasswordNotFoundException {
    Thread t1 = new TestThread();
    Thread t2 = new TestThread();
    t1.start();
    t2.start();
    PasswordService service = new PasswordService();
    assertNotNull("Response from password service", service.getPassword("ICDM.LDAP_URL"));
  }


}

class TestThread extends Thread {

  private static final ILoggerAdapter LOGGER = new Log4JLoggerAdapterImpl(LogManager.getLogger(TestThread.class));

  @Override
  public void run() {
    try {
      String result = new PasswordService().getPassword("ICDM.LDAP_URL");
      LOGGER.debug("call from thread: {}", result);
      Thread.sleep(1000);

    }
    catch (InterruptedException e) {
      LOGGER.warn("sleep interrupted", e);
      Thread.currentThread().interrupt();
    }
    catch (PasswordNotFoundException e) {
      LOGGER.warn("Password not found", e);
    }
  }

}

