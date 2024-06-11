/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.VcdmPstContent;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class VcdmPstContentServiceClientTest extends AbstractRestClientTest {

  private static final Long A2L_ID_01 = 1469350150L;
  private final Long Invalidid = -1234L;

  /**
   * Test method for {@link VcdmPstContentServiceClient#getVcdmPstContentsForA2l( Long) }
   *
   * @throws ApicWebServiceException error in service call
   */
  @Test
  public void testGetVcdmPstContentsForPidcA2l() throws ApicWebServiceException {
    Set<VcdmPstContent> retSet = new VcdmPstContentServiceClient().getVcdmPstContentsForA2l(A2L_ID_01);
    boolean VcdmPstContent = false;
    for (VcdmPstContent PstContent : retSet) {
      if (PstContent.getId().equals(10901197L)) {
        VcdmPstContent = true;
        testOutput(PstContent);
      }
    }
    assertTrue("VcdmPstContent is available", VcdmPstContent);
  }

  /**
   * @param content
   */
  private void testOutput(final VcdmPstContent PstContent) {
    assertEquals("VcdmVariant is equal", "-", PstContent.getVcdmVariant());
    assertEquals("Vcdm_name is equal", "P1337_M240_WLINE_VM_CY_CUSTOMER_PATCH_MM3", PstContent.getVcdmName());
    assertEquals("Firld_id is equal", Long.valueOf(20545383L), PstContent.getFileId());
    assertEquals("FileName is equal", "P1337_M240_Wline_VM_CY_customer_patch_mm3.a2l", PstContent.getFileName());
    assertEquals("PstId is equal", Long.valueOf(20679792L), PstContent.getPstId());
    assertEquals("Vcdm_revision is equal", Long.valueOf(0L), PstContent.getVcdmRevision());
    assertEquals("Vcdm_revision is equal", "A2L", PstContent.getVcdmClass());
  }

  /**
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetVcdmPstContentsForPidcA2lNegative() throws ApicWebServiceException {
    VcdmPstContentServiceClient client = new VcdmPstContentServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("A2L File with ID '" + this.Invalidid + "' not found");
    client.getVcdmPstContentsForA2l(this.Invalidid);
    fail("expected exception not thrown");
  }


}
