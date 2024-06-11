package com.bosch.caltool.icdm.ws.rest.client.uc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.general.DataCreationModel;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UsecaseCreationData;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.icdm.model.uc.UsecaseType;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for Usecase
 *
 * @author AND4COB
 */

public class UseCaseServiceClientTest extends AbstractRestClientTest {

  private final static Long USECASE_ID = 772262165L;
  private final static Long INVALID_UCS_ID = -100L; // for some wrong usecase_id


  /**
   * Test method for {@link UseCaseServiceClient#getAll()}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetAll() throws ApicWebServiceException {
    UseCaseServiceClient servClient = new UseCaseServiceClient();
    Map<Long, UseCase> retMap = servClient.getAll();
    assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
    UseCase useCase = retMap.get(USECASE_ID);
    assertNotNull("Response should not be null", useCase);
    testUseCase(useCase);
  }

  /**
   * Test method for {@link UseCaseServiceClient#getById(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    UseCaseServiceClient servClient = new UseCaseServiceClient();
    UseCase useCase = servClient.getById(USECASE_ID);
    assertFalse("Response should not be null", (useCase == null));
    testUseCase(useCase);
  }


  /**
   * negative test case
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    UseCaseServiceClient servClient = new UseCaseServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Use Case with ID '" + INVALID_UCS_ID + "' not found");
    servClient.getById(INVALID_UCS_ID);
    fail("Expected exception not thrown");
  }


  /**
   * Test method for {@link UseCaseServiceClient#getUseCaseEditorData(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetEditorData() throws ApicWebServiceException {
    UseCaseServiceClient servClient = new UseCaseServiceClient();
    UsecaseEditorModel ret = servClient.getUseCaseEditorData(USECASE_ID);
    assertFalse("Response should not be null", (ret == null));
  }


  /**
   * for empty inputset Test method for {@link UseCaseServiceClient#getUseCases(Set)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetUseCasesEmpty() throws ApicWebServiceException {
    UseCaseServiceClient servClient = new UseCaseServiceClient();
    Set<Long> inputSet = new HashSet<>();
    Set<UsecaseType> ret = servClient.getUseCases(inputSet);
    assertNotNull("Response should not be null", (ret == null));
  }


  /**
   * for testing multiple usecaseIds
   *
   * @throws ApicWebServiceException web service error
   */

  @Test
  public void testGetUseCasesMultiple() throws ApicWebServiceException {
    UseCaseServiceClient servClient = new UseCaseServiceClient();
    Set<Long> inputSet = new HashSet<>();
    inputSet.add(76041L);
    inputSet.add(760569216L);
    inputSet.add(1495789611L);
    inputSet.add(786993666L);
    inputSet.add(469544L);
    inputSet.add(1487765628L);
    Set<UsecaseType> ret = servClient.getUseCases(inputSet);
    assertFalse("Response should not be null", (ret == null));
  }

  /**
   * for empty inputset Test method for {@link UseCaseServiceClient#getUsecaseEditorModels(Set)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetUsecaseEditorModelsEmpty() throws ApicWebServiceException {
    UseCaseServiceClient servClient = new UseCaseServiceClient();
    Set<Long> inputSet = new HashSet<>();
    Map<Long, UsecaseEditorModel> ucEditorModels = servClient.getUsecaseEditorModels(inputSet);
    assertNotNull("Response should not be null", ucEditorModels);
  }

  /**
   * for testing with a set containing multiple usecaseids Test method for
   * {@link UseCaseServiceClient#getUsecaseEditorModels(Set)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetUsecaseEditorModelsMultiple() throws ApicWebServiceException {
    UseCaseServiceClient servClient = new UseCaseServiceClient();
    Set<Long> inputSet = new HashSet<>();
    inputSet.add(760419516L);
    inputSet.add(760473517L);
    inputSet.add(949462716L);
    inputSet.add(490726L);
    Map<Long, UsecaseEditorModel> ucEditorModels = servClient.getUsecaseEditorModels(inputSet);
    assertNotNull("Response should not be null", ucEditorModels);
    UsecaseEditorModel ucEditorModel = ucEditorModels.get(760419516L);
    testUCEditorModel(ucEditorModel);
  }

  /**
   * @param ucEditorModel
   */
  private void testUCEditorModel(final UsecaseEditorModel ucEditorModel) {
    Map<Long, Attribute> attrMap = ucEditorModel.getAttrMap();
    assertNotNull("Attr map not null", attrMap);
    assertFalse("Attr map not empty", attrMap.isEmpty());
    LOG.debug("Attr Map size = {}", attrMap.size());

    Attribute attribute = attrMap.get(2766L);
    assertEquals("Attr_name_Eng is equal", "EGR HP - Configuration", attribute.getNameEng());
    assertEquals("Attr_name_Ger is equal", "AGR HD - Konfiguration", attribute.getNameGer());
    assertEquals("Attr_Desc_Eng is equal", "Configuration of High Pressure EGR system", attribute.getDescriptionEng());
    assertEquals("Attr_Desc_Ger is equal", "Konfiguration der Hochdruck-AGR-Strecke", attribute.getDescriptionGer());
    assertEquals("Group_ID is equal", Long.valueOf(770157637), attribute.getAttrGrpId());
    assertEquals("Created user is equal", "tbd2si", attribute.getCreatedUser());
  }

  /**
   * for empty inputset Test method for {@link UseCaseServiceClient#getUseCaseWithSectionTree(Set)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetUseCaseWithSectionTreeEmpty() throws ApicWebServiceException {
    UseCaseServiceClient servClient = new UseCaseServiceClient();
    Set<Long> inputSet = new HashSet<>();
    Set<UsecaseType> ret = servClient.getUseCaseWithSectionTree(inputSet);
    assertFalse("Response should not be null", (ret == null));
  }


  /**
   * for inputset containing multiple usecaseids Test method for
   * {@link UseCaseServiceClient#getUseCaseWithSectionTree(Set)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetUseCaseWithSectionTreeMultiple() throws ApicWebServiceException {
    UseCaseServiceClient servClient = new UseCaseServiceClient();
    Set<Long> inputSet = new HashSet<>();
    inputSet.add(760419516L);
    inputSet.add(760473517L);
    inputSet.add(949462716L);
    inputSet.add(490726L);
    Set<UsecaseType> ret = servClient.getUseCaseWithSectionTree(inputSet);
    assertFalse("Response should not be null", (ret == null));
  }


  /**
   * test output data
   */
  private void testUseCase(final UseCase useCase) {
    assertEquals("GroupId is equal", "772259417", useCase.getGroupId().toString());
    assertEquals("NameEng is equal", "Driveability", useCase.getNameEng());
    assertEquals("NameGer is equal", null, useCase.getNameGer());
    assertEquals("DescEng is equal", "Driveability", useCase.getDescEng());
    assertEquals("DescGer is equal", null, useCase.getDescGer());
    assertEquals("CreatedUser is equal", "DGS_ICDM", useCase.getCreatedUser());
    assertNotNull("CreatedDate is not null", useCase.getCreatedDate());
    assertEquals("DeletedFlag is equal", false, useCase.isDeleted());
    assertEquals("FocusMatrixYn is equal", false, useCase.getFocusMatrixYn());
    assertEquals("LastConfirmationDate is equal", null, useCase.getLastConfirmationDate());
  }

  /**
   * Test method for {@link UseCaseServiceClient#create(UsecaseCreationData)},
   * {@link UseCaseServiceClient#update(UseCase)}, {@link UseCaseServiceClient#changeUpToDateStatus(UseCase, Boolean)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreateUpdateUseCase() throws ApicWebServiceException {
    UseCaseServiceClient servClient = new UseCaseServiceClient();

    UseCase useCase = new UseCase();
    useCase.setGroupId(490728L);
    useCase.setNameEng("Junit_" + getRunId() + " testCreateUpdateUseCase");
    useCase.setNameGer("Junit 1" + getRunId());
    useCase.setDescEng("Junit 1");
    useCase.setDescGer("Junit 1");
    // useCase.setDeleted(false);

    UsecaseCreationData ucCreationData = new UsecaseCreationData();
    ucCreationData.setUsecase(useCase);
    ucCreationData.setOwnerId(1256566L);

    // Invoke create method
    DataCreationModel<UseCase> createdObj = servClient.create(ucCreationData);
    LOG.info("UseCaseName After Create : {} ", createdObj.getDataCreated().getNameEng());


    // validate create
    UseCase retObj = createdObj.getDataCreated();
    assertNotNull("Object is not null", createdObj);
    assertEquals("NameEng is equal", "Junit_" + getRunId() + " testCreateUpdateUseCase", retObj.getNameEng());
    assertEquals("DescEng is equal", "Junit 1", retObj.getDescEng());
    assertEquals("DescGer is equal", "Junit 1", retObj.getDescGer());


    // Invoke update method
    retObj.setNameEng("Junit_" + getRunId() + " NameEng Updated");
    retObj.setNameGer("Junit_" + getRunId() + " NameGer Updated");
    assertFalse("Deleted flag is false before update", retObj.isDeleted());
    retObj.setDeleted(true);
    UseCase updatedObj = servClient.update(retObj);
    LOG.info("UseCase NameEng After Update : {} ", updatedObj.getNameEng());
    LOG.info("UseCase NameGer After Update : {} ", updatedObj.getNameGer());


    // validate update
    assertNotNull("Updated object is not null", updatedObj);
    assertEquals("NameEng is equal", "Junit_" + getRunId() + " NameEng Updated", updatedObj.getNameEng());
    assertEquals("NameGer is equal", "Junit_" + getRunId() + " NameGer Updated", updatedObj.getNameGer());
    assertEquals("DescEng is equal", "Junit 1", updatedObj.getDescEng());
    assertEquals("DescGer is equal", "Junit 1", updatedObj.getDescGer());
    assertTrue("Deleted flag is true", updatedObj.isDeleted());


    // Invoke changeUpToDate method
    assertNull("Last Confirmation Date of the created usecase is null", retObj.getLastConfirmationDate());
    retObj.setNameEng("Junit_" + getRunId() + " Status Updated");
    UseCase retObj1 = servClient.changeUpToDateStatus(retObj, true);
    assertNotNull("Last Confirmation Date of the created usecase is not null", retObj1.getLastConfirmationDate());
  }
}
