/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model.test;


import org.junit.Assert;
import org.junit.Test;

import com.bosch.boot.ssd.api.model.RuleValidationOutputMessage;

/**
 * @author TUD1COB
 */
public class RuleValidationOutputMessageTest {

  /**
   * Test RuleValidationOutputMessage
   */
  @Test
  public void testRuleValidationOutputMessage() {
    // Create an instance of RuleValidationOutputMessage
    RuleValidationOutputMessage outputMessage = new RuleValidationOutputMessage();

    // Test the getters and setters for lineNo
    Integer lineNo = 10;
    outputMessage.setLineNo(lineNo);
    Assert.assertEquals(lineNo, outputMessage.getLineNo());

    // Test the getters and setters for message
    String message = "Invalid input";
    outputMessage.setMessage(message);
    Assert.assertEquals(message, outputMessage.getMessage());

    // Test the getters and setters for type
    String type = "Error";
    outputMessage.setType(type);
    Assert.assertEquals(type, outputMessage.getType());
  }
}

