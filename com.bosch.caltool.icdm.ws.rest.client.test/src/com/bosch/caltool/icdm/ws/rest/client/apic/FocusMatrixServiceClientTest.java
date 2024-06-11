package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrix;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixDetailsModel;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixMappingData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for Focus Matrix
 *
 * @author MKL2COB
 */
public class FocusMatrixServiceClientTest extends AbstractRestClientTest {

  private final static Long FOCUSMATRIX_ID = 1117441695L;
  /**
   * CONSTANT FOR FOCUS MATRIX VERSION ID
   */
  private final static Long FOCUSMATRIX_VERSION_ID = 1117441693L;


  /**
   * Test method for {@link FocusMatrixServiceClient#getById(Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    FocusMatrixServiceClient servClient = new FocusMatrixServiceClient();
    FocusMatrix ret = servClient.getById(FOCUSMATRIX_ID);
    assertFalse("Response should not be null", (ret == null));
    testOutput(ret);
  }

  /**
   * Test method for {@link FocusMatrixServiceClient#getById(Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    FocusMatrixServiceClient servClient = new FocusMatrixServiceClient();
    this.thrown.expectMessage("Focus Matrix with ID '" + -1l + "' not found");
    servClient.getById(-1l);
    fail("Expected exception not thrown");
  }

  /**
   * Test method for {@link FocusMatrixServiceClient#create(FocusMatrix)} ,
   * {@link FocusMatrixServiceClient#update(FocusMatrix)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateUpdate() throws ApicWebServiceException {
    FocusMatrixServiceClient servClient = new FocusMatrixServiceClient();

    Attribute input = new Attribute();
    input.setNameEng("Junit_For_FM_" + getRunId());
    input.setDescriptionEng("Junit_For_FM");
    input.setEadmName("Junit_For_FM_" + getRunId());
    input.setAttrGrpId(519L);
    input.setExternal(false);
    input.setMandatory(false);
    input.setValueTypeId(1L);
    Attribute createdAttr = new AttributeServiceClient().create(input);
    FocusMatrix obj = new FocusMatrix();
    obj.setComments("Junit_" + getRunId());
    obj.setUseCaseId(469540L);
    obj.setSectionId(774382569L);
    obj.setAttrId(createdAttr.getId());
    obj.setIsDeleted(false);
    obj.setFmVersId(FOCUSMATRIX_VERSION_ID);
    // Invoke create method
    FocusMatrix createdObj = servClient.create(obj);
    assertNotNull("object not null", createdObj);
    // validate create
    assertEquals("Comments is equal", "Junit_" + getRunId(), createdObj.getComments());
    assertNotNull("CreatedUser is not null", createdObj.getCreatedUser());
    assertNotNull("CreatedDate is not null", createdObj.getCreatedDate());
    assertEquals("UseCaseId is equal", Long.valueOf(469540), createdObj.getUseCaseId());
    assertEquals("SectionId is equal", Long.valueOf(774382569), createdObj.getSectionId());
    assertEquals("AttrId is equal", Long.valueOf(createdAttr.getId()), createdObj.getAttrId());
    assertEquals("FmVersId is equal", Long.valueOf(FOCUSMATRIX_VERSION_ID), createdObj.getFmVersId());
    assertFalse("IsDeleted is equal", createdObj.getIsDeleted());

    createdObj.setComments("Junit_Updated_" + getRunId());
    // Invoke update method
    FocusMatrix updatedObj = servClient.update(createdObj);
    // validate update
    assertNotNull("object not null", updatedObj);
    assertEquals("Comments is equal", "Junit_Updated_" + getRunId(), updatedObj.getComments());

  }

  /**
   * Test method for {@link FocusMatrixServiceClient#multipleCreate(List)} ,
   * {@link FocusMatrixServiceClient#multipleUpdate(List)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testMultipleCreateUpdate() throws ApicWebServiceException {
    FocusMatrixServiceClient servClient = new FocusMatrixServiceClient();

    Attribute input = new Attribute();
    input.setNameEng("Junit_For_multipleFM_" + getRunId());
    input.setDescriptionEng("Junit_For_multipleFM");
    input.setEadmName("Junit_For_multipleFM_" + getRunId());
    input.setAttrGrpId(519L);
    input.setExternal(false);
    input.setMandatory(false);
    input.setValueTypeId(1L);
    Attribute createdAttr = new AttributeServiceClient().create(input);
    FocusMatrix obj = new FocusMatrix();
    obj.setComments("Junit_multiple_" + getRunId());
    obj.setUseCaseId(469540L);
    obj.setSectionId(774382569L);
    obj.setAttrId(createdAttr.getId());
    obj.setIsDeleted(false);
    obj.setFmVersId(FOCUSMATRIX_VERSION_ID);

    // Invoke create method
    Map<Long, FocusMatrix> retMap = servClient.multipleCreate(Arrays.asList(obj));
    FocusMatrix createdObj = retMap.values().iterator().next();
    assertNotNull("object not null", createdObj);
    // validate create
    assertEquals("Comments is equal", "Junit_multiple_" + getRunId(), createdObj.getComments());
    assertNotNull("CreatedUser is not null", createdObj.getCreatedUser());
    assertNotNull("CreatedDate is not null", createdObj.getCreatedDate());
    assertEquals("UseCaseId is equal", Long.valueOf(469540), createdObj.getUseCaseId());
    assertEquals("SectionId is equal", Long.valueOf(774382569), createdObj.getSectionId());
    assertEquals("AttrId is equal", Long.valueOf(createdAttr.getId()), createdObj.getAttrId());
    assertEquals("FmVersId is equal", Long.valueOf(FOCUSMATRIX_VERSION_ID), createdObj.getFmVersId());
    assertFalse("IsDeleted is equal", createdObj.getIsDeleted());

    createdObj.setComments("Junit_multipleUpdated_" + getRunId());
    // Invoke update method
    Map<Long, FocusMatrix> UpdatedMap = servClient.multipleUpdate(Arrays.asList(createdObj));
    FocusMatrix updatedObj = UpdatedMap.values().iterator().next();
    // validate update
    assertNotNull("object not null", updatedObj);
    assertEquals("Comments is equal", "Junit_multipleUpdated_" + getRunId(), updatedObj.getComments());
  }

  /**
   * testing web service for focus matrix for a FM version
   * {@link FocusMatrixServiceClient#getFocusMatrixForVersion(Long)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetFocusMatrixForVersion() throws ApicWebServiceException {
    FocusMatrixServiceClient servClient = new FocusMatrixServiceClient();
    FocusMatrixDetailsModel focusMatrixForVersion = servClient.getFocusMatrixForVersion(FOCUSMATRIX_VERSION_ID);
    assertFalse("Response should not be null", (focusMatrixForVersion == null));
    Map<Long, FocusMatrix> retMap = focusMatrixForVersion.getFocusMatrixMap();
    testOutput(retMap.get(FOCUSMATRIX_ID));
  }


  /**
   * Test method for {@link FocusMatrixServiceClient#isFocusMatrixAvailableWhileMapping(FocusMatrixMappingData)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testIsFocusMatrixAvailableWhileMapping() throws ApicWebServiceException {
    FocusMatrixServiceClient servClient = new FocusMatrixServiceClient();

    FocusMatrixMappingData data = new FocusMatrixMappingData();
    data.setUseCaseId(469538L);
    data.setUseCaseSectionId(789845216L);
    data.setAttrId(769955025L);
    boolean ret = servClient.isFocusMatrixAvailableWhileMapping(data);
    assertTrue("Response is true", ret);
  }

  /**
   * Test method for {@link FocusMatrixServiceClient#isFocusMatrixAvailableWhileUnMapping(FocusMatrixMappingData)}
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testIsFocusMatrixAvailableWhileUnMapping() throws ApicWebServiceException {
    FocusMatrixServiceClient servClient = new FocusMatrixServiceClient();

    FocusMatrixMappingData data = new FocusMatrixMappingData();
    data.setUseCaseId(469538L);
    data.setUseCaseSectionId(789845216L);
    data.setAttrId(769955025L);
    boolean ret = servClient.isFocusMatrixAvailableWhileUnMapping(data);
    assertTrue("Response is true", ret);
  }

  /**
   * test output data
   */
  private void testOutput(final FocusMatrix obj) {
    assertNull("UcpaId is equal", obj.getUcpaId());
    assertNull("ColorCode is equal", obj.getColorCode());
    assertNull("Comments is equal", obj.getComments());
    assertNotNull("CreatedUser is equal", obj.getCreatedUser());
    assertNotNull("CreatedDate is not null", obj.getCreatedDate());
    assertNull("Link is equal", obj.getLink());
    assertEquals("UseCaseId is equal", Long.valueOf(469540), obj.getUseCaseId());
    assertEquals("SectionId is equal", Long.valueOf(774382569), obj.getSectionId());
    assertEquals("AttrId is equal", Long.valueOf(224), obj.getAttrId());
    assertEquals("FmVersId is equal", Long.valueOf(1117441693), obj.getFmVersId());
    assertFalse("IsDeleted is equal", obj.getIsDeleted());
  }
}
