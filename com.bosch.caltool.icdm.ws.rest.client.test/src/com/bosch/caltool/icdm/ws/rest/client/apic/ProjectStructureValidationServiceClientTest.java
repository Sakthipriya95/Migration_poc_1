/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.bosch.caltool.icdm.model.apic.ProjectStructureModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author hnu1cob
 */
public class ProjectStructureValidationServiceClientTest extends AbstractRestClientTest {


  /**
   * PIDC Version Link: icdm:pidvarid,23407765481-23407765488 <br>
   */
  private static final long PIDC_ID_FOR_NEGATIVE_CASE = 23407765481L;

  /**
   * A2L File: A2L File: Chrysler->Diesel Engine->PC - Passenger Car->MD1-C->Pidc_structure_junit_dont_modify_2
   * (v1)->MMD114A0CC1788->MMD114A0CC1788_MC50_DISCR.A2L<br>
   * Link: icdm:a2lid,22195927309-2068555001<br>
   */
  private static final long PIDC_A2L_ID = 22195927309L;

  /**
   * Variant: PIDC Variant: Chrysler->Diesel Engine->PC - Passenger Car->MD1-C->Pidc_structure_junit_dont_modify_2
   * (v1)->Variant2<br>
   * Link: icdm:pidvarid,22195927281-22424065430
   */
  private static final long VARIANT_ID = 22539444586L;

  /**
   * PIDC Version Link: icdm:pidvid,22195927281<br>
   */
  private static final long PIDC_VER_ID = 22195927281L;


  private static final long PIDC_ID = 22195927279L;

  /**
   *
   */
  private static final String PROJECT_STRUCTURE_SHOULD_BE_VALID = "Project Structure should be valid";

  /**
   *
   */
  private static final String VALID_RESPONSE = "valid";


  /**
   * @given PIDC_ID, PIDC_VER_ID VARIANT_ID PIDC_A2L_ID
   * @output: VALID_RESPONSE
   * @throws ApicWebServiceException
   */
  @Test
  public void testValidateProjectHierarchy() throws ApicWebServiceException {
    ProjectStructureModel projectStructureModel = new ProjectStructureModel();
    projectStructureModel.setPidcId(PIDC_ID);
    projectStructureModel.setPidcVersId(PIDC_VER_ID);
    projectStructureModel.setVarId(VARIANT_ID);
    projectStructureModel.setPidcA2lId(PIDC_A2L_ID);

    String response = new ProjectStructureValidationServiceClient().validateProjectStructure(projectStructureModel);
    assertEquals(PROJECT_STRUCTURE_SHOULD_BE_VALID, VALID_RESPONSE, response);
  }

  /**
   * @given PIDC_VER_ID VARIANT_ID PIDC_A2L_ID and pidcId=0
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testValidateProjectStructureInvalidPidcId() throws ApicWebServiceException {
    ProjectStructureModel projectStructureModel = new ProjectStructureModel();
    projectStructureModel.setPidcId(0L);
    projectStructureModel.setPidcVersId(PIDC_VER_ID);
    projectStructureModel.setVarId(VARIANT_ID);
    projectStructureModel.setPidcA2lId(PIDC_A2L_ID);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("ID '0' is invalid for Project ID Card");

    new ProjectStructureValidationServiceClient().validateProjectStructure(projectStructureModel);

  }

  /**
   * @given PIDC_ID VARIANT_ID PIDC_A2L_ID and PidcVersId=0
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testValidateProjectStructureInvalidVersId() throws ApicWebServiceException {
    ProjectStructureModel projectStructureModel = new ProjectStructureModel();
    projectStructureModel.setPidcId(PIDC_ID);
    projectStructureModel.setPidcVersId(0L);
    projectStructureModel.setVarId(VARIANT_ID);
    projectStructureModel.setPidcA2lId(PIDC_A2L_ID);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("ID '0' is invalid for PIDC Version");

    new ProjectStructureValidationServiceClient().validateProjectStructure(projectStructureModel);

  }

  /**
   * @given PIDC_ID PIDC_VER_ID PIDC_A2L_ID and varId=-1
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testValidateProjectStructureInvalidVariantId() throws ApicWebServiceException {
    ProjectStructureModel projectStructureModel = new ProjectStructureModel();
    projectStructureModel.setPidcId(PIDC_ID);
    projectStructureModel.setPidcVersId(PIDC_VER_ID);
    projectStructureModel.setVarId(-1L);
    projectStructureModel.setPidcA2lId(PIDC_A2L_ID);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("ID '-1' is invalid for PIDC Variant");

    new ProjectStructureValidationServiceClient().validateProjectStructure(projectStructureModel);

  }

  /**
   * @given PIDC_ID PIDC_VER_ID VARIANT_ID and PidcA2lId=0
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testValidateProjectStructureInvalidPidcA2lId() throws ApicWebServiceException {
    ProjectStructureModel projectStructureModel = new ProjectStructureModel();
    projectStructureModel.setPidcId(PIDC_ID);
    projectStructureModel.setPidcVersId(PIDC_VER_ID);
    projectStructureModel.setVarId(VARIANT_ID);
    projectStructureModel.setPidcA2lId(0L);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("ID '0' is invalid for PIDC A2L File");

    new ProjectStructureValidationServiceClient().validateProjectStructure(projectStructureModel);

  }

  /**
   * @given PIDC_ID PIDC_VER_ID VARIANT_ID and PidcA2lId=null
   * @output VALID_RESPONSE
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testValidateProjectStructureA2lIdIsNull() throws ApicWebServiceException {
    ProjectStructureModel projectStructureModel = new ProjectStructureModel();
    projectStructureModel.setPidcId(PIDC_ID);
    projectStructureModel.setPidcVersId(PIDC_VER_ID);
    projectStructureModel.setVarId(VARIANT_ID);
    projectStructureModel.setPidcA2lId(null);

    String response = new ProjectStructureValidationServiceClient().validateProjectStructure(projectStructureModel);
    assertEquals(PROJECT_STRUCTURE_SHOULD_BE_VALID, VALID_RESPONSE, response);

  }

  /**
   * @given PIDC_ID PIDC_VER_ID VARIANT_ID and PidcA2lId=null
   * @output VALID_RESPONSE
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testValidateProjectStructurePidcIdAndPidcVerId() throws ApicWebServiceException {
    ProjectStructureModel projectStructureModel = new ProjectStructureModel();
    projectStructureModel.setPidcId(PIDC_ID);
    projectStructureModel.setPidcVersId(PIDC_VER_ID);

    String response = new ProjectStructureValidationServiceClient().validateProjectStructure(projectStructureModel);
    assertEquals(PROJECT_STRUCTURE_SHOULD_BE_VALID, VALID_RESPONSE, response);

  }

  /**
   * @given PIDC_ID PIDC_VER_ID VARIANT_ID and PidcA2lId=null
   * @output VALID_RESPONSE
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testValidateProjectStructurePidcIdAndVariantId() throws ApicWebServiceException {
    ProjectStructureModel projectStructureModel = new ProjectStructureModel();
    projectStructureModel.setPidcId(PIDC_ID);
    projectStructureModel.setVarId(VARIANT_ID);

    String response = new ProjectStructureValidationServiceClient().validateProjectStructure(projectStructureModel);
    assertEquals(PROJECT_STRUCTURE_SHOULD_BE_VALID, VALID_RESPONSE, response);

  }

  /**
   * @given only PIDC_ID
   * @throws ApicWebServiceException exception from service
   */
  @Test
  public void testValidateProjectStructureOnlyPidcId() throws ApicWebServiceException {
    ProjectStructureModel projectStructureModel = new ProjectStructureModel();
    projectStructureModel.setPidcId(PIDC_ID);

    String response = new ProjectStructureValidationServiceClient().validateProjectStructure(projectStructureModel);
    assertEquals(PROJECT_STRUCTURE_SHOULD_BE_VALID, VALID_RESPONSE, response);
  }

  /**
   * @given only PIDC_VERS_ID
   * @output: VALID_RESPONSE
   */
  @Test
  public void testValidateProjectStructureOnlyPidcVersId() throws ApicWebServiceException {
    ProjectStructureModel projectStructureModel = new ProjectStructureModel();
    projectStructureModel.setPidcVersId(PIDC_VER_ID);

    String response = new ProjectStructureValidationServiceClient().validateProjectStructure(projectStructureModel);
    assertEquals(PROJECT_STRUCTURE_SHOULD_BE_VALID, VALID_RESPONSE, response);
  }

  /**
   * @given only PIDC_A2L_ID
   * @output: VALID_RESPONSE
   */
  @Test
  public void testValidateProjectStructureOnlyA2lId() throws ApicWebServiceException {
    ProjectStructureModel projectStructureModel = new ProjectStructureModel();
    projectStructureModel.setPidcA2lId(PIDC_A2L_ID);

    String response = new ProjectStructureValidationServiceClient().validateProjectStructure(projectStructureModel);
    assertEquals(PROJECT_STRUCTURE_SHOULD_BE_VALID, VALID_RESPONSE, response);
  }

  /**
   * @given only VARIANT_ID
   * @output: VALID_RESPONSE
   * @throws ApicWebServiceException
   */
  @Test
  public void testValidateProjectStructureOnlyVariantId() throws ApicWebServiceException {
    ProjectStructureModel projectStructureModel = new ProjectStructureModel();
    projectStructureModel.setVarId(VARIANT_ID);

    String response = new ProjectStructureValidationServiceClient().validateProjectStructure(projectStructureModel);
    assertEquals(PROJECT_STRUCTURE_SHOULD_BE_VALID, VALID_RESPONSE, response);
  }


  /**
   * InPositive Testcase <br>
   * PIDC Version Link: icdm:pidvid,1013489805 and get PidcId from PIDC Version Link<br>
   * and PIDC Version Link icdm:pidvarid,23407765481-23407765488
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testValidateProjectStructurePidcVersWithoutPidcId() throws ApicWebServiceException {
    ProjectStructureModel projectStructureModel = new ProjectStructureModel();
    projectStructureModel.setPidcVersId(PIDC_ID_FOR_NEGATIVE_CASE);
    projectStructureModel.setPidcId(775972017L);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Version ID is invalid for PIDC ID");

    new ProjectStructureValidationServiceClient().validateProjectStructure(projectStructureModel);
  }

  /**
   * @given PIDC Version Link: icdm:pidvarid,23407765481-23407765488<br>
   *        and another A2lID: 2114886320L
   * @throws ApicWebServiceException
   */
  @Test
  public void testValidateProjectStructurePidcA2lWithPidcWithoutVersId() throws ApicWebServiceException {
    ProjectStructureModel projectStructureModel = new ProjectStructureModel();
    projectStructureModel.setPidcA2lId(1205578266L);
    projectStructureModel.setVarId(23407765488L);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC VariantID is invalid for the Pidc A2l ID");

    new ProjectStructureValidationServiceClient().validateProjectStructure(projectStructureModel);
  }

  /**
   * @given PIDC Version Link: icdm:pidvid,22195927281 and another A2lID: 2114886320L
   * @throws ApicWebServiceException
   */
  @Test
  public void testValidateProjectStructureA2lWithPidcWihoutVersId() throws ApicWebServiceException {
    ProjectStructureModel projectStructureModel = new ProjectStructureModel();
    projectStructureModel.setPidcVersId(PIDC_VER_ID);
    projectStructureModel.setPidcA2lId(1205578266L);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC A2L ID is invalid for PIDC Version ID");

    new ProjectStructureValidationServiceClient().validateProjectStructure(projectStructureModel);
  }

  /**
   * @given PIDC_ID with another variantID: 768112367
   * @throws ApicWebServiceException
   */
  @Test
  public void testValidateProjectStructurePidcVarWithoutPidcVers() throws ApicWebServiceException {
    ProjectStructureModel projectStructureModel = new ProjectStructureModel();
    projectStructureModel.setPidcVersId(PIDC_ID_FOR_NEGATIVE_CASE);
    projectStructureModel.setVarId(768112367L);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC VariantID is invalid for the PIDC Version ID");

    new ProjectStructureValidationServiceClient().validateProjectStructure(projectStructureModel);
  }

  /**
   * @given PIDC Version Link: icdm:pidvarid,773510665L- <empty> <br>
   *        with another variantID: 768112367L
   * @throws ApicWebServiceException
   */
  @Test
  public void testValidateProjectStructurePidcIdDoesNotContainVariants() throws ApicWebServiceException {
    ProjectStructureModel projectStructureModel = new ProjectStructureModel();
    projectStructureModel.setPidcVersId(773510665L);
    projectStructureModel.setVarId(768112367L);

    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("PIDC Version does not contain variants");

    new ProjectStructureValidationServiceClient().validateProjectStructure(projectStructureModel);
  }
}
