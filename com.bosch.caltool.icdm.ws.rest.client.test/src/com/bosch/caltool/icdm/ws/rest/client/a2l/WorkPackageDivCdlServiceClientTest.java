/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.wp.WorkpackageDivisionCdl;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author apj4cob
 */
public class WorkPackageDivCdlServiceClientTest extends AbstractRestClientTest {

  private static final Long WP_DIV_ID = 1433834328L;

  /**
   * Test method for {@link WorkPackageDivServiceCdlClient#create(WorkpackageDivisionCdl)},
   * {@link WorkPackageDivServiceCdlClient#update(WorkpackageDivisionCdl)},
   * {@link WorkPackageDivServiceCdlClient#delete(Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateUpdateDelete() throws ApicWebServiceException {
    WorkPackageDivServiceCdlClient servClient = new WorkPackageDivServiceCdlClient();
    WorkpackageDivisionCdl cdl = new WorkpackageDivisionCdl();
    cdl.setUserId(1424293578L);
    cdl.setRegionId(1423096027L);
    cdl.setWpDivId(1433727730L);
    // create
    WorkpackageDivisionCdl createdCdl = servClient.create(cdl);
    // validate
    assertNotNull("Created Object should not be null", createdCdl);
    assertEquals("User Id is equal", Long.valueOf(1424293578), createdCdl.getUserId());
    assertEquals("Region Id is equal", Long.valueOf(1423096027), createdCdl.getRegionId());
    assertEquals("Wp Div Id is equal", Long.valueOf(1433727730), createdCdl.getWpDivId());
    assertEquals("Version is equal", Long.valueOf(1), createdCdl.getVersion());
    createdCdl.setUserId(784335367L);
    // update
    WorkpackageDivisionCdl cdlUpdated = servClient.update(createdCdl);
    assertNotNull("Created Object should not be null", cdlUpdated);
    assertEquals("User Id is equal", Long.valueOf(784335367), cdlUpdated.getUserId());
    assertEquals("Version is equal", Long.valueOf(1 + createdCdl.getVersion()), cdlUpdated.getVersion());
    // delete
    servClient.delete(cdlUpdated.getId());
  }

  /**
   * Test method for {@link WorkPackageDivServiceCdlClient#getCdlByDivId(Long)},
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetCdlByDivId() throws ApicWebServiceException {
    WorkPackageDivServiceCdlClient servClient = new WorkPackageDivServiceCdlClient();
    Set<WorkpackageDivisionCdl> retSet = servClient.getCdlByDivId(WP_DIV_ID);
    assertFalse("Response should not be null or empty ", retSet.isEmpty());
    // validate
    for (WorkpackageDivisionCdl cdl : retSet) {
      if (cdl.getId() == 1447014778L) {
        assertEquals("User Id is equal", Long.valueOf(770105916), cdl.getUserId());
        assertEquals("Region Id is equal", Long.valueOf(1423095977), cdl.getRegionId());
        assertEquals("Wp Div Id is equal", Long.valueOf(1433834328), cdl.getWpDivId());
      }
    }
  }

  /**
   * Test method for {@link WorkPackageDivServiceCdlClient#getCdlByDivId(Long)},
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetCdlByDivIdNegative() throws ApicWebServiceException {
    WorkPackageDivServiceCdlClient servClient = new WorkPackageDivServiceCdlClient();
    Set<WorkpackageDivisionCdl> retSet = servClient.getCdlByDivId(-9L);
    assertTrue("Response should be null or empty", retSet.isEmpty());
  }
}
