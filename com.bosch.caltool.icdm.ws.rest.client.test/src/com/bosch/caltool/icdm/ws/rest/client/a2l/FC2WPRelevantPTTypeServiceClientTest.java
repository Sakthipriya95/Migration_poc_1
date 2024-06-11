/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

//import com.bosch.caltool.icdm.model.a2l.PTType;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPRelvPTType;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author IKI1COB
 */
public class FC2WPRelevantPTTypeServiceClientTest extends AbstractRestClientTest {

  private static final long FCWP_DEF_ID = 926369915L; // FC2WP definition : FC2WP Gen2 - generic

  private static final long PT_TYPE_ID = 926286065L;// PT Type : C (Common)
  private static final long TEST_PT_TYPE_ID = 2679127277L;// PT Type : Z (PT Type for tests)

  /**
   * Test retrieval of PT types
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetRelevantPTTypes() throws ApicWebServiceException {
    FC2WPRelevantPTTypeServiceClient servClient = new FC2WPRelevantPTTypeServiceClient();
    Set<FC2WPRelvPTType> ptTypeSet = servClient.getRelevantPTTypes(FCWP_DEF_ID);
    assertNotNull("response not null", ptTypeSet);
    boolean pttypeAvailable = false;
    for (FC2WPRelvPTType type : ptTypeSet) {
      if (type.getPtTypeId().equals(PT_TYPE_ID)) {
        pttypeAvailable = true;
        testOutput("All Versions", type);
        break;
      }
    }
    assertTrue("pttypeAvailable is available", pttypeAvailable);
  }

  /**
   * @param string
   * @param type
   */
  private void testOutput(final String string, final FC2WPRelvPTType type) {
    // TODO Auto-generated method stub
    assertEquals("Def ID is equal", Long.valueOf(FCWP_DEF_ID), type.getFcwpDefId());
    assertEquals("pt type desc is equal", "Common", type.getPtTypeDesc());
    assertEquals("pt_type_name is equal", "C", type.getPtTypeName());
  }

  /**
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreateDelete() throws ApicWebServiceException {
    FC2WPRelvPTType Type = new FC2WPRelvPTType();
    Type.setFcwpDefId(FCWP_DEF_ID);
    Type.setPtTypeId(TEST_PT_TYPE_ID);

    FC2WPRelevantPTTypeServiceClient servClient = new FC2WPRelevantPTTypeServiceClient();
    FC2WPRelvPTType createType = servClient.create(Type);

    assertEquals("Fcwpdef id is equal", Long.valueOf(FCWP_DEF_ID), createType.getFcwpDefId());
    assertEquals("pttype id is equal", Long.valueOf(TEST_PT_TYPE_ID), createType.getPtTypeId());

    servClient.delete(createType);

  }


}

