/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model.test;


import org.junit.Assert;
import org.junit.Test;

import com.bosch.boot.ssd.api.model.SSDRuleInputModel;

/**
 * @author TUD1COB
 */
public class SSDRuleInputModelTest {

  /**
   * Test SSDRuleInputModel
   */
  @Test
  public void testSSDRuleInputModel() {
    // Create an instance of SSDRuleInputModel
    SSDRuleInputModel ruleInputModel = new SSDRuleInputModel();

    // Test the getters and setters for ruleText
    String ruleText = "Sample rule text";
    ruleInputModel.setRuleText(ruleText);
    Assert.assertEquals(ruleText, ruleInputModel.getRuleText());
  }
}
