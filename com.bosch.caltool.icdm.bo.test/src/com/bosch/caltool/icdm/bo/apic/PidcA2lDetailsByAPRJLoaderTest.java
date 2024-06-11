/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.bo.test.AbstractIcdmBOTest;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.PidcA2lDetails;


/**
 * @author bne4cob
 */
public class PidcA2lDetailsByAPRJLoaderTest extends AbstractIcdmBOTest {

  private final static String APRJ_NAME = "X_Test_Hz_AUDI_1788";
  private final static String VCDM_VARIANT_NAME = "0436_17_L538_4AT_ED_AJ200_DM_TN4D";
  private final static Long VCDM_A2LFILE_ID = 20504831L;

  /**
   * Test method for
   * {@link PidcA2lDetailsByAPRJLoader#getPidcDetails(java.lang.String, java.lang.String, java.lang.Long)}.
   *
   * @throws IcdmException error from loader
   */
  @Test
  public void testGetPidcDetails() throws IcdmException {
    PidcA2lDetailsByAPRJLoader loader = new PidcA2lDetailsByAPRJLoader(getServiceData());
    Map<Long, PidcA2lDetails> retMap = loader.getPidcDetails(APRJ_NAME, VCDM_VARIANT_NAME, VCDM_A2LFILE_ID);
    assertFalse("Response should not be null or empty", (retMap == null) || retMap.isEmpty());
    PidcA2lDetails pidcA2lDetails = retMap.get(2516883379L);
    testPidcA2lDetails(pidcA2lDetails);

  }

  /**
   * @param pidcA2lDetails
   */
  private void testPidcA2lDetails(final PidcA2lDetails pidcA2lDetails) {
    assertEquals("Pidc Version Id is equal", Long.valueOf(2376682331L), pidcA2lDetails.getPidcVersionId());
    assertEquals("Pidc Version Name is equal", "test_Aniket (Version 1)", pidcA2lDetails.getPidcVersionName());
    assertEquals("SDOM Pver Name is equal", "MMD114A0CC1788", pidcA2lDetails.getPverName());
    assertEquals("Pver Variant Name is equal", "MC00", pidcA2lDetails.getPverVariant());
    assertEquals("Pidc Variant Id is equal", Long.valueOf(2516883379L), pidcA2lDetails.getPidcVariantId());
    assertEquals("Pidc Variant Name is equal", "0436_17_L538_4AT_ED_AJ200_DM_TN4D",
        pidcA2lDetails.getPidcVariantName());
    assertEquals("Pidc A2l File name is equal", "MMD114A0CC1788_MC00.A2L", pidcA2lDetails.getPidcA2lName());
  }

}
