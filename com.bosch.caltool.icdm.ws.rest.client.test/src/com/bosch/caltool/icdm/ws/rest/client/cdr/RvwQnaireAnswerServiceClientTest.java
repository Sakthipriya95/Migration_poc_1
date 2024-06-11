/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

// TODO: Auto-generated Javadoc
/**
 * The Class RvwQnaireAnswerServiceClientTest.
 *
 * @author gge6cob
 */
public class RvwQnaireAnswerServiceClientTest extends AbstractRestClientTest {


  /**
   *
   */
  private static final String ERROR_IN_WS_CALL = "Error in WS call";
  /**
   *
   */
  private static final String OBJECT_NOT_NULL = "object not null";

  /**
   *
   */
  @Test
  public void testGetRvwQnaireAnswer() {
    LOG.info("=======================================================================================================");
    LOG.info(
        " TestGetById==============================================================================================");
    LOG.info("=======================================================================================================");
    RvwQnaireAnswerServiceClient servClient = new RvwQnaireAnswerServiceClient();
    try {
      RvwQnaireAnswer rvwQnaireAnswer = servClient.getById(2194035738l);
      assertNotNull(OBJECT_NOT_NULL, rvwQnaireAnswer);
      testOutput(rvwQnaireAnswer);
    }
    catch (ApicWebServiceException excep) {
      LOG.error(ERROR_IN_WS_CALL, excep);
      assertNull(ERROR_IN_WS_CALL, excep);
    }

  }

  /**
   * Testcase create , update and delete
   */
  @Test
  public void testCreateUpadateDelete() {
    LOG.info("=======================================================================================================");
    LOG.info(
        " TestCreate==============================================================================================");
    LOG.info("=======================================================================================================");
    RvwQnaireAnswerServiceClient servClient = new RvwQnaireAnswerServiceClient();
    try {
      RvwQnaireAnswer obj = new RvwQnaireAnswer();
      obj.setQuestionId(795090665L);
      obj.setResult("P");
      obj.setVersion(1L);
      obj.setRemark(getRunId() + " -Junit 2 Update - Create");

      // Invoke create method
      RvwQnaireAnswer createdObj = servClient.create(obj);
      assertNotNull(OBJECT_NOT_NULL, createdObj);
      testOutput(createdObj);

      createdObj.setVersion(1L);
      createdObj.setRemark(getRunId() + " -Junit 2 Update");

      // Invoke update method
      RvwQnaireAnswer updatedObj = servClient.update(createdObj);
      assertNotNull(OBJECT_NOT_NULL, updatedObj);
      testOutput(updatedObj);

      servClient.delete(updatedObj);

    }
    catch (Exception excep) {
      LOG.error(ERROR_IN_WS_CALL, excep);
      assertNull(ERROR_IN_WS_CALL, excep);
    }
  }

  /**
   * test output data.
   *
   * @param obj the obj
   */
  private void testOutput(final RvwQnaireAnswer obj) {
    assertNotNull("RvwQnaireAnswer object is not null", obj);
    assertNotNull("Result is not null", obj.getResult());
    assertNotNull("Remark is not null", obj.getRemark());
  }
}
