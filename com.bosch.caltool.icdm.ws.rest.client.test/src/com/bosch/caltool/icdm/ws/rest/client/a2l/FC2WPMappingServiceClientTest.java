/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.BaseComponent;
import com.bosch.caltool.icdm.model.a2l.PTType;
import com.bosch.caltool.icdm.model.cdr.RvwFuncDetails;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMappingWithDetails;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class FC2WPMappingServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String DATA_NULL_MSG = "report data not null";
  /**
   *
   */
  private static final String MAPPINGMSG = "Mapping retrieved by mapping version key";
  /**
   *
   */
  private static final long FUNCTION_VER_ID = 926370015L;
  /**
   *
   */
  private static final long FUNCTION_ID = 774302777L;
  /**
   *
   */
  private static final String FUNCTION_NAME = "vscdiagdmy";
  /**
   * 787372417 PS-EC (formerly DGS-EC)
   */
  private static final long T06_DIV_VAL_ID = 787372417L;
  /**
   * FC-WP Version : PS-EC (formerly DGS-EC) - v2.1, <br>
   * 230671 - FC2WP Gen2 - generic
   */
  private static final String T05_FC2_WP_NAME = "FC2WP Gen2 - generic";
  /**
   * FC-WP Version : PS-EC (formerly DGS-EC) - v2.1, <br>
   * 787372417 - PS-EC (formerly DGS-EC)
   */
  private static final long T05_DIV_VAL_ID = 787372417L;
  /**
   * A2L File: Alfa Romeo->Gasoline Engine->PC - Passenger Car->ME(D)17->Alfa 1.8L, FamB, Gen2, US (Version
   * 1)->D173307->009-Alfa_FamB_Gen3_1.8L_TFSI_MED17.3.3_D17330713A3_or_1.A2L
   * <p>
   * URL : icdm:a2lid,788934409-1403715001
   */
  private static final long T05_A2L_ID = 1403715001L;
  /**
   * A2L File: Alfa Romeo->Gasoline Engine->PC - Passenger Car->ME(D)17->Alfa 1.8L, FamB, Gen2, US (Version
   * 1)->D173307->009-Alfa_FamB_Gen3_1.8L_TFSI_MED17.3.3_D17330713A3_or_1.A2L
   * <p>
   * URL : icdm:a2lid,788934409-1403715001
   */
  private static final long T04_PIDC_A2L_ID = 788934409L;
  /**
   * FC-WP Version : PS-EC (formerly DGS-EC) - v2.1, <br>
   * Function - SCR
   */
  private static final long T03_FCWP_MAP_ID_REF = 1433693537L;
  /**
   * FC-WP Version : PS-EC (formerly DGS-EC) - v2.1, <br>
   * Function - FlFPSwt_DD
   */
  private static final long T03_FCWP_MAP_ID = 1433693533L;
  /**
   * FC-WP Version : PS-EC (formerly DGS-EC) - v2.1, <br>
   * Function - HDRVW
   */
  private static final long T02_FCWP_MAP_ID = 1433693529L;
  /**
   * FC-WP Version : PS-EC (formerly DGS-EC) - v2.1
   */
  private static final long T01_FCWP_VER_ID = 1433693528L;

  /**
   * Fetch all FC2WP Mappings for FC2WP Version
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void testFC2WPMappingByVersion() throws ApicWebServiceException {
    FC2WPMappingServiceClient servClient = new FC2WPMappingServiceClient();
    FC2WPMappingWithDetails mappingByVersion = servClient.getFC2WPMappingByVersion(T01_FCWP_VER_ID);

    testOutput(mappingByVersion);
    assertNotNull(DATA_NULL_MSG, mappingByVersion);
    logOutput(MAPPINGMSG, mappingByVersion);
  }

  /**
   * Fetch FC2WP mapping by primary Key
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void testGetFC2WPMappingById() throws ApicWebServiceException {

    FC2WPMappingServiceClient servClient = new FC2WPMappingServiceClient();
    FC2WPMappingWithDetails mappingByID = servClient.getFC2WPMappingById(T02_FCWP_MAP_ID);

    testOutput(mappingByID);
    assertNotNull(DATA_NULL_MSG, mappingByID);

    logOutput("Mapping retrieved by mapping key", mappingByID);

  }

  /**
   * Test Creating FC2WP mapping
   *
   * @throws ApicWebServiceException service error
   */

  public void testCreateFC2WPMapping() throws ApicWebServiceException {
    List<FC2WPMapping> fc2WpMappingListToConsider = new ArrayList<>();
    FC2WPMapping createdObject = createFC2WPMapping();
    fc2WpMappingListToConsider.add(createdObject);
    FC2WPMappingServiceClient servClient = new FC2WPMappingServiceClient();
    FC2WPMappingWithDetails createFC2WPMapping = servClient.createFC2WPMapping(fc2WpMappingListToConsider);
    FC2WPMapping fc2WpMapping = createFC2WPMapping.getFc2wpMappingMap().get(FUNCTION_NAME);
    testCreatedFc2WpMapping(fc2WpMapping);
    testOutput(createFC2WPMapping);
  }

  private FC2WPMapping createFC2WPMapping() {
    FC2WPMapping fc2wpMapping = new FC2WPMapping();
    fc2wpMapping.setFcwpVerId(FUNCTION_VER_ID);
    fc2wpMapping.setName(FUNCTION_NAME);
    fc2wpMapping.setFunctionId(FUNCTION_ID);
    fc2wpMapping.setWpDivId(null);
    fc2wpMapping.setDeleted(false);
    fc2wpMapping.setAgreeWithCoc(false);
    fc2wpMapping.setBcID(956329415L);
    Set<Long> ptTypeSet = new HashSet<>();
    ptTypeSet.add(926286165L);
    fc2wpMapping.setPtTypeSet(ptTypeSet);
    fc2wpMapping.setWpDivId(795146666L);
    return fc2wpMapping;
  }

  private void testCreatedFc2WpMapping(final FC2WPMapping fc2WpMapping) {
    assertEquals("FC2Wp version ID is equal", Long.valueOf(FUNCTION_VER_ID), fc2WpMapping.getFcwpVerId());
    assertEquals("Function name is equal", FUNCTION_NAME, fc2WpMapping.getName());
    assertEquals("Function ID is equal", Long.valueOf(FUNCTION_ID), fc2WpMapping.getFunctionId());
    assertNotNull("Function ID is not null", fc2WpMapping.getFunctionId());
  }


  /**
   * Test FC2WP MAPPING update
   *
   * @throws ApicWebServiceException service error
   * @throws CloneNotSupportedException error while cloning
   */
  @Test
  public void testFc2wpMappingUpdate() throws ApicWebServiceException, CloneNotSupportedException {

    FC2WPMappingServiceClient servClient = new FC2WPMappingServiceClient();

    FC2WPMappingWithDetails mappingByVersion1 = servClient.getFC2WPMappingById(T03_FCWP_MAP_ID);
    logOutput("Mapping to update(A)", mappingByVersion1);
    FC2WPMapping mappingToUpdate = mappingByVersion1.getFc2wpMappingMap().values().iterator().next();
    FC2WPMapping mtuBackup = mappingToUpdate.clone();

    FC2WPMappingWithDetails mappingByVersion2 = servClient.getFC2WPMappingById(T03_FCWP_MAP_ID_REF);
    logOutput("Reference Mapping(B)", mappingByVersion2);
    FC2WPMapping refMapping = mappingByVersion2.getFc2wpMappingMap().values().iterator().next();

    copyMappingDetails(mappingToUpdate, refMapping);
    List<FC2WPMapping> mappingList = new ArrayList<FC2WPMapping>();
    mappingList.add(mappingToUpdate);
    servClient.updateFC2WPMapping(mappingList);

    FC2WPMappingWithDetails mappingByVersionUpd = servClient.getFC2WPMappingById(T03_FCWP_MAP_ID);
    logOutput("Mapping after update(A)", mappingByVersionUpd);
    FC2WPMapping mappingUpd = mappingByVersionUpd.getFc2wpMappingMap().values().iterator().next();

    // Revert the changes
    copyMappingDetails(mappingUpd, mtuBackup);
    List<FC2WPMapping> mappingListUpd = new ArrayList<FC2WPMapping>();
    mappingListUpd.add(mappingUpd);
    servClient.updateFC2WPMapping(mappingListUpd);

    FC2WPMappingWithDetails mappingByVersionUpd2 = servClient.getFC2WPMappingById(T03_FCWP_MAP_ID);
    logOutput("Mapping after revert(A)", mappingByVersionUpd2);
    assertTrue(mappingByVersionUpd2.getFc2wpMappingMap().size() > 0);

  }

  /**
   * Find FC2WP mappings for the given PIDC A2L file
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void testFindByPidcA2lId() throws ApicWebServiceException {

    FC2WPMappingServiceClient servClient = new FC2WPMappingServiceClient();

    FC2WPMappingWithDetails mappingByVersion = servClient.findByPidcA2lId(T04_PIDC_A2L_ID);
    assertNotNull(DATA_NULL_MSG, mappingByVersion);
    testOutput(mappingByVersion);

    logOutput("Mapping retrieved by PIDC A2L ID key", mappingByVersion);

  }

  /**
   * Find FC2WP mappings for the given A2L file
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void testFindByA2lId() throws ApicWebServiceException {

    FC2WPMappingServiceClient servClient = new FC2WPMappingServiceClient();

    FC2WPMappingWithDetails mappingByVersion = servClient.findByA2lId(T05_A2L_ID, T05_DIV_VAL_ID, T05_FC2_WP_NAME);
    assertNotNull(DATA_NULL_MSG, mappingByVersion);
    testOutput(mappingByVersion);
    logOutput(MAPPINGMSG, mappingByVersion);

  }

  /**
   * Test Fetch FC2WPVersMapping relevant for questionnaire for a given division Id and list of functions
   *
   * @throws ApicWebServiceException service error
   */
  @Test
  public void testFC2WPMappingByDivId() throws ApicWebServiceException {

    FC2WPMappingServiceClient servClient = new FC2WPMappingServiceClient();

    RvwFuncDetails rvwFuncDetails = new RvwFuncDetails();
    rvwFuncDetails.setDivId(T06_DIV_VAL_ID);
    rvwFuncDetails.getFuncSet().add("AADPD");
    rvwFuncDetails.getFuncSet().add("A2MTORQ");
    rvwFuncDetails.getFuncSet().add("ComRx_VM2EDC_FPT");


    FC2WPMappingWithDetails mappingByVersion = servClient.getQFC2WPMappingByDivId(rvwFuncDetails);
    assertNotNull(DATA_NULL_MSG, mappingByVersion);
    testOutput(mappingByVersion);
    logOutput(MAPPINGMSG, mappingByVersion);

  }


  /**
   * @param copyTo
   * @param from
   */
  private void copyMappingDetails(final FC2WPMapping copyTo, final FC2WPMapping from) {
    copyTo.setAgreeWithCoc(from.isAgreeWithCoc());
    copyTo.setAgreeWithCocDate(from.getAgreeWithCocDate());
    copyTo.setAgreeWithCocRespUserId(from.getAgreeWithCocRespUserId());
    copyTo.setComments(from.getComments());
    copyTo.setContactPersonId(from.getContactPersonId());
    copyTo.setContactPersonSecondId(from.getContactPersonSecondId());
    copyTo.setDeleted(from.isDeleted());
    copyTo.setUsedInIcdm(from.isUsedInIcdm());
    copyTo.setFcInSdom(from.isFcInSdom());
    copyTo.setUseWpDef(from.isUseWpDef());
    copyTo.setWpDivId(from.getWpDivId());
  }


  /**
   * Test output
   *
   * @param reportData output object
   */
  private void testOutput(final FC2WPMappingWithDetails mappingByVersion) {

    assertNotNull("Mappings not null", mappingByVersion.getFc2wpMappingMap());
    assertFalse("Mappings not empty", mappingByVersion.getFc2wpMappingMap().isEmpty());

    assertNotNull("BC details not null", mappingByVersion.getBcMap());
    assertFalse("BC details details not empty", mappingByVersion.getBcMap().isEmpty());

    assertNotNull("PT Type details not null", mappingByVersion.getPtTypeMap());
    assertFalse("PT Type details not empty", mappingByVersion.getPtTypeMap().isEmpty());


    assertNotNull("WP Div details not null", mappingByVersion.getWpDetMap());
    assertFalse("WP Div details not empty", mappingByVersion.getWpDetMap().isEmpty());
  }

  /**
   * Log output
   *
   * @param reportData output object
   */
  private void logOutput(final String opName, final FC2WPMappingWithDetails mappingByVersion) {

    LOG.info("-----------------Log output : {}", opName);

    LOG.info("Mappings = {}", mappingByVersion.getFc2wpMappingMap().size());
    LOG.info("BC details = {}", mappingByVersion.getBcMap().size());
    LOG.info("PT Type details = {}", mappingByVersion.getPtTypeMap().size());
    LOG.info("WP Div details = {}", mappingByVersion.getWpDetMap().size());

    if (!mappingByVersion.getFc2wpMappingMap().isEmpty()) {
      FC2WPMapping mapping = mappingByVersion.getFc2wpMappingMap().entrySet().iterator().next().getValue();
      LOG.info("First Mapping : {}", mapping);
    }

    if (!mappingByVersion.getBcMap().isEmpty()) {
      BaseComponent bc = mappingByVersion.getBcMap().entrySet().iterator().next().getValue();
      LOG.info("First BC : {}", bc);
    }

    if (!mappingByVersion.getPtTypeMap().isEmpty()) {
      PTType ptType = mappingByVersion.getPtTypeMap().entrySet().iterator().next().getValue();
      LOG.info("First PT Type : {}", ptType);
    }

    if (!mappingByVersion.getWpDetMap().isEmpty()) {
      WorkPackageDivision wpDiv = mappingByVersion.getWpDetMap().entrySet().iterator().next().getValue();
      LOG.info("First WP Division : {}", wpDiv);
    }
  }
}
