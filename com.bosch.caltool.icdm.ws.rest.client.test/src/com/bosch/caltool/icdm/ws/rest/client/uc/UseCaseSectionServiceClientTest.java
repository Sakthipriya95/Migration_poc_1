package com.bosch.caltool.icdm.ws.rest.client.uc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UseCaseSectionResponse;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client Test for Usecase Section
 *
 * @author bne4cob
 */
public class UseCaseSectionServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String TEST_CREATE_UPDATE = " testCreateUpdate";
  /**
   *
   */
  private static final String JUNIT_UCS = "Junit_Section_";
  private final static long USECASESECTION_ID = 769132019L;
  private final static long INVALID_UCS_ID = -1L;// for some wrong USECASESECTION_ID
  private static final String DEFAULT_SUB_SECTION = "default sub-section";
  private static final String DEFAULT_SECTION = "default section";


  /**
   * Test method for {@link UseCaseSectionServiceClient#getAll()}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetAll() throws ApicWebServiceException {
    UseCaseSectionServiceClient servClient = new UseCaseSectionServiceClient();
    Map<Long, UseCaseSection> retMap = servClient.getAll();
    assertFalse("Response should not be null or empty", ((retMap == null) || retMap.isEmpty()));
    UseCaseSection section = retMap.get(USECASESECTION_ID);
    assertNotNull("Response should not be null", section);
    testOutput(section);
  }

  private void testOutput(final UseCaseSection obj) {
    // Verify contents
    assertEquals("Use case id are equal", Long.valueOf(518118), obj.getUseCaseId());
    assertEquals("NameEng is equal", "(DI-CAT) Cal Data influencing", obj.getNameEng());
    assertEquals("NameGer is equal", "(DI-CAT) Applikationsdatenbestimmend", obj.getNameGer());
    assertEquals("DescEng is equal", "These attributes influence the final calibration data", obj.getDescEng());
    assertEquals("DescGer is equal", "Diese Attribute sind entscheidend für die Bedatung", obj.getDescGer());
    assertEquals("ParentSectionId is equal", Long.valueOf(578817), obj.getParentSectionId());
    assertEquals("CreatedUser is equal", "MAH2SI", obj.getCreatedUser());
    assertNotNull("CreatedDate is not null", obj.getCreatedDate());
    assertFalse("DeletedFlag is false", obj.isDeleted());
    assertFalse("FocusMatrixYn is false", obj.getFocusMatrixYn());
  }

  /**
   * Test method for {@link UseCaseSectionServiceClient#getById(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetById() throws ApicWebServiceException {
    UseCaseSectionServiceClient servClient = new UseCaseSectionServiceClient();
    UseCaseSection ret = servClient.getById(USECASESECTION_ID);
    assertNotNull("Response should not be null", ret);
    testOutput(ret);
  }


  /**
   * negative test case
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetByIdNegative() throws ApicWebServiceException {
    UseCaseSectionServiceClient servClient = new UseCaseSectionServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Use Case Section with ID '" + INVALID_UCS_ID + "' not found");
    servClient.getById(INVALID_UCS_ID);
    fail("Expected exception not thrown");
  }


  /**
   * Test method for {@link UseCaseSectionServiceClient#create(UseCaseSection)},
   * {@link UseCaseSectionServiceClient#update(UseCaseSection)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreateUpdate() throws ApicWebServiceException {
    UseCaseSectionServiceClient ucsServClient = new UseCaseSectionServiceClient();

    UseCaseSection ucSection = new UseCaseSection();
    ucSection.setUseCaseId(10836620660L);
    ucSection.setNameEng(JUNIT_UCS + getRunId() + TEST_CREATE_UPDATE);
    ucSection.setDescEng(JUNIT_UCS);
    ucSection.setDeleted(false);

    UseCaseSection createdParentUCS = null;

    // Invoke create method
    UseCaseSectionResponse useCaseSectionResponse = ucsServClient.create(ucSection);

    assertNotNull("Use Case Section Response not null", useCaseSectionResponse);
    Set<UseCaseSection> ucSectionSet = useCaseSectionResponse.getUcSectionSet();

    for (UseCaseSection useCaseSection : ucSectionSet) {
      if (!DEFAULT_SECTION.equals(useCaseSection.getName())) {
        validateCreateUpdateUCS(useCaseSection);
        createdParentUCS = useCaseSection;
        break;
      }
    }

    doTestForDefaultSubSection(createdParentUCS);

  }

  /**
   * @param createdParentUCS
   * @throws ApicWebServiceException
   */
  private void validateCreateUpdateUCS(final UseCaseSection createdParentUCS) throws ApicWebServiceException {
    UseCaseSectionServiceClient ucsServClient = new UseCaseSectionServiceClient();

    // validate create
    assertNotNull("Created Use Case Section Object is not null", createdParentUCS);

    LOG.info("UseCaseSection Name After Create : {} ", createdParentUCS.getNameEng());
    assertEquals("NameEng is equal", JUNIT_UCS + getRunId() + TEST_CREATE_UPDATE, createdParentUCS.getNameEng());

    // Invoke update method
    createdParentUCS.setDescEng(JUNIT_UCS + getRunId() + " Updated");
    UseCaseSection updatedUCS = ucsServClient.update(createdParentUCS);
    LOG.info("UseCaseSection Name After Update : {} ", updatedUCS.getDescEng());

    // validate update
    assertNotNull("Updated object is not null", updatedUCS);
    assertEquals("NameEng is equal", JUNIT_UCS + getRunId() + TEST_CREATE_UPDATE, updatedUCS.getNameEng());
    assertEquals("DescEng is equal", JUNIT_UCS + getRunId() + " Updated", updatedUCS.getDescEng());
    assertFalse("Deleted flag is false", updatedUCS.isDeleted());
  }

  /**
   * @param servClient
   * @param parentUseCaseSectionToTest
   * @throws ApicWebServiceException
   */
  private void doTestForDefaultSubSection(final UseCaseSection parentUseCaseSectionToTest)
      throws ApicWebServiceException {
    UseCaseSectionServiceClient ucsServClient = new UseCaseSectionServiceClient();

    UseCaseSection subSection = new UseCaseSection();

    subSection.setUseCaseId(10836620660L);
    subSection.setParentSectionId(null != parentUseCaseSectionToTest ? parentUseCaseSectionToTest.getId() : null);
    subSection.setNameEng("Junit_SubSection" + getRunId() + TEST_CREATE_UPDATE);
    subSection.setDescEng("Junit_SubSection");
    subSection.setDeleted(false);


    // Invoke create method to validate default sub-section
    UseCaseSectionResponse useCaseSubSectionResponse = ucsServClient.create(subSection);
    Set<UseCaseSection> ucSubSectionSet = useCaseSubSectionResponse.getUcSectionSet();
    for (UseCaseSection useCaseSubSection : ucSubSectionSet) {
      if (DEFAULT_SUB_SECTION.equals(useCaseSubSection.getName())) {
        LOG.info("default sub-section created with id : {}", useCaseSubSection.getId());
        validateCreateUpdateDefaultSubSec(useCaseSubSection);
        break;
      }
    }
  }

  /**
   * @param useCaseSubSection
   * @throws ApicWebServiceException
   */
  private void validateCreateUpdateDefaultSubSec(final UseCaseSection useCaseSubSection)
      throws ApicWebServiceException {
    UseCaseSectionServiceClient ucsServClient = new UseCaseSectionServiceClient();

    assertEquals("DescEng is Equal", DEFAULT_SUB_SECTION, useCaseSubSection.getDescEng());
    LOG.info("UseCaseSection Description Before Update : {} ", useCaseSubSection.getDescEng());

    useCaseSubSection.setDescEng(DEFAULT_SUB_SECTION + " Updated");
    UseCaseSection updatedDefaultUCS = ucsServClient.update(useCaseSubSection);
    // validate update
    assertNotNull("Updated object is not null", updatedDefaultUCS);
    LOG.info("UseCaseSection Name After Update : {} ", updatedDefaultUCS.getDescEng());

  }


}
