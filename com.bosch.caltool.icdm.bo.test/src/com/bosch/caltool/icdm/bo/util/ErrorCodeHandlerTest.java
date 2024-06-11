/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bosch.caltool.icdm.bo.test.AbstractIcdmBOTest;
import com.bosch.caltool.icdm.common.exception.IcdmException;


/**
 * @author bne4cob
 */
public class ErrorCodeHandlerTest extends AbstractIcdmBOTest {

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.bo.util.ErrorCodeHandler#getErrorMessage(com.bosch.caltool.icdm.common.exception.IcdmException)}.
   */
  @Test
  public void testGetErrorMessage1() {
    IcdmException exp = new IcdmException("A2L.INCOMPLETE_VCDM_FILE_INFO", -1L);

    String msg = new ErrorCodeHandler(getServiceData()).getErrorMessage(exp);
    assertEquals("Check Error code conversion",
        "Incomplete vCDM file information in iCDM for A2L file ID : -1. Please contact iCDM hotline.", msg);
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.bo.util.ErrorCodeHandler#getErrorMessage(com.bosch.caltool.icdm.common.exception.IcdmException)}.
   */
  @Test
  public void testGetErrorMessage2() {
    IcdmException exp = new IcdmException("Exception Message");
    String msg = new ErrorCodeHandler(getServiceData()).getErrorMessage(exp);
    assertEquals("Check Error code conversion", "Exception Message", msg);
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.bo.util.ErrorCodeHandler#getErrorMessage(com.bosch.caltool.icdm.common.exception.IcdmException)}.
   */
  @Test
  public void testGetErrorMessage3() {
    Long a2lFileId = 100000000000L;
    String a2lFileIdStr = a2lFileId.toString();
    IcdmException exp = new IcdmException("A2L.INCOMPLETE_VCDM_FILE_INFO", a2lFileIdStr);

    String msg = new ErrorCodeHandler(getServiceData()).getErrorMessage(exp);
    assertEquals("Check Error code conversion",
        "Incomplete vCDM file information in iCDM for A2L file ID : 100000000000. Please contact iCDM hotline.", msg);
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.bo.util.ErrorCodeHandler#isValidErrorMessageFormat(java.lang.String)}.
   */
  @Test
  public void testIsValidErrorMessageFormat() {
    assertTrue("Valid format : GROUP_NAME.NAME", ErrorCodeHandler.isValidErrorMessageFormat("GROUP_NAME.NAME"));

    assertFalse("Not valid format 1 : GROUP_NAME", ErrorCodeHandler.isValidErrorMessageFormat("GROUP_NAME"));
    assertFalse("Not valid format 2 : Simple String", ErrorCodeHandler.isValidErrorMessageFormat("Simple String"));
    assertFalse("Not valid format 3 : null", ErrorCodeHandler.isValidErrorMessageFormat(null));
    assertFalse("Not valid format 4 : empty string", ErrorCodeHandler.isValidErrorMessageFormat(""));

  }

}
