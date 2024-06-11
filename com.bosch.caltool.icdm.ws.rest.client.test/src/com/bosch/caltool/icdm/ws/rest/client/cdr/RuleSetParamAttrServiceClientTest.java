/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.RuleSetParameterAttr;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author and4cob
 */
public class RuleSetParamAttrServiceClientTest extends AbstractRestClientTest {

  private static final Long RSET_ID = 774509315L;
  private static final Long ATTR_ID = 391L;
  private static final Long PARAM_ID = 436220065L;
  private static final Long RSET_PARAM_ID = 774513583L;

  /**
   * Test Method for {@link RuleSetParamAttrServiceClient#create(RuleSetParameterAttr)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreateDelete() throws ApicWebServiceException {

    RuleSetParamAttrServiceClient servClient = new RuleSetParamAttrServiceClient();

    RuleSetParameterAttr ruleSetParamAttr = new RuleSetParameterAttr();
    ruleSetParamAttr.setAttrId(ATTR_ID);
    ruleSetParamAttr.setRuleSetParamId(RSET_PARAM_ID);
    RuleSetParameterAttr createdObj = servClient.create(ruleSetParamAttr);
    assertNotNull("Created object is not null", createdObj);
    testRuleSetParamAttribute(createdObj);
    servClient.delete(createdObj);
  }

  /**
   * @param createdObj
   */
  private void testRuleSetParamAttribute(final RuleSetParameterAttr createdObj) {
    assertEquals("Attribute Id is equal", ATTR_ID, createdObj.getAttrId());
    assertEquals("Rule Set Id is equal", RSET_ID, createdObj.getRuleSetId());
    assertEquals("Parameter Id is equal", PARAM_ID, createdObj.getParamId());
    assertEquals("Rule Set Parameter Id is equal", RSET_PARAM_ID, createdObj.getRuleSetParamId());
  }


}
