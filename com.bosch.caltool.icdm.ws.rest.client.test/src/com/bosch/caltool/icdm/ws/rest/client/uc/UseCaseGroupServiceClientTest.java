package com.bosch.caltool.icdm.ws.rest.client.uc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseDetailsModel;
import com.bosch.caltool.icdm.model.uc.UsecaseModel;
import com.bosch.caltool.icdm.model.uc.UsecaseTreeGroupModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for Usecase Group
 *
 * @author EMS4KOR
 */
public class UseCaseGroupServiceClientTest extends AbstractRestClientTest {

  private final static long USECASEGROUP_ID = 760473516L;
  private final static long INVALID_USECASEGROUP_ID = -1L;
  private final static long USECASE_ID = 772262915L;
  private final static long PARENT_GROUP_ID = 760469720L;
  private final static long USECASEGROUP_ID1 = 772259417;
  private final static long USECASESECTION_ID = 1676887027;
  private final static long USECASESECTION_ID1 = 772280715;

  /**
   * Test method for {@link UseCaseGroupServiceClientTest#getRootGroups()}
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testGetRootGroups() throws ApicWebServiceException {
    UseCaseGroupServiceClient servClient = new UseCaseGroupServiceClient();
    UsecaseTreeGroupModel retMap = servClient.getUseCaseTreeDataModel();
    assertNotNull("Response should not be null or empty", retMap);
    Map<Long, UseCaseGroup> useCaseGroupMap = retMap.getUseCaseGroupMap();
    assertNotNull("Response should not be null or empty", useCaseGroupMap);
    testOutput(useCaseGroupMap.get(USECASEGROUP_ID));
    Map<Long, UseCase> useCaseMap = retMap.getUsecaseMap();
    assertNotNull("Response should not be null or empty", useCaseMap);
    testOutput(useCaseMap.get(USECASE_ID));
    Set<Long> rootUCGSet = retMap.getRootUCGSet();
    assertNotNull("Response should not be null or empty", rootUCGSet);
    boolean parentIdAvailable = false;
    for (Long useCaseGrpId : rootUCGSet) {
      if (useCaseGrpId.equals(PARENT_GROUP_ID)) {
        parentIdAvailable = true;
        break;
      }
    }
    assertTrue("UseCaseGroup Id is available", parentIdAvailable);
    Map<Long, Set<Long>> childGroupSetMap = retMap.getChildGroupSetMap();
    assertNotNull("Response should not be null or empty", childGroupSetMap);
    Set<Long> childGrpMap = childGroupSetMap.get(PARENT_GROUP_ID);
    boolean useCaseGrpIdAvailable = false;
    for (Long groupId : childGrpMap) {
      if (groupId.equals(USECASEGROUP_ID)) {
        useCaseGrpIdAvailable = true;
        break;
      }
    }
    assertTrue("UseCaseGroup Id is available", useCaseGrpIdAvailable);
    Map<Long, Set<Long>> childUsecaseSetMap = retMap.getChildUsecaseSetMap();
    assertNotNull("Response should not be null or empty", childUsecaseSetMap);
    Set<Long> childUseCaseSetMap = childUsecaseSetMap.get(USECASEGROUP_ID1);
    assertNotNull("Response should not be null or empty", childUseCaseSetMap);
    boolean useCaseIdAvailable = false;
    for (Long useCaseId : childUseCaseSetMap) {
      if (useCaseId.equals(USECASE_ID)) {
        useCaseIdAvailable = true;
        break;
      }
    }
    assertTrue("UseCase Id is available", useCaseIdAvailable);

  }


  /**
   * Test method for {@link UseCaseGroupServiceClient#getUseCaseDetailsModel()}
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testgetUseCaseDetailsModel() throws ApicWebServiceException {

    UseCaseGroupServiceClient servClient = new UseCaseGroupServiceClient();
    UsecaseDetailsModel retModel = servClient.getUseCaseDetailsModel();
    assertNotNull("Response should not be null or empty", retModel);
    Map<Long, UsecaseModel> usecaseDetailsModelMap = retModel.getUsecaseDetailsModelMap();
    assertNotNull("Response should not be null or empty", usecaseDetailsModelMap);
    Map<Long, UseCaseSection> ucSectionMap = retModel.getUcSectionMap();
    assertNotNull("Response should not be null or empty", ucSectionMap);
    UsecaseModel useCaseModel = usecaseDetailsModelMap.get(USECASE_ID);
    assertNotNull("Response should not be null or empty", useCaseModel);
    Map<Long, Set<Long>> childSectionsMap = useCaseModel.getChildSectionsMap();
    assertNotNull("Response should not be null or empty", childSectionsMap);
    Map<Long, Set<Long>> ucItemAttrMap = useCaseModel.getUcItemAttrMap();
    assertNotNull("Response should not be null or empty", ucItemAttrMap);
    Map<Long, Set<Long>> ucItemAttrMapIncDel = useCaseModel.getUcItemAttrMapIncDel();
    assertNotNull("Response should not be null or empty", ucItemAttrMapIncDel);
    UseCaseSection useCaseSection = ucSectionMap.get(USECASESECTION_ID);
    assertNotNull("Response should not be null or empty", useCaseSection);
    testOutput(useCaseSection);
  }


  /**
   * Test method for {@link UseCaseGroupServiceClientTest#getById(Long)}
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testGetById() throws ApicWebServiceException {

    UseCaseGroupServiceClient servClient = new UseCaseGroupServiceClient();
    UseCaseGroup ret = servClient.getById(USECASEGROUP_ID);
    assertNotNull("Response should not be null", ret);
    testOutput(ret);
  }

  /**
   * Test method for {@link UseCaseGroupServiceClientTest#getById(Long)} Negative testcase
   *
   * @throws ApicWebServiceException Webservice Error
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    UseCaseGroupServiceClient servClient = new UseCaseGroupServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Use Case Group with ID" + " '" + INVALID_USECASEGROUP_ID + "'" + " not found");
    UseCaseGroup ret = servClient.getById(INVALID_USECASEGROUP_ID);
    fail("Expected Exception not thrown");

  }

  /**
   * Test method for {@link UseCaseGroupServiceClient#createupdate(UseCaseGroup)}
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testCreateUpdate() throws ApicWebServiceException {
    UseCaseGroupServiceClient servClient = new UseCaseGroupServiceClient();
    UseCaseGroup obj = new UseCaseGroup();
    obj.setNameEng("Testing Group");
    obj.setNameGer("Product Release");
    obj.setDescEng("test");
    obj.setDescGer("zz");
    // invoke create
    UseCaseGroup useCaseGroup = servClient.create(obj);
    // validate create
    assertNotNull("Value is not null", useCaseGroup);
    assertEquals("NameEng is equal", "Testing Group", useCaseGroup.getNameEng());
    assertEquals("NameGer is equal", "Product Release", useCaseGroup.getNameGer());
    assertEquals("DescEng is equal", "test", useCaseGroup.getDescEng());
    assertEquals("DescGer is equal", "zz", useCaseGroup.getDescGer());
    useCaseGroup.setNameEng("Updated Testing Group");
    useCaseGroup.setNameGer("Updated Product Release");
    useCaseGroup.setDescEng("Updated test");
    useCaseGroup.setDescGer("Updated zz");
    // invoke update
    UseCaseGroup useCaseGroupUpdate = servClient.update(useCaseGroup);
    // validate update
    assertNotNull("Value is not null", useCaseGroupUpdate);
    assertEquals("NameEng is equal", "Updated Testing Group", useCaseGroupUpdate.getNameEng());
    assertEquals("NameGer is equal", "Updated Product Release", useCaseGroupUpdate.getNameGer());
    assertEquals("DescEng is equal", "Updated test", useCaseGroupUpdate.getDescEng());
    assertEquals("DescGer is equal", "Updated zz", useCaseGroupUpdate.getDescGer());
  }


  //
  // /**
  // * Test method for {@link com.bosch.caltool.icdm.rest.client.uc.UseCaseGroupServiceClientTest#create()}
  // *
  // */
  // @Test
  // public void testCreate() {
  // LOG.info("=======================================================================================================");
  // LOG.info("
  // TestCreate==============================================================================================");
  // LOG.info("=======================================================================================================");
  // UseCaseGroupServiceClient servClient = new UseCaseGroupServiceClient();
  // try {
  // UseCaseGroup obj = new UseCaseGroup();
  // obj.setId(<Enter your input here>);
  // obj.setParentGroupId(<Enter your input here>);
  // obj.setNameEng(<Enter your input here>);
  // obj.setNameGer(<Enter your input here>);
  // obj.setDescEng(<Enter your input here>);
  // obj.setDescGer(<Enter your input here>);
  // obj.setCreatedUser(<Enter your input here>);
  // obj.setCreatedDate(<Enter your input here>);
  // obj.setModifiedDate(<Enter your input here>);
  // obj.setModifiedUser(<Enter your input here>);
  // obj.setVersion(<Enter your input here>);
  // obj.setDeletedFlag(<Enter your input here>);
  //
  // //Invoke create method
  // UseCaseGroup createdObj = servClient.create(obj);
  // assertNotNull("object not null", createdObj);
  // testOutput(createdObj);
  // } catch (Exception excep) {
  // LOG.error("Error in WS call", excep);
  // assertNull("Error in WS call", excep);
  // }
  // }
  //
  // /**
  // * Test method for {@link com.bosch.caltool.icdm.rest.client.uc.UseCaseGroupServiceClientTest#update()}
  // *
  // */
  // @Test
  // public void testUpdate() {
  // LOG.info("=======================================================================================================");
  // LOG.info("
  // TestUpdate==============================================================================================");
  // LOG.info("=======================================================================================================");
  // UseCaseGroupServiceClient servClient = new UseCaseGroupServiceClient();
  // try {
  // UseCaseGroup obj = new UseCaseGroup();
  // obj.setId(<Enter your input here>);
  // obj.setParentGroupId(<Enter your input here>);
  // obj.setNameEng(<Enter your input here>);
  // obj.setNameGer(<Enter your input here>);
  // obj.setDescEng(<Enter your input here>);
  // obj.setDescGer(<Enter your input here>);
  // obj.setCreatedUser(<Enter your input here>);
  // obj.setCreatedDate(<Enter your input here>);
  // obj.setModifiedDate(<Enter your input here>);
  // obj.setModifiedUser(<Enter your input here>);
  // obj.setVersion(<Enter your input here>);
  // obj.setDeletedFlag(<Enter your input here>);
  //
  // //Invoke update method
  // UseCaseGroup updatedObj = servClient.update(obj);
  // assertNotNull("object not null", updatedObj);
  // testOutput(updatedObj);
  // } catch (Exception excep) {
  // LOG.error("Error in WS call", excep);
  // assertNull("Error in WS call", excep);
  // }
  // }
  //
  // /**
  // * Test method for {@link com.bosch.caltool.icdm.rest.client.uc.UseCaseGroupServiceClientTest#delete()}
  // *
  // */
  // @Test
  // public void testDelete() {
  // LOG.info("=======================================================================================================");
  // LOG.info("
  // TestDelete==============================================================================================");
  // LOG.info("=======================================================================================================");
  // UseCaseGroupServiceClient servClient = new UseCaseGroupServiceClient();
  // try {
  // UseCaseGroup obj = new UseCaseGroup();
  // obj.setId(<Enter your input here>);
  // obj.setParentGroupId(<Enter your input here>);
  // obj.setNameEng(<Enter your input here>);
  // obj.setNameGer(<Enter your input here>);
  // obj.setDescEng(<Enter your input here>);
  // obj.setDescGer(<Enter your input here>);
  // obj.setCreatedUser(<Enter your input here>);
  // obj.setCreatedDate(<Enter your input here>);
  // obj.setModifiedDate(<Enter your input here>);
  // obj.setModifiedUser(<Enter your input here>);
  // obj.setVersion(<Enter your input here>);
  // obj.setDeletedFlag(<Enter your input here>);
  // UseCaseGroup createdObj = servClient.create(obj);
  //
  // //Invoke delete method
  // servClient.delete(createdObj.getId());
  //
  // //If the previous delete method is successful, then getById call will throw exception
  // thrown.expect(DataNotFoundException.class);
  // thrown.expectMessage(containsString("not found"));
  // servClient.getById(createdObj.getId());
  // } catch (Exception excep) {
  // LOG.error("Error in WS call", excep);
  // assertNull("Error in WS call", excep);
  // }
  // }
  //
  /**
   * test output data
   */
  private void testOutput(final UseCaseGroup obj) {
    assertEquals("ParentGroupId is equal", Long.valueOf(760469720), Long.valueOf(obj.getParentGroupId()));
    assertEquals("NameEng is equal", "D2P - Delivery to Production", obj.getNameEng());
    assertEquals("NameGer is equal", "D2P - Ablieferung in die Serie", obj.getNameGer());
    assertEquals("DescEng is equal", "D2P - Delivery to Production (plant or EOL)", obj.getDescEng());
    assertEquals("DescGer is equal", "D2P - Ablieferung in die Serie (Werk oder End of line)",
        obj.getDescGer().toString());
    assertEquals("CreatedUser is equal", "GUK2SI", obj.getCreatedUser().toString());
    assertNotNull("CreatedDate is not null", obj.getCreatedDate());
    assertTrue("DeletedFlag is true", obj.isDeleted());
  }

  /**
   * @param useCase
   */
  private void testOutput(final UseCase useCase) {
    assertEquals("Group Id must be equal", Long.valueOf(772259417), useCase.getGroupId());
    assertEquals("Name Eng must be equal", "Knock Control", useCase.getNameEng());
    assertEquals("Name Ger must be equal", null, useCase.getNameGer());
    assertEquals("Desc Eng must be equal", "Knock Control", useCase.getDescEng());
    assertEquals("Desc Eng must be equal", null, useCase.getDescGer());
    assertEquals("Created user must be equal", "DGS_ICDM", useCase.getCreatedUser());
    // TODO Auto-generated method stub

  }

  /**
   * @param useCaseSection
   */
  private void testOutput(final UseCaseSection useCaseSection) {
    assertEquals("UseCase Id must be equal", Long.valueOf(767625216), useCaseSection.getUseCaseId());
    assertEquals("Name Eng must be equal", "Junit_1576028842816 testCreateUpdate", useCaseSection.getNameEng());
    assertEquals("Name Ger must be equal", "Junit 1", useCaseSection.getNameGer());
    assertEquals("Desc Eng must be equal", "Junit_1576028842816 Updated", useCaseSection.getDescEng());
    assertEquals("Desc Eng must be equal", "Junit 1", useCaseSection.getDescGer());
    assertEquals("Created user must be equal", "BNE4COB", useCaseSection.getCreatedUser());
  }


}
