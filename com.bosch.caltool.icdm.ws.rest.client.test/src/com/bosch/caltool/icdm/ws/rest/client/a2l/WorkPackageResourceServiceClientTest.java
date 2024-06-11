/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.wp.WPResourceDetails;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author dja7cob Test class for service to fetch work package resources
 */
public class WorkPackageResourceServiceClientTest extends AbstractRestClientTest {

  public final Long WpResId = 927896665L;

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageResourceServiceClient#update(com.bosch.caltool.icdm.model.wp.WPResourceDetails)}
   * .
   */
  //@Test
  public void testUpdate() {
    fail("Not yet implemented");
  }

  /**
   * Test method for
   * {@link com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageResourceServiceClient#create(com.bosch.caltool.icdm.model.wp.WPResourceDetails)}
   * .
   */
 //@Test
  public void testCreate() {
    fail("Not yet implemented");
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageResourceServiceClient#getAllWpRes()}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetAllWpRes() throws ApicWebServiceException {
    WorkPackageResourceServiceClient service = new WorkPackageResourceServiceClient();
    Set<WPResourceDetails> wpResources = service.getAllWpRes();
    assertFalse("Response should not be null or empty", (wpResources == null) || (wpResources.isEmpty()));
    boolean wpResourceisAvailable = false;
    for (WPResourceDetails wpres : wpResources) {
      if (wpres.getWpResId().equals(this.WpResId)) {
        wpResourceisAvailable = true;
        testOutput(wpres);
      }
    }
    assertTrue("wpResource is available", wpResourceisAvailable);
  }

  /**
   * @param wpres
   */
  private void testOutput(final WPResourceDetails wpres) {
    assertEquals("WpRespCode is equal", "AF_BCAL", wpres.getWpResCode());
  }
}
