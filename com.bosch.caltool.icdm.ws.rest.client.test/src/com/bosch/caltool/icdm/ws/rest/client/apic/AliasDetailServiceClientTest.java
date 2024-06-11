/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.AliasDetail;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for Alias Detail
 *
 * @author bne4cob
 */
public class AliasDetailServiceClientTest extends AbstractRestClientTest {

  /**
   * 779837615 - Malaysia (Alias Def : WebFlow)
   */
  private final static Long ALIASDETAIL_READ_ID = 786370065L;

  /**
   * 779269465 - WebFlow
   */
  private final static Long ALIASDEF_READ_ID = 780309114L;

  /**
   * 777403865 - Alias 1
   */
  private final static Long ALIASDEF_EDIT_ID = 780309114L;

  private final long InvalidId = -1234L;

  /**
   * Expected exception
   */

  /**
   * Test method for {@link AliasDetailServiceClient#get(Long) }
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGet() throws ApicWebServiceException {
    AliasDetailServiceClient servClient = new AliasDetailServiceClient();
    AliasDetail ret = servClient.get(ALIASDETAIL_READ_ID);
    assertFalse("should not be null", ret == null);
    testOutput(ret);
  }

  /**
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetNegative() throws ApicWebServiceException {
    AliasDetailServiceClient servClient = new AliasDetailServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Attribute/Value Alias with ID '" + this.InvalidId + "' not found");
    servClient.get(this.InvalidId);
    fail("expected exception not thrown");
  }

  /**
   * @param ret
   */
  private void testOutput(final AliasDetail ret) {
    assertEquals("AD_ID is equal", Long.valueOf(780309114L), ret.getAdId());
    assertEquals("Attr_Id is equal", Long.valueOf(258L), ret.getAttrId());
    assertEquals("Aliasname is equal", "Model", ret.getAliasName());
    assertEquals("Created user is equal", "DGS_ICDM", ret.getCreatedUser());
    assertNotNull("Created date should not be null", ret.getCreatedDate());
  }

  /**
   * Test method for {@link AliasDetailServiceClient#getByAdId(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetByAdId() throws ApicWebServiceException {
    AliasDetailServiceClient servClient = new AliasDetailServiceClient();
    Map<Long, AliasDetail> retMap = servClient.getByAdId(ALIASDEF_READ_ID);
    assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
    LOG.debug("Response element count = {}", retMap.size());
    testOutput(retMap.get(ALIASDETAIL_READ_ID));
  }

  /**
   * @throws ApicWebServiceException web service error
   */
  // @Test
  // internal server error
  public void testGetByAdIdNegative() throws ApicWebServiceException {
    AliasDetailServiceClient servClient = new AliasDetailServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("ID '" + this.InvalidId + "' not found");
    servClient.getByAdId(this.InvalidId);
    fail("Expected exception not thrown");


  }

  /**
   * Test method for {@link AliasDetailServiceClient#create(AliasDetail)},
   * {@link AliasDetailServiceClient#update(AliasDetail)}, {@link AliasDetailServiceClient#delete(AliasDetail)}
   *
   * @throws ApicWebServiceException error in WS call
   */
  @Test
  public void testCreate() throws ApicWebServiceException {
    AliasDetailServiceClient servClient = new AliasDetailServiceClient();
    AttributeServiceClient attrClient = new AttributeServiceClient();

    Attribute input = new Attribute();
    input.setNameEng("Z-Junit AliasTest" + getRunId());
    input.setDescriptionEng("ZJunit01");
    input.setEadmName("ZJunitAliasTest" + getRunId());
    input.setAttrGrpId(519L);
    input.setExternal(false);
    input.setMandatory(false);
    input.setValueTypeId(1L);

    Attribute attr = attrClient.create(input);

    AliasDetail obj = new AliasDetail();
    obj.setAdId(ALIASDEF_EDIT_ID);
    obj.setAttrId(attr.getId());
    obj.setAliasName("Junit_attribute_Alias");

    // Test creation`
    AliasDetail createdObj = servClient.create(obj);
    assertEquals("AD_ID is equal", Long.valueOf(780309114L), createdObj.getAdId());
    assertEquals("Aliasname is equal", "Junit_attribute_Alias", createdObj.getAliasName());
    assertEquals("Aliasname is equal", Long.valueOf(attr.getId()), createdObj.getAttrId());

    // Test Update
    createdObj.setAliasName("Junit_attribute_Alias_updated");
    AliasDetail updatedObj = servClient.update(createdObj);

    // validate update
    assertEquals("Check updated alias name", "Junit_attribute_Alias_updated", updatedObj.getAliasName());

    // Invoke delete method
    servClient.delete(updatedObj);

    // If the previous delete method is successful, then get call will throw exception
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("not found");
    servClient.get(updatedObj.getId());

  }
}
