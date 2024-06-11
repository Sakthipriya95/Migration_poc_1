/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.RuleSetParamInput;
import com.bosch.caltool.icdm.model.a2l.RuleSetRulesResponse;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author AND4COB
 */
public class RuleSetParameterServiceClientTest extends AbstractRestClientTest {

  private static final Long RULE_SET_ID = 1214156465L;

  private static final Long RULE_SET_ID_FOR_A2L_PARAM_IMPORT1 = 34015642127L;

  private static final Long RULE_SET_ID_FOR_A2L_PARAM_IMPORT2 = 34015644577L;


  // PVER_NAME : MMD114A0CC1788
  private static final Long A2L_FILE_ID = 2189855001L;


  /**
   * a2l file name
   */
  private static final String A2L_FILE_NAME = "P1284_I10R2.a2l";
  /**
   * source a2l file path
   */
  private static final String SOURCE_A2L_FILE_PATH = TESTDATA_ROOT_DIR + "a2l" + File.separator + A2L_FILE_NAME;


  private static final String PARAM_NAME = "AirMod_stSwtTCmprPipRef_C";

  /**
   * Test method for {@link RuleSetParameterServiceClient#getAllParamNames(Long) }
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetAllParamNames() throws ApicWebServiceException {
    Set<String> retSet = new RuleSetParameterServiceClient().getAllParamNames(RULE_SET_ID);
    assertFalse("response not null or empty", (retSet == null) || retSet.isEmpty());
    LOG.info("Size : {}", retSet.size());
    // LOG.debug("First parameter {}", retSet.iterator().next());
    assertTrue("Parameter is available", retSet.contains(PARAM_NAME));
  }

  /**
   * Test method for {@link RuleSetParameterServiceClient#create(RuleSetParameter)},
   * {@link RuleSetParameterServiceClient#delete(RuleSetParameter) }
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testCreateDelete() throws ApicWebServiceException {
    RuleSetParameterServiceClient servClient = new RuleSetParameterServiceClient();

    RuleSetParameter obj = new RuleSetParameter();

    obj.setRuleSetId(774352165L);
    obj.setFuncId(445719365L);
    obj.setParamId(379419065L);

    // invoke create method
    RuleSetParameter createdObj = servClient.create(obj);
    assertNotNull("Created object is not null", createdObj);
    testCreatedObj(createdObj);

    // testcase to fetch RuleSetParameter using RuleSetParameter ID
    RuleSetParameter ruleSetParameter = servClient.get(createdObj.getId());

    // invoke delete method
    servClient.delete(ruleSetParameter);
  }

  /**
   * Test method for {@link RuleSetParameterServiceClient#createRuleSetParamUsingA2lFileID(RuleSetParamInput)},
   * {@link RuleSetParameterServiceClient#deleteMultiple(Set) }
   *
   * @throws ApicWebServiceException as exception
   */
  @Test
  public void testGetRuleSetParameterFromA2lFileId() throws ApicWebServiceException {
    RuleSetParameterServiceClient servClient = new RuleSetParameterServiceClient();
    RuleSetParamInput ruleSetParamInput = new RuleSetParamInput();
    ruleSetParamInput.setRuleSetId(RULE_SET_ID_FOR_A2L_PARAM_IMPORT1);
    ruleSetParamInput.setA2lFileId(A2L_FILE_ID);
    servClient.createRuleSetParamUsingA2lFileID(ruleSetParamInput);
    RuleSetRulesResponse rules = new RuleSetServiceClient().getRuleSetParamRules(RULE_SET_ID_FOR_A2L_PARAM_IMPORT1);
    assertTrue(!rules.getParamMap().isEmpty());
    // deleting the first 100 rulesetparameters of the ruleset
    Set<RuleSetParameter> ruleSetParameters = rules.getParamMap().entrySet().stream().map(Map.Entry::getValue).sorted()
        .limit(100).collect(Collectors.toSet());
    if (!ruleSetParameters.isEmpty()) {
      servClient.deleteMultiple(ruleSetParameters);
    }
  }

  /**
   * Test method for {@link RuleSetParameterServiceClient#createRuleSetParamUsingA2l(Long, String)},
   * {@link RuleSetParameterServiceClient#deleteMultiple(Set) }
   *
   * @throws ApicWebServiceException as exception
   * @throws IOException as exception
   */
  @Test
  public void testGetRuleSetParameterFromA2lFile() throws ApicWebServiceException, IOException {
    RuleSetParameterServiceClient servClient = new RuleSetParameterServiceClient();
    servClient.createRuleSetParamUsingA2l(RULE_SET_ID_FOR_A2L_PARAM_IMPORT2, SOURCE_A2L_FILE_PATH);
    RuleSetRulesResponse rules = new RuleSetServiceClient().getRuleSetParamRules(RULE_SET_ID_FOR_A2L_PARAM_IMPORT1);
    assertTrue(!rules.getParamMap().isEmpty());
    // deleting the first 100 rulesetparameters of the ruleset
    Set<RuleSetParameter> ruleSetParameters = rules.getParamMap().entrySet().stream().map(Map.Entry::getValue).sorted()
        .limit(100).collect(Collectors.toSet());
    if (!ruleSetParameters.isEmpty()) {
      servClient.deleteMultiple(ruleSetParameters);
    }
  }

  /**
   * @param createdObj
   */
  private void testCreatedObj(final RuleSetParameter createdObj) {

    assertEquals("Rule Set Id is equal", Long.valueOf(774352165), createdObj.getRuleSetId());
    assertEquals("Function Id is equal", Long.valueOf(445719365), createdObj.getFuncId());
    assertEquals("Parameter Id is equal", Long.valueOf(379419065), createdObj.getParamId());
  }

  /**
   * Test method for {@link RuleSetParameterServiceClient#createMultiple(Set)},
   * {@link RuleSetParameterServiceClient#deleteMultiple(Set) }
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testCreateDelMultiple() throws ApicWebServiceException {
    RuleSetParameterServiceClient servClient = new RuleSetParameterServiceClient();

    RuleSetParameter obj1 = new RuleSetParameter();
    obj1.setRuleSetId(775887565L);
    obj1.setFuncId(445719365L);
    obj1.setParamId(379416465L);
    obj1.setName("Test_param1");

    RuleSetParameter obj2 = new RuleSetParameter();
    obj2.setRuleSetId(984325365L);
    obj2.setFuncId(445186965L);
    obj2.setParamId(379419065L);
    obj1.setName("Test_param2");


    Set<RuleSetParameter> ruleSetParamInput = new HashSet<RuleSetParameter>();
    ruleSetParamInput.add(obj1);
    ruleSetParamInput.add(obj2);
    System.out.println(ruleSetParamInput.size());

    // invoke createMultiple method
    Set<RuleSetParameter> createdSet = servClient.createMultiple(ruleSetParamInput);
    assertFalse("Response should not be empty", createdSet.isEmpty());
    LOG.info("Size of RuleSetParameter Set after Create : {}", createdSet.size());

    boolean paramAvailable = false;
    for (RuleSetParameter ruleSetParameter : createdSet) {
      Long ruleSetId = ruleSetParameter.getRuleSetId();
      if (ruleSetId.equals(984325365L)) {
        paramAvailable = true;
        break;
      }
    }
    assertTrue("Parameter is available", paramAvailable);


    // invoke deleteMultiple method
    servClient.deleteMultiple(createdSet);
  }
}

