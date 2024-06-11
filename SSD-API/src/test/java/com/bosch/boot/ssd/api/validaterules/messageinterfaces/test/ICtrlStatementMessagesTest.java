/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.validaterules.messageinterfaces.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.bosch.boot.ssd.api.service.ValidateRuleInvokerService;
import com.bosch.boot.ssd.api.validaterules.messagesinterfaces.ICtrlStatementMessages;
import com.bosch.checkssd.datamodel.SSDStatement;
import com.bosch.checkssd.datamodel.control.CtrlFidCheck;
import com.bosch.checkssd.datamodel.util.SSDModelUtils;

/**
 * @author TUD1COB
 */

class test implements ICtrlStatementMessages {

  /**
   * {@inheritDoc}
   */
  @Override
  public ValidateRuleInvokerService getInstance() {
    return new ValidateRuleInvokerService();
  }

}

/**
 * @author TUD1COB
 */
public class ICtrlStatementMessagesTest {

  /**
   * Test GetErrorNo
   */
  @Test
  public void testGetErrorNo() {
    test test = new test();
    SSDStatement ssdstament = new SSDStatement() {/**/};
    ssdstament.setError(10);
    assertEquals(test.getErrorNo(ssdstament), 10);
  }

  /**
   * Test GetErrorNoWithCtrlFidCheck
   */
  @Test
  public void testGetErrorNoWithCtrlFidCheck() {
    test test = new test();
    CtrlFidCheck stmt = new CtrlFidCheck(new SSDModelUtils());
    stmt.setError(100);
    test.getErrorNo(stmt);
    assertEquals(test.getErrorNo(stmt), 100);
  }
}
