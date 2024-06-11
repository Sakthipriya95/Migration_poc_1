/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2LFileInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2LFiles;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class PidcA2LFilesServiceClientTest extends AbstractRestClientTest {

  /**
   * PIDC : Honda->Gasoline Engine->PC - Passenger Car->ME(D)17->HONDA_ME17956_2SV_4cyl_2.0l_PFI_FF
   */
  private static final Long PROJ_ID = 769492467L;

  /**
   * PIDC : Alfa Romeo->Gasoline Engine->PC - Passenger Car->ME(D)17->Alfa 1.8L, FamB, Gen2, US
   */
  private static final Long PROJ_ID_NOVAR = 770445817L;

  /**
   * Test method for {@link PidcA2LFilesServiceClient#getPidcA2lFilesWithResults( Long)} for pidc with variants.
   *
   * @throws ApicWebServiceException error from service
   */
  @Test
  public void testGetPidcA2lFilesWithResults() throws ApicWebServiceException {
    PidcA2LFiles ret = new PidcA2LFilesServiceClient().getPidcA2lFilesWithResults(PROJ_ID);
    assertNotNull("response not null", ret);

    assertTrue("A2L File info", CommonUtils.isNotEmpty(ret.getPidcA2LInfo()));

    List<PidcA2LFileInfo> pa2lList = ret.getPidcA2LInfo().get(773518215L);

    boolean check = false;
    for (PidcA2LFileInfo pa2l : pa2lList) {
      if ("159_HONDA_ME17956_2SV_4cyl_2.0l_PFI_FF_CR6511_internal.A2L".equals(pa2l.getFileName())) {
        assertEquals("Check PVER name", "ME17956CF516241", pa2l.getPverName());
        assertEquals("Check PVER Variant", "CR651", pa2l.getPverVariant());
        assertTrue("A2L File info - variant Id check", pa2l.getVariantIDList().contains(771006868L));
        check = true;
        break;
      }
    }

    if (!check) {
      fail("A2L file info for 159_HONDA_ME17956_2SV_4cyl_2.0l_PFI_FF_CR6511_internal.A2L not found");
    }

    assertTrue("A2L File info - variants map", CommonUtils.isNotEmpty(ret.getPidcVersMap()));
    assertEquals("A2L File info - version map", "Version 2", ret.getPidcVersMap().get(774486867L));

    assertTrue("A2L File info - variants map", CommonUtils.isNotEmpty(ret.getPidcVarsMap()));
    assertEquals("A2L File info - variant map", "2SV_CVT", ret.getPidcVarsMap().get(774487180L));

  }

  /**
   * Test method for {@link PidcA2LFilesServiceClient#getPidcA2lFilesWithResults( Long)} for PIDC without variants.
   *
   * @throws ApicWebServiceException error from service
   */
  @Test
  public void testGetPidcA2lFilesWithResultsNoVariant() throws ApicWebServiceException {
    PidcA2LFiles ret = new PidcA2LFilesServiceClient().getPidcA2lFilesWithResults(PROJ_ID_NOVAR);
    assertNotNull("response not null", ret);

    assertTrue("A2L File info", CommonUtils.isNotEmpty(ret.getPidcA2LInfo()));

    List<PidcA2LFileInfo> pa2lList = ret.getPidcA2LInfo().get(773519265L);

    boolean check = false;
    for (PidcA2LFileInfo pa2l : pa2lList) {
      if ("D17330713A3_f.A2L".equals(pa2l.getFileName())) {
        assertEquals("Check PVER name", "D173307", pa2l.getPverName());
        assertEquals("Check PVER Variant", "13A3", pa2l.getPverVariant());
        assertTrue("A2L File info - variant Id list is empty", pa2l.getVariantIDList().isEmpty());
        check = true;
        break;
      }
    }

    if (!check) {
      fail("A2L file info for D17330713A3_f.A2L not found");
    }

    assertTrue("A2L File info - variants map", CommonUtils.isNotEmpty(ret.getPidcVersMap()));
    assertEquals("A2L File info - version map", "Version 1", ret.getPidcVersMap().get(773519265L));

    assertTrue("A2L File info - variants should be empty", ret.getPidcVarsMap().isEmpty());
  }

  /**
   * Test method for {@link PidcA2LFilesServiceClient#getPidcA2lFilesWithResults( Long)}.
   *
   * @throws ApicWebServiceException error from service
   */
  @Test
  public void testGetPidcA2lFilesWithResultsInvalidId() throws ApicWebServiceException {
    PidcA2LFiles ret = new PidcA2LFilesServiceClient().getPidcA2lFilesWithResults(-10000L);

    assertNotNull("response not null", ret);

    assertTrue("A2L File info should be empty", ret.getPidcA2LInfo().isEmpty());
    assertTrue("A2L File info - versions map should be empty", ret.getPidcVersMap().isEmpty());
    assertTrue("A2L File info - variants map should be empty", ret.getPidcVarsMap().isEmpty());
  }

}
