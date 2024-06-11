/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.ssd;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.ssd.OEMRuleDescriptionInput;
import com.bosch.caltool.icdm.model.ssd.RuleIdIcdmDescriptionModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author dja7cob Test class for service to fetch work package resources
 */
public class RuleDescriptionServiceClientTest extends AbstractRestClientTest {

  private static final String ERR_MSG_INVALID_RULE_REV_IDS =
      "Rule Id & Rev Id is not available. Cannot generate OEM Description";

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.ssd.RuleDescriptionServiceClient#getRuleDescription(List)}.
   *
   * @throws ApicWebServiceException error in service
   */
  @Test
  public void testRuleOEMService() throws ApicWebServiceException {
    List<OEMRuleDescriptionInput> input = new ArrayList<>();

    OEMRuleDescriptionInput obj1 = new OEMRuleDescriptionInput();
    obj1.setRuleId("14196045");
    obj1.setRevision("0002");
    input.add(obj1);

    OEMRuleDescriptionInput obj2 = new OEMRuleDescriptionInput();
    obj2.setRuleId("14196048");
    obj2.setRevision("0003");
    input.add(obj2);

    OEMRuleDescriptionInput obj3 = new OEMRuleDescriptionInput();
    obj3.setRuleId("14392237");
    obj3.setRevision("0001");
    input.add(obj3);

    OEMRuleDescriptionInput obj4 = new OEMRuleDescriptionInput();
    obj4.setRuleId("14392236");
    obj4.setRevision("0001");
    input.add(obj4);

    Map<String, RuleIdIcdmDescriptionModel> ruleDescMap = new RuleDescriptionServiceClient().getRuleDescription(input);

    assertNotNull("response not null", ruleDescMap);
    assertFalse("response not empty", ruleDescMap.isEmpty());

    testOutput(ruleDescMap);
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.ssd.RuleDescriptionServiceClient#getRuleDescription(List)}.
   *
   * @throws ApicWebServiceException error from web service
   */
  @Test
  public void testInvalidInput() throws ApicWebServiceException {
    RuleDescriptionServiceClient service = new RuleDescriptionServiceClient();

    List<OEMRuleDescriptionInput> input = new ArrayList<>();
    OEMRuleDescriptionInput obj1 = new OEMRuleDescriptionInput();
    obj1.setRuleId("14196045");
    obj1.setRevision(null);
    input.add(obj1);

    // throws exception due to invalid input
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(ERR_MSG_INVALID_RULE_REV_IDS);

    service.getRuleDescription(input);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.ssd.RuleDescriptionServiceClient#getRuleDescription(List)}.
   *
   * @throws ApicWebServiceException error from web service
   */
  @Test
  public void testEmptyInput() throws ApicWebServiceException {
    List<OEMRuleDescriptionInput> input = new ArrayList<>();

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Parameters cannot be empty");

    // Should throw exception due to invalid input
    new RuleDescriptionServiceClient().getRuleDescription(input);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }

  /**
   * @param string
   * @param wpRes
   */
  private void testOutput(final Map<String, RuleIdIcdmDescriptionModel> ruleDescMap) {
    assertNotNull("response not null", ruleDescMap);
    assertFalse("response not empty", ruleDescMap.isEmpty());

    LOG.debug("Response size  = {}", ruleDescMap.size());
    for (RuleIdIcdmDescriptionModel model : ruleDescMap.values()) {
      testOutput("Test First Response item", model);
      LOG.debug(" {}", model);
    }

  }

  /**
   * @param string
   * @param wpRes
   */
  private void testOutput(final String string, final RuleIdIcdmDescriptionModel ruleIdDesc) {
    assertNotNull(string + ": object not null", ruleIdDesc);
    LOG.debug("{}: Rule Id not empty = {}; {}", string, ruleIdDesc.getDataDescription(), ruleIdDesc.getRuleIdWihtRev());
    assertFalse(string + ": Description not empty not empty",
        (ruleIdDesc.getDataDescription() == null) || (ruleIdDesc.getRuleIdWihtRev() == null));
  }
}
