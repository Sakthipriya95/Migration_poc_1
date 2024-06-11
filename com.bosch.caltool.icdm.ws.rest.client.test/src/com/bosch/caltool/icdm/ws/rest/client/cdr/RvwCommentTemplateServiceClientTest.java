/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.RvwCommentTemplate;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class RvwCommentTemplateServiceClientTest extends AbstractRestClientTest {

  /**
   * Test method for {@link RvwCommentTemplateServiceClient#getAll() }.
   *
   * @throws ApicWebServiceException error while invoking service
   */
  @Test
  public void testGetAll() throws ApicWebServiceException {
    RvwCommentTemplateServiceClient rvwCommentTemplateServiceClient = new RvwCommentTemplateServiceClient();
    Map<Long, RvwCommentTemplate> rvwCommentTemplateMap = rvwCommentTemplateServiceClient.getAll();
    assertTrue("RvwCommentTemplate Set is not empty", !rvwCommentTemplateMap.isEmpty());
  }

}
