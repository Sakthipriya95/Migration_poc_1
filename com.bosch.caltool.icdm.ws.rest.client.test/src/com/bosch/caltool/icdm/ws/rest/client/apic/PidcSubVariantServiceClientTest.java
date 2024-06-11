package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for Pidc Sub Variant
 *
 * @author HNU1COB
 */
public class PidcSubVariantServiceClientTest extends AbstractRestClientTest {

  private final static Long PIDC_SUBVARIANT_ID = 791682870L;
  private final static Long INVALID_PIDC_SUBVARIANT_ID = -791682870L;

  private final static Long PIDC_VERSION_ID = 773510565L;


  /**
   * Test method for {@link PidcSubVariantServiceClient#get(Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGet() throws ApicWebServiceException {
    PidcSubVariantServiceClient servClient = new PidcSubVariantServiceClient();
    PidcSubVariant ret = servClient.get(PIDC_SUBVARIANT_ID);
    assertNotNull("Response should not be null", ret);
    testOutput(ret);
  }

  /**
   * Test method for {@link PidcSubVariantServiceClient#get(Long)}.Negative Test
   *
   * @throws ApicWebServiceException 'PIDC Sub-variant with ID not found'
   */
  @Test
  public void testGetNegative() throws ApicWebServiceException {
    PidcSubVariantServiceClient servClient = new PidcSubVariantServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Sub-variant with ID '" + INVALID_PIDC_SUBVARIANT_ID + "' not found");
    servClient.get(INVALID_PIDC_SUBVARIANT_ID);
    fail("Expected exception is not thrown");
  }


  /**
   * Test method for {@link PidcSubVariantServiceClient#create(PidcSubVariantData)},
   * {@link PidcSubVariantServiceClient#update(PidcSubVariantData)}
   *
   * @throws ApicWebServiceException exception
   */

  @Test
  public void testCreateUpdate() throws ApicWebServiceException {
    PidcSubVariantServiceClient servClient = new PidcSubVariantServiceClient();
    AttributeValue attributeValue = new AttributeValue();
    attributeValue.setDescriptionEng("Junit_SubVariant_" + getRunId());
    attributeValue.setAttributeId(126l);
    attributeValue.setTextValueEng("Junit_SubVariant_" + getRunId());
    attributeValue.setDeleted(false);
    attributeValue.setChangeComment(null);
    attributeValue.setCharacteristicValueId(null);
    attributeValue.setClearingStatus("R");

    PidcSubVariantData subVariantData = new PidcSubVariantData();
    subVariantData.setPidcVariantId(768208517l);
    subVariantData.setSubvarNameAttrValue(attributeValue);
    // Invoke create method
    PidcSubVariantData createdObj = servClient.create(subVariantData);
    // validate create
    assertNotNull("Created object not null", createdObj);
    PidcSubVariant createdPidcSubVariant = createdObj.getDestPidcSubVar();
    assertEquals("Pidc Version Id is equal", Long.valueOf(773515365), createdPidcSubVariant.getPidcVersionId());
    assertEquals("Pidc Variant Id is equal", Long.valueOf(768208517), createdPidcSubVariant.getPidcVariantId());
    assertFalse("Deleted Flag is equal", createdPidcSubVariant.isDeleted());
    assertEquals("Description is equal", "Junit_SubVariant_" + getRunId(), createdPidcSubVariant.getDescription());
    assertEquals("Textvalue is equal", "Junit_SubVariant_" + getRunId(), createdPidcSubVariant.getName());
    assertEquals("Attribute id is equal", Long.valueOf(126), createdObj.getSubvarNameAttrValue().getAttributeId());


    AttributeValue value = subVariantData.getSubvarNameAttrValue();
    value.setDescriptionEng("Junit_SubVariant_Updated_" + getRunId());
    value.setTextValueEng("Junit_SubVariant_Updated_" + getRunId());
    createdObj.setSrcPidcSubVar(createdPidcSubVariant);
    createdObj.setNameUpdated(true);
    createdObj.setSubvarNameAttrValue(value);
    // invoke update method
    PidcSubVariantData updatedPidcSubVariantData = servClient.update(createdObj);
    // validate update
    assertTrue("Name is updated", updatedPidcSubVariantData.isNameUpdated());
    assertEquals("Description is equal", "Junit_SubVariant_Updated_" + getRunId(),
        updatedPidcSubVariantData.getDestPidcSubVar().getDescription());
    assertEquals("Textvalue is equal", "Junit_SubVariant_Updated_" + getRunId(),
        updatedPidcSubVariantData.getDestPidcSubVar().getName());

  }


  private void testOutput(final PidcSubVariant obj) {
    assertEquals("Sub Variant ID is equal", Long.valueOf(791682870), obj.getId());
    assertEquals("Variant Id is equal", Long.valueOf(784497117), obj.getPidcVariantId());
    assertEquals("Value Id is equal", Long.valueOf(768964559), obj.getNameValueId());
    assertNotNull("Created Date is not null", obj.getCreatedDate());
    assertEquals("Created User is equal", "FRO2BH", obj.getCreatedUser());
    assertEquals("Pidc Version Id is equal", Long.valueOf(773510565), obj.getPidcVersionId());
  }

  /**
   * Test method for {@link PidcSubVariantServiceClient#getSubVariantsForVersion(Long, boolean)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetSubVariantsForVersion() throws ApicWebServiceException {
    PidcSubVariantServiceClient servClient = new PidcSubVariantServiceClient();
    Map<Long, PidcSubVariant> retMap = servClient.getSubVariantsForVersion(PIDC_VERSION_ID, false);
    assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
    testOutput(retMap.get(PIDC_SUBVARIANT_ID));
  }

  /**
   * Test method for {@link PidcSubVariantServiceClient#getSubVariantsForVersion(Long, boolean)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetSubVariantsForVersionNegative() throws ApicWebServiceException {
    PidcSubVariantServiceClient servClient = new PidcSubVariantServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    servClient.getSubVariantsForVersion(-12L, false);
    fail("Expected exception not thrown");
  }
}
