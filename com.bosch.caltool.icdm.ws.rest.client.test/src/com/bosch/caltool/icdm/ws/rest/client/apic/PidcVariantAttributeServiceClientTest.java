package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for Pidc Variant Attribute
 *
 * @author mkl2cob
 */
public class PidcVariantAttributeServiceClientTest extends AbstractRestClientTest {

  private final static Long VAR_ID = 817L;
  private final static Long ATTR_ID = 615L;
  private final static Long INVALID_ATTRGROUP_ID = -100L;
  private final static Long INVALID_VARATTR_ID = -100L;
  private final static long VAR_ATTR_ID = 818;

  /**
   * @param ret
   */
  /**
   * @throws ApicWebServiceException web service exception
   */
  @Test
  public void getVarAttrsForVariant() throws ApicWebServiceException {
    PidcVariantAttributeServiceClient servClient = new PidcVariantAttributeServiceClient();
    Map<Long, PidcVariantAttribute> res = servClient.getVarAttrsForVariant(VAR_ID);
    assertFalse("Response should not be null or empty", (res == null) || res.isEmpty());
    System.out.println(res.keySet());
    PidcVariantAttribute pidcVariantAttribute = res.get(ATTR_ID);
    testOutput(pidcVariantAttribute);
  }

  /**
   * @param ret
   */


  /**
   * Test method for {@link PidcVariantAttributeServiceClient#getbyId(Long)}
   *
   * @throws ApicWebServiceException web exception
   */

  @Test
  public void testGet() throws ApicWebServiceException {
    PidcVariantAttributeServiceClient servClient = new PidcVariantAttributeServiceClient();
    PidcVariantAttribute ret = servClient.getbyId(VAR_ATTR_ID);
    assertFalse("Response should not be null", (ret == null));
    testOutput(ret);
  }


  /**
   * Test method for {@link PidcVariantAttributeServiceClientTest#testUpdate()}
   *
   * @throws ApicWebServiceException web error
   */
  @Test
  public void testUpdate() throws ApicWebServiceException {

    PidcVariantServiceClient servClient = new PidcVariantServiceClient();
    PidcVariantData pidcVariantData = new PidcVariantData();

    AttributeValue attributeValue = new AttributeValue();
    attributeValue.setDescriptionEng("Junit_PidcVariantForAttr_" + getRunId());
    attributeValue.setAttributeId(126l);
    attributeValue.setTextValueEng("Junit_PidcVariantForAttr_" + getRunId());
    attributeValue.setDeleted(false);
    attributeValue.setChangeComment(null);
    attributeValue.setCharacteristicValueId(null);
    attributeValue.setClearingStatus("R");

    PidcVersionServiceClient versionServiceClient = new PidcVersionServiceClient();
    pidcVariantData.setPidcVersion(versionServiceClient.getById(773515365l));
    pidcVariantData.setVarNameAttrValue(attributeValue);
    // Invoke create method
    PidcVariantData createdObj = servClient.create(pidcVariantData);


    PidcVariantAttributeServiceClient Client = new PidcVariantAttributeServiceClient();
    Map<Long, PidcVariantAttribute> varCustCDMAttr = Client.getVarAttrsForVariant(createdObj.getDestPidcVar().getId());


    Map.Entry<Long, PidcVariantAttribute> entry = varCustCDMAttr.entrySet().iterator().next();
    PidcVariantAttribute value = entry.getValue();
    value.setValueId(null);

    // Invoke update method
    PidcVariantAttribute updatedObj = Client.update(value);
    assertNotNull("object not null", updatedObj);

    assertEquals("version is equal", Long.valueOf(value.getVersion() + 1), updatedObj.getVersion());


  }


  /**
   * test output data
   */
  private void testOutput(final PidcVariantAttribute ret) {
    assertEquals("Var_attrid is equal", Long.valueOf(818), ret.getId());
    assertEquals("pidc version id is equal", Long.valueOf(773510565), ret.getPidcVersionId());
    assertEquals("CreatedUser is equal", "DGS_ICDM", ret.getCreatedUser());
    assertNotNull("CreatedDate is not null", ret.getCreatedDate());
    assertEquals("value id is equal", Long.valueOf(2104), ret.getValueId());


    // assertEquals("Group_Desc_Eng is equal", "BIN5 MQ350-6F 110kW DB-line", ret.getDescription());

  }

  /**
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    PidcVariantAttributeServiceClient servClient = new PidcVariantAttributeServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Variant Attribute with ID '" + INVALID_ATTRGROUP_ID + "' not found");
    servClient.getbyId(INVALID_ATTRGROUP_ID);
    fail("Expected exception not thrown");
  }

  /**
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testgetVarAttrsForVariantNegative() throws ApicWebServiceException {
    PidcVariantAttributeServiceClient servClient = new PidcVariantAttributeServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Variant with ID '" + INVALID_VARATTR_ID + "' not found");
    servClient.getVarAttrsForVariant(INVALID_VARATTR_ID);
    fail("Expected exception not thrown");
  }

}
