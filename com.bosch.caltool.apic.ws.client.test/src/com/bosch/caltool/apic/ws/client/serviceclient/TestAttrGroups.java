/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.bosch.caltool.apic.ws.client.APICStub.GroupType;
import com.bosch.caltool.apic.ws.client.APICStub.SuperGroupType;

/**
 * @author rgo7cob
 */
public class TestAttrGroups extends AbstractSoapClientTest {

  private final APICWebServiceClient stub = new APICWebServiceClient();

  /**
   * @throws Exception Exception
   */
  @Test
  public void testAttrGrps() throws Exception {
    SuperGroupType[] superGroups = this.stub.getAttrGroups();


    assertNotNull(superGroups);

    int sCount = 0;


    LOG.info("Super Groups count = {}", superGroups.length);

    for (SuperGroupType superGroupType : superGroups) {
      if (sCount > 3) {
        break;
      }
      LOG.info(superGroupType.getNameE());

      checkGroups(superGroupType);

      sCount++;
    }

  }

  /**
   * check Groups
   */
  private void checkGroups(final SuperGroupType superGroupType) {
    if (superGroupType.getGroups() == null) {
      return;
    }

    LOG.info(" Groups count = {}", superGroupType.getGroups().length);
    int gCount = 0;
    for (GroupType grType : superGroupType.getGroups()) {
      if (gCount > 3) {
        break;
      }
      LOG.info("  {}", grType.getNameE());
      gCount++;
    }

  }
}
