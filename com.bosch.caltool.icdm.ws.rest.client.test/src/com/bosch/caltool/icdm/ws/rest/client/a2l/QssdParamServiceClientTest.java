/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertFalse;

import java.util.Map.Entry;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.QssdParamOutput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author hnu1cob
 */
public class QssdParamServiceClientTest extends AbstractRestClientTest {

  /**
   * Maximum number of items to log
   */
  protected static final int LOG_ITEM_MAX = 5;

  /**
   * Test method for {@link QssdParamServiceClient#getQssdParams()}. Test method to get quality ( QSSD type) labels
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetQssdParams() throws ApicWebServiceException {
    QssdParamServiceClient servClient = new QssdParamServiceClient();
    QssdParamOutput output = servClient.getQssdParams();
    assertFalse("Response map should not be empty", output.getQssdParamMap().isEmpty());
    int count = 0;
    LOG.info("Logging first {} parameters : ", LOG_ITEM_MAX);
    for (Entry<String, String> param : output.getQssdParamMap().entrySet()) {
      if (count > LOG_ITEM_MAX) {
        break;
      }
      count++;
      LOG.info("  {} : {}", param.getKey(), param.getValue());
    }
  }
}
