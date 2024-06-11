/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchCondition;
import com.bosch.caltool.icdm.model.cdr.PreCalibrationDataInput;
import com.bosch.caltool.icdm.model.cdr.PreCalibrationDataOutput;
import com.bosch.caltool.icdm.model.cdr.PreCalibrationDataResponse;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Class for testing the Pre calibration service client
 *
 * @author svj7cob
 */
// Task 243510
public class PreCalibrationDataServiceClientTest extends AbstractRestClientTest {

  /**
   * passing parameter ids
   */
  private static final Set<Long> PARAM_IDS = new HashSet<>(Arrays.asList(403905665L, 381201365L, 398523915L));
  // 385456065 ZFC_tiHotCham_C
  // 391621965 ZFC_qHotChamMin_C
  // 394775215 ZFC_qCldChamMin_C
  // 418897515 ZFC_numTExhCldCham_C

  /**
   * attr1 id
   */
  private static final Long ATTR_ID1 = 777768616L;
  // 36L Customer/Brand

  /**
   * value1 ids
   */
  private static final Set<Long> VALUES_ID1 = new HashSet<>(Arrays.asList(777768619L));
  // 860036820L Maserati

  /**
   * Used flag-1
   */
  private static final String USED_FLAG1 = "YES";

  /**
   * attr2 id
   */
  private static final Long ATTR_ID2 = 197L;
  // 197 ECU Generation

  /**
   * value2 ids
   */
  private static final Set<Long> VALUES_ID2 = new HashSet<>(Arrays.asList(200L));
  // 200 197 EDC 17


  /**
   * Used flag-2
   */
  private static final String USED_FLAG2 = null;


  /**
   * Test retrieval of fetching pre caldata from rvw results - one condition
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void test01() throws ApicWebServiceException {
    PreCalibrationDataInput preCalDataRvwResInput = new PreCalibrationDataInput();
    preCalDataRvwResInput.setParameterIdSet(PARAM_IDS);

    Set<PidcSearchCondition> pidcSearchConditionSet = new HashSet<>();
    // add condition
    pidcSearchConditionSet.add(getPidcSearchConditionObj(ATTR_ID1, VALUES_ID1, USED_FLAG1));
    preCalDataRvwResInput.setPidcSearchConditionSet(pidcSearchConditionSet);

    PreCalibrationDataResponse response = new PreCalibrationDataServiceClient().getPreCalData(preCalDataRvwResInput);

    assertNotNull("PreCalibrationData Response not null", response);
    testOutput(response);
  }

  /**
   * Test retrieval of fetching pre caldata from rvw results - with 2 conditions
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void test02() throws ApicWebServiceException {
    PreCalibrationDataInput preCalDataRvwResInput = new PreCalibrationDataInput();
    preCalDataRvwResInput.setParameterIdSet(PARAM_IDS);

    Set<PidcSearchCondition> pidcSearchConditionSet = new HashSet<>();
    // add first condition
    pidcSearchConditionSet.add(getPidcSearchConditionObj(ATTR_ID1, VALUES_ID1, USED_FLAG1));
    // add second condition
    pidcSearchConditionSet.add(getPidcSearchConditionObj(ATTR_ID2, VALUES_ID2, USED_FLAG2));
    preCalDataRvwResInput.setPidcSearchConditionSet(pidcSearchConditionSet);

    PreCalibrationDataResponse response = new PreCalibrationDataServiceClient().getPreCalData(preCalDataRvwResInput);

    assertNotNull("PreCalibrationData Response not null", response);
    testOutput(response);
  }

  /**
   * Gets the pidc search condition object
   *
   * @param attrId attrId
   * @param valueId valueId
   * @param usedFlag usedFlag
   * @return Pidc search condition object
   */
  private PidcSearchCondition getPidcSearchConditionObj(final Long attrId, final Set<Long> valueId,
      final String usedFlag) {
    PidcSearchCondition pidcSearchCondition = new PidcSearchCondition();
    pidcSearchCondition.setAttributeId(attrId);
    if ((valueId != null) && !valueId.isEmpty()) {
      pidcSearchCondition.setAttributeValueIds(valueId);
    }
    else {
      pidcSearchCondition.setUsedFlag(usedFlag);
    }
    return pidcSearchCondition;
  }

  /**
   * Test output
   *
   * @param reportData output object
   */
  private void testOutput(final PreCalibrationDataResponse response) {
    Map<Long, PreCalibrationDataOutput> paramPreCalDataDetails = response.getParamPreCalDataDetails();

    assertNotNull("PreCalibrationData Response param details not null", paramPreCalDataDetails);
    assertFalse("param details not empty", paramPreCalDataDetails.isEmpty());

    assertTrue("Result contains the input params", paramPreCalDataDetails.containsKey(PARAM_IDS.iterator().next()));

    LOG.info("Output :");
    for (Map.Entry<Long, PreCalibrationDataOutput> entry : paramPreCalDataDetails.entrySet()) {
      Long key = entry.getKey();
      PreCalibrationDataOutput value = entry.getValue();
      LOG.info("  Key : {}", key);
      LOG.info("    getPercentOfMostFreqCheckValue : {}", value.getPercentOfMostFreqCheckValue());
      LOG.info("    getOtherCheckValuesCount : {}", value.getOtherCheckValuesCount());
      LOG.info("    the type : {}", value.getType());
    }
  }

}
