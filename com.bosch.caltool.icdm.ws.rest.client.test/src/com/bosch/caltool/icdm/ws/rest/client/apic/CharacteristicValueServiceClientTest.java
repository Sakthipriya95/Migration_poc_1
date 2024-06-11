/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.attr.CharacteristicValue;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author AND4COB
 */
public class CharacteristicValueServiceClientTest extends AbstractRestClientTest {

  private final static long CHAR_ID = 767171915L;
  private final static long CHAR_VAL_ID = 768278065L;

  /**
   * Test method for {@link CharacteristicValueServiceClient#getValuesByCharacteristic(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetValuesByCharacteristic() throws ApicWebServiceException {
    CharacteristicValueServiceClient charValServClient = new CharacteristicValueServiceClient();
    Map<Long, CharacteristicValue> charMap = charValServClient.getValuesByCharacteristic(CHAR_ID);
    assertFalse("Response should not be null or empty", ((charMap == null) || charMap.isEmpty()));
    CharacteristicValue cVal = charMap.get(CHAR_VAL_ID);
    testOutput(cVal);
  }

  /**
   * @param cVal
   */
  private void testOutput(final CharacteristicValue cVal) {
    assertNotNull("CreatedDate is not null", cVal.getCreatedDate());
    assertEquals("Char_Name_Eng is equal", "ElecActr", cVal.getValNameEng());
    assertEquals("Char_Name_Ger is equal", "ElecActr", cVal.getValNameGer());
    assertEquals("Created user is equal", "DGS_ICDM", cVal.getCreatedUser());
  }

}
