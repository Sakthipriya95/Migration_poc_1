/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.general.DataRefreshInput;
import com.bosch.caltool.icdm.model.general.DataRefreshResult;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class DataRefreshServiceClientTest extends AbstractRestClientTest {

  private static final String MSG_REFRESH_OK = "OK";
  private static final String MSG_REFRESH_INVALID_ID = "Invalid ID -10000";
  private static final String MSG_REFRESH_INVALID_TYPE = "Invalid model type code INVALID_TYPE";

  private static final String MSG_ASSERT_RESULT_NOT_NULL = "result not null";
  private static final String MSG_ASSERT_REFRESH_STATUS = "Refresh status";

  private static final String MSG_PART_RESULTS_PRESENT = " results present";

  private static final Long PIDC_ID1 = 760420017L;
  private static final Long PIDC_ID2 = 762L;

  private static final Long PIDC_VERS_ID1 = 781387043L;

  private static final String TYPE_INVALID_TYPE = "INVALID_TYPE";

  private static final Long DATA_REFRESH_INVALID_ID = -10000L;

  /**
   * Model Types excluded from Data refresh test
   */
  private static final MODEL_TYPE[] EXCLUDED_MODEL_TYPES = { MODEL_TYPE.RVW_QNAIRE_ANS_DUMMY };

  /**
   * Test method for {@link DataRefreshServiceClient#refreshData(DataRefreshInput )}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testRefreshDataDataRefreshInput1SingleType() throws ApicWebServiceException {
    DataRefreshInput input = createInputWith(MODEL_TYPE.PIDC.getTypeCode(), PIDC_ID1, PIDC_ID2);

    DataRefreshResult result = new DataRefreshServiceClient().refreshData(input);

    assertNotNull(MSG_ASSERT_RESULT_NOT_NULL, result);
    Map<Long, String> typeResultMap = result.getRefreshStatus().get(MODEL_TYPE.PIDC.getTypeCode());
    assertNotNull(MSG_ASSERT_RESULT_NOT_NULL, typeResultMap);
    assertEquals(2 + MSG_PART_RESULTS_PRESENT, 2, typeResultMap.size());

    assertEquals(MSG_ASSERT_REFRESH_STATUS, MSG_REFRESH_OK, typeResultMap.get(PIDC_ID1));
    assertEquals(MSG_ASSERT_REFRESH_STATUS, MSG_REFRESH_OK, typeResultMap.get(PIDC_ID2));
  }

  /**
   * Test method for {@link DataRefreshServiceClient#refreshData(DataRefreshInput )}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testRefreshDataDataRefreshInput2SingleTypeInvalidMix() throws ApicWebServiceException {

    DataRefreshInput input =
        createInputWith(MODEL_TYPE.PIDC.getTypeCode(), PIDC_ID1, PIDC_ID2, DATA_REFRESH_INVALID_ID);

    DataRefreshResult result = new DataRefreshServiceClient().refreshData(input);

    assertNotNull(MSG_ASSERT_RESULT_NOT_NULL, result);

    Map<Long, String> typeResultMap = result.getRefreshStatus().get(MODEL_TYPE.PIDC.getTypeCode());
    assertNotNull(MSG_ASSERT_RESULT_NOT_NULL, typeResultMap);
    assertEquals(3 + MSG_PART_RESULTS_PRESENT, 3, typeResultMap.size());

    assertEquals(MSG_ASSERT_REFRESH_STATUS, MSG_REFRESH_OK, typeResultMap.get(PIDC_ID1));
    assertEquals(MSG_ASSERT_REFRESH_STATUS, MSG_REFRESH_OK, typeResultMap.get(PIDC_ID2));
    assertEquals(MSG_ASSERT_REFRESH_STATUS, MSG_REFRESH_INVALID_ID, typeResultMap.get(DATA_REFRESH_INVALID_ID));
  }

  /**
   * Test method for {@link DataRefreshServiceClient#refreshData(DataRefreshInput )}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testRefreshDataDataRefreshInput3MultipleType() throws ApicWebServiceException {
    DataRefreshInput input = createInputWith(MODEL_TYPE.PIDC.getTypeCode(), PIDC_ID1, PIDC_ID2);
    addToInput(input, MODEL_TYPE.PIDC_VERSION.getTypeCode(), PIDC_VERS_ID1);

    DataRefreshResult result = new DataRefreshServiceClient().refreshData(input);

    assertNotNull(MSG_ASSERT_RESULT_NOT_NULL, result);

    // Check PIDC refresh
    Map<Long, String> typeResultMap = result.getRefreshStatus().get(MODEL_TYPE.PIDC.getTypeCode());
    assertNotNull(MSG_ASSERT_RESULT_NOT_NULL, typeResultMap);
    assertEquals(2 + MSG_PART_RESULTS_PRESENT, 2, typeResultMap.size());
    assertEquals(MSG_ASSERT_REFRESH_STATUS, MSG_REFRESH_OK, typeResultMap.get(PIDC_ID1));

    // Check PIDC Version refresh
    typeResultMap = result.getRefreshStatus().get(MODEL_TYPE.PIDC_VERSION.getTypeCode());
    assertNotNull(MSG_ASSERT_RESULT_NOT_NULL, typeResultMap);
    assertEquals(1 + MSG_PART_RESULTS_PRESENT, 1, typeResultMap.size());
    assertEquals(MSG_ASSERT_REFRESH_STATUS, MSG_REFRESH_OK, typeResultMap.get(PIDC_VERS_ID1));
  }


  /**
   * Test method for {@link DataRefreshServiceClient#refreshData(DataRefreshInput )}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testRefreshDataDataRefreshInput3MultipleTypeInvalidMix() throws ApicWebServiceException {
    DataRefreshInput input = createInputWith(MODEL_TYPE.PIDC.getTypeCode(), PIDC_ID1, PIDC_ID2);
    addToInput(input, TYPE_INVALID_TYPE, PIDC_VERS_ID1);

    DataRefreshResult result = new DataRefreshServiceClient().refreshData(input);

    assertNotNull(MSG_ASSERT_RESULT_NOT_NULL, result);

    // Check PIDC refresh
    Map<Long, String> typeResultMap = result.getRefreshStatus().get(MODEL_TYPE.PIDC.getTypeCode());
    assertNotNull(MSG_ASSERT_RESULT_NOT_NULL, typeResultMap);
    assertEquals(2 + MSG_PART_RESULTS_PRESENT, 2, typeResultMap.size());
    assertEquals(MSG_ASSERT_REFRESH_STATUS, MSG_REFRESH_OK, typeResultMap.get(PIDC_ID1));

    // Check INVALID_TYPE refresh
    typeResultMap = result.getRefreshStatus().get(TYPE_INVALID_TYPE);
    assertNotNull(MSG_ASSERT_RESULT_NOT_NULL, typeResultMap);
    assertEquals(1 + MSG_PART_RESULTS_PRESENT, 1, typeResultMap.size());
    assertEquals(MSG_ASSERT_REFRESH_STATUS, MSG_REFRESH_INVALID_TYPE, typeResultMap.get(PIDC_VERS_ID1));
  }

  private DataRefreshInput createInputWith(final String type, final Long... ids) {
    DataRefreshInput input = new DataRefreshInput();
    input.getInput().put(type, new HashSet<>(Arrays.asList(ids)));
    return input;
  }

  private void addToInput(final DataRefreshInput input, final String type, final Long... ids) {
    input.getInput().put(type, new HashSet<>(Arrays.asList(ids)));
  }

  /**
   * Test method for {@link DataRefreshServiceClient#refreshData(DataRefreshInput)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testValidateAllModels() throws ApicWebServiceException {

    SortedSet<MODEL_TYPE> excludedModelTypeSet = new TreeSet<>(Arrays.asList(EXCLUDED_MODEL_TYPES));

    MODEL_TYPE[] modelTypeArr = MODEL_TYPE.values();
    DataRefreshInput input = new DataRefreshInput();

    // store only those models which are not in the excludedModelTypeSet
    Arrays.stream(modelTypeArr).filter(m -> !excludedModelTypeSet.contains(m)).map(MODEL_TYPE::getTypeCode)
        .forEach(c -> addToInput(input, c, DATA_REFRESH_INVALID_ID));

    DataRefreshResult result = new DataRefreshServiceClient().refreshData(input);

    Map<String, Map<Long, String>> allTypeResultMap = result.getRefreshStatus();

    // key - model type, value - refresh message
    Map<String, String> invalidModelConfigMap = new HashMap<>();

    for (Entry<String, Map<Long, String>> mapEntry : allTypeResultMap.entrySet()) {
      Map<Long, String> typeResultMap = result.getRefreshStatus().get(mapEntry.getKey());
      assertNotNull(MSG_ASSERT_RESULT_NOT_NULL, typeResultMap);
      assertEquals(1 + MSG_PART_RESULTS_PRESENT, 1, typeResultMap.size());

      String refreshMsg = typeResultMap.get(DATA_REFRESH_INVALID_ID);
      // if refresh message is 'Invalid ID -10000' then model has correct config, otherwise it has invalid config
      if (!MSG_REFRESH_INVALID_ID.equals(refreshMsg)) {
        invalidModelConfigMap.put(mapEntry.getKey(), refreshMsg);
      }
    }

    LOG.info("Total valid models = {}, invalid models = {}", (modelTypeArr.length - invalidModelConfigMap.size()),
        invalidModelConfigMap.size());

    if (!invalidModelConfigMap.isEmpty()) {
      StringBuilder modelWithRefreshMsg = new StringBuilder();
      LOG.error("Invalid configuration(s) found :");
      modelWithRefreshMsg.append("Invalid model(s) with refresh message type are as follows: ");

      // store all the models with invalid config and their refresh message in a single message
      for (Entry<String, String> invalidModelMapEntry : invalidModelConfigMap.entrySet()) {
        LOG.error("  " + invalidModelMapEntry.getKey() + " -> " + invalidModelMapEntry.getValue());
        modelWithRefreshMsg.append(invalidModelMapEntry.getKey()).append(" -> ").append(invalidModelMapEntry.getValue())
            .append("; ");
      }
      String message = modelWithRefreshMsg.toString();
      fail(message);
    }

  }
}
