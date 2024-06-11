/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.admin.NodeAccessOutput;
import com.bosch.caltool.icdm.model.general.DataCreationModel;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UsecaseCreationData;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccessDetails;
import com.bosch.caltool.icdm.model.user.NodeAccessDetailsExt;
import com.bosch.caltool.icdm.model.user.NodeAccessInfo;
import com.bosch.caltool.icdm.model.user.NodeAccessWithUserInput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseServiceClient;


/**
 * @author bne4cob
 */
/**
 * @author say8cob
 */
public class NodeAccessServiceClientTest extends AbstractRestClientTest {


  /**
   * Test retrieval of all pidc access rights
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void test01() throws ApicWebServiceException {
    NodeAccessServiceClient servClient = new NodeAccessServiceClient();
    NodeAccessDetails ret = servClient.getNodeAccessDetailsByNode(MODEL_TYPE.PIDC, new Long[0]);
    assertNotNull("Response should not be null ", ret);
    testOutput(ret);
  }

  /**
   * Test retrieval of pidc access rights by node ID
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void test02() throws ApicWebServiceException {
    NodeAccessServiceClient servClient = new NodeAccessServiceClient();
    NodeAccessDetails ret = servClient.getNodeAccessDetailsByNode(MODEL_TYPE.PIDC, 372767L, 768620517L, 545067L);
    assertNotNull("Response should not be null ", ret);
    testOutput(ret);
  }

  /**
   * Test retrieval of pidc access rights by node ID .Negative test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetNodeAccessDetailsByNodeNegative() throws ApicWebServiceException {
    NodeAccessServiceClient servClient = new NodeAccessServiceClient();
    NodeAccessDetails ret = servClient.getNodeAccessDetailsByNode(MODEL_TYPE.PIDC, -2L);
    assertTrue("Response should be null ", ret.getNodeAccessMap().isEmpty());
  }

  /**
   * Test to getAllSpecialNodeAccess
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetAllSpecialNodeAccess() throws ApicWebServiceException {
    Map<Long, NodeAccessInfo> allSpecialNodeAccess = new NodeAccessServiceClient().getAllSpecialNodeAccess();
    assertNotNull("Response should not be null ", allSpecialNodeAccess);
    assertTrue(allSpecialNodeAccess.size() >= 1);
  }

  /**
   * Test retrieval of all pidc access rights - extended details
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void test03() throws ApicWebServiceException {
    NodeAccessServiceClient servClient = new NodeAccessServiceClient();
    NodeAccessDetailsExt ret = servClient.getNodeAccessDetailsByNodeExt(MODEL_TYPE.PIDC, null, null, new HashSet<>());
    assertNotNull("Response should not be null ", ret);
    testOutputExt(ret);
  }

  /**
   * Test retrieval of all pidc access rights - extended details
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testNodeAccessForUser() throws ApicWebServiceException {
    NodeAccessServiceClient servClient = new NodeAccessServiceClient();
    Map<Long, NodeAccessInfo> ret = servClient.findNodeAccessForGivenUser("SAY8COB");
    assertNotNull("Response should not be null ", ret);
    assertTrue(ret.size() >= 1);
  }


  /**
   * Test retrieval of all pidc access rights - extended details
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testNodeAccesInfo() throws ApicWebServiceException {
    NodeAccessServiceClient servClient = new NodeAccessServiceClient();
    Map<Long, NodeAccessInfo> ret =
        servClient.findNodeAccessForUserAndNodeNames(MODEL_TYPE.CDR_FUNCTION.getTypeCode(), "RGO7COB", "*DFES*");
    assertNotNull("Response should not be null ", ret);
    assertTrue(ret.size() >= 1);
    Map<Long, NodeAccessInfo> ret1 =
        servClient.findNodeAccessForUserAndNodeNames(MODEL_TYPE.CDR_RULE_SET.getTypeCode(), "RGO7COB", "Air");
    assertNotNull("Response should not be null ", ret1);
    assertTrue(ret1.size() >= 1);
    Map<Long, NodeAccessInfo> ret2 =
        servClient.findNodeAccessForUserAndNodeNames(MODEL_TYPE.QUESTIONNAIRE.getTypeCode(), "RGO7COB", "VehM");
    assertNotNull("Response should not be null ", ret2);
    assertTrue(ret2.size() >= 1);
    Map<Long, NodeAccessInfo> ret3 =
        servClient.findNodeAccessForUserAndNodeNames(MODEL_TYPE.PIDC.getTypeCode(), "RGO7COB", "rg");
    assertNotNull("Response should not be null ", ret3);
    assertTrue(ret3.size() >= 1);


  }

  /**
   * @throws ApicWebServiceException ApicWebServiceException
   */
  @Test
  public void testUsecase() throws ApicWebServiceException {
    NodeAccessServiceClient servClient = new NodeAccessServiceClient();
    Map<Long, NodeAccessInfo> ret4 =
        servClient.findNodeAccessForUserAndNodeNames(MODEL_TYPE.USE_CASE.getTypeCode(), "RGO7COB", "uc1");
    assertNotNull("Response should not be null ", ret4);
    assertTrue(ret4.size() >= 1);
  }

  /**
   * Test retrieval of all pidc owner access rights - extended details
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void test04() throws ApicWebServiceException {
    NodeAccessServiceClient servClient = new NodeAccessServiceClient();
    NodeAccessDetailsExt ret =
        servClient.getNodeAccessDetailsByNodeExt(MODEL_TYPE.PIDC, null, "OWNER", new HashSet<>());
    assertNotNull("Response should not be null ", ret);
    testOutputExt(ret);
  }

  /**
   * Test retrieval of all pidc write access rights of a given user - extended details
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void test05() throws ApicWebServiceException {
    NodeAccessServiceClient servClient = new NodeAccessServiceClient();
    NodeAccessDetailsExt ret =
        servClient.getNodeAccessDetailsByNodeExt(MODEL_TYPE.PIDC, "BNE4COB", "WRITE", new HashSet<>());

    assertNotNull("Response should not be null ", ret);

    testOutputExt(ret);
  }

  /**
   * Test retrieval of all pidc write access rights of a given user and set of node ids- extended details
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void test06() throws ApicWebServiceException {
    NodeAccessServiceClient servClient = new NodeAccessServiceClient();
    NodeAccessDetailsExt ret = servClient.getNodeAccessDetailsByNodeExt(MODEL_TYPE.PIDC, "BNE4COB", "WRITE",
        new HashSet<>(Arrays.asList(1468289579L, 1507932229L, 1523381529L)));
    assertNotNull("Response should not be null ", ret);
    testOutputExt(ret);
  }

  /**
   * Test retrieval of all pidc access rights - extended details
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testNodeAccessForFunFile() throws ApicWebServiceException {
    NodeAccessServiceClient servClient = new NodeAccessServiceClient();
    NodeAccessOutput nodeAccessOutput = servClient.findNodeAccessForFunFile("testdata/nodeaccess/FunNodeAccess.fun");
    assertNotNull("Response should not be null ", nodeAccessOutput.getNodeAccessInfoMap());
    assertTrue(nodeAccessOutput.getNodeAccessInfoMap().size() >= 1);
  }

  /**
   * Test retrieval of all pidc owner access rights - extended details.Negative test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetNodeAccessDetailsByNodeExtNegative() throws ApicWebServiceException {
    NodeAccessServiceClient servClient = new NodeAccessServiceClient();
    NodeAccessDetailsExt ret = servClient.getNodeAccessDetailsByNodeExt(MODEL_TYPE.FC2WP_DEF, null, "OWNER",
        new HashSet<>(Arrays.asList(-8l)));
    assertTrue("Response should be null ", (ret.getNodeAccessMap().isEmpty()));
  }

  /**
   * Test retrieval of all pidc access rights of current user
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetAllNodeAccessforCurrentUser() throws ApicWebServiceException {
    NodeAccessServiceClient servClient = new NodeAccessServiceClient();
    Map<Long, NodeAccess> retMap = servClient.getAllNodeAccessforCurrentUser();
    assertFalse("Response should not be null ", retMap.isEmpty());
    NodeAccess nodeAccess = retMap.get(2747L);
    assertEquals("Node access id is equal", Long.valueOf(235366L), nodeAccess.getId());
    assertEquals("Node type is equal", "PIDC", nodeAccess.getNodeType());
  }


  /**
   * Test create , update and delete the nodeAccess
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testCreateUpdateDelete() throws ApicWebServiceException {
    NodeAccessServiceClient servClient = new NodeAccessServiceClient();
    NodeAccess access = new NodeAccess();
    access.setNodeId(1677573854L);
    access.setNodeType("PIDC");
    access.setUserId(230016L);
    access.setRead(true);
    // create node access
    NodeAccess createdAccess = servClient.create(access);
    assertNotNull("Response should not be null ", createdAccess);
    // validate
    assertEquals("Node id is equal", Long.valueOf(1677573854), createdAccess.getNodeId());
    assertEquals("Node type is equal", "PIDC", createdAccess.getNodeType());
    assertEquals("User id is equal", Long.valueOf(230016), createdAccess.getUserId());
    assertTrue("Read access is present", createdAccess.isRead());
    assertEquals("version is equal", Long.valueOf(1L), createdAccess.getVersion());
    // update node access
    createdAccess.setWrite(true);
    NodeAccess updatedAccess = servClient.update(createdAccess);
    // validate
    Long createdVersion = createdAccess.getVersion();
    assertNotNull("Response should not be null ", updatedAccess);
    assertTrue("Write access is present", updatedAccess.isWrite());
    assertEquals("version is equal", Long.valueOf(createdVersion + 1), updatedAccess.getVersion());
    // delete node access
    servClient.delete(updatedAccess);
  }


  /**
   * Test create , update and delete the nodeAccess
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testmultinodeAccessMgmt() throws ApicWebServiceException {

    UseCaseServiceClient client = new UseCaseServiceClient();

    UseCase useCase1 = new UseCase();
    useCase1.setGroupId(772259417L);
    useCase1.setNameEng("Junit_" + getRunId() + " testCreateUpdateUseCase");
    useCase1.setNameGer("Junit 1 _" + getRunId() + " testCreateUpdateUseCase");
    useCase1.setDescEng("Junit 1");
    useCase1.setDescGer("Junit 1");


    UsecaseCreationData ucCreationData = new UsecaseCreationData();
    ucCreationData.setUsecase(useCase1);
    ucCreationData.setOwnerId(233516L);


    DataCreationModel<UseCase> createdObj1 = client.create(ucCreationData);


    UseCase useCase2 = new UseCase();
    useCase2.setGroupId(772259417L);
    useCase2.setNameEng("Junit_" + getRunId() + 1 + " testCreateUpdateUseCase");
    useCase2.setNameGer("Junit 1 _" + getRunId() + 1 + " testCreateUpdateUseCase");
    useCase2.setDescEng("Junit 1");
    useCase2.setDescGer("Junit 1");
    UsecaseCreationData ucCreationData2 = new UsecaseCreationData();
    ucCreationData2.setUsecase(useCase2);
    ucCreationData2.setOwnerId(937778116L);
    DataCreationModel<UseCase> createdObj2 = client.create(ucCreationData2);


    NodeAccessServiceClient servClient = new NodeAccessServiceClient();

    List<NodeAccessWithUserInput> nodeAccessInputList = new ArrayList<>();
    // create node access for Use cases
    NodeAccessWithUserInput input = new NodeAccessWithUserInput();
    NodeAccess access = new NodeAccess();
    access.setNodeId(createdObj1.getNodeAccess().getNodeId());
    access.setNodeType("USECASE");
    access.setUserId(937778116L);
    access.setRead(true);
    access.setGrant(true);
    access.setWrite(true);
    access.setOwner(true);

    input.setNodeAccess(access);

    input.setDelete(false);
    nodeAccessInputList.add(input);

    NodeAccessWithUserInput input2 = new NodeAccessWithUserInput();
    NodeAccess access2 = new NodeAccess();
    access2.setNodeId(createdObj2.getNodeAccess().getNodeId());
    access2.setNodeType("USECASE");
    access2.setUserId(233516L);
    access2.setRead(true);
    access2.setGrant(false);
    access2.setWrite(true);
    access2.setOwner(false);
    input2.setNodeAccess(access2);

    input2.setDelete(false);
    nodeAccessInputList.add(input2);


    Map<Long, NodeAccess> manageMultiaccess = servClient.createUpdateMultiNode(nodeAccessInputList);

    for (NodeAccess createdNodeAccess : manageMultiaccess.values()) {
      assertTrue("New node access id is not null", createdNodeAccess.getId() != null);

    }

    // update Node access
    assertNotNull("Response should not be null ", manageMultiaccess);
    nodeAccessInputList = new ArrayList<>();
    for (NodeAccess nodeAccessWithUserInput : manageMultiaccess.values()) {
      NodeAccessWithUserInput input3 = new NodeAccessWithUserInput();
      NodeAccess newNodeAccess = nodeAccessWithUserInput.clone();
      newNodeAccess.setWrite(false);
      input3.setNodeAccess(newNodeAccess);
      nodeAccessInputList.add(input3);
    }
    // delete node access
    manageMultiaccess = servClient.createUpdateMultiNode(nodeAccessInputList);
    for (NodeAccess updatedNodeAccess : manageMultiaccess.values()) {
      assertFalse("Node access Write is false", updatedNodeAccess.isWrite());
    }


    nodeAccessInputList = new ArrayList<>();
    for (NodeAccess nodeAccessWithUserInput : manageMultiaccess.values()) {
      NodeAccessWithUserInput input3 = new NodeAccessWithUserInput();
      NodeAccess newNodeAccess = nodeAccessWithUserInput.clone();
      input3.setNodeAccess(newNodeAccess);
      input3.setDelete(true);
      nodeAccessInputList.add(input3);
    }

    manageMultiaccess = servClient.createUpdateMultiNode(nodeAccessInputList);
    assertNotNull("Deleted Node access is not null", manageMultiaccess);

  }

  /**
   * Test retrieval of all pidc access rights of current user
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetObjectIdentifier() throws ApicWebServiceException {
    NodeAccessServiceClient servClient = new NodeAccessServiceClient();
    Map<Long, NodeAccess> retMap = servClient.getAllNodeAccessforCurrentUser();
    assertFalse("Response should not be null ", retMap.isEmpty());
    NodeAccess nodeAccess = retMap.get(2747L);
    String ret = servClient.getObjectIdentifier(nodeAccess);
    assertNotNull("Response should not be null ", ret);
    LOG.info("{}", ret);
  }

  /**
   * Test retrieval of all pidc access rights of current user
   *
   * @throws ApicWebServiceException exception
   * @throws NullPointerException exc
   */
  @Test
  public void testGetObjectIdentifierNegative() throws ApicWebServiceException, NullPointerException {
    NodeAccessServiceClient servClient = new NodeAccessServiceClient();
    this.thrown.expect(NullPointerException.class);
    servClient.getObjectIdentifier(null);
    fail("Expected exception not thrown");
  }

  /**
   * Test retrieval of all pidc access rights
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetAdGroupDetails() throws ApicWebServiceException {
    NodeAccessServiceClient servClient = new NodeAccessServiceClient();
    Set<String> ntUserSet = new HashSet<>();
    Set<Long> pidcIdSet = new HashSet<>();
    NodeAccessDetails retAll = servClient.getAdGroupDetails("PIDC", ntUserSet, pidcIdSet);
    assertNotNull("Response should not be null ", retAll);

    pidcIdSet.add(31193082179L);
    NodeAccessDetails retWithPidc = servClient.getAdGroupDetails("PIDC", ntUserSet, pidcIdSet);
    assertNotNull("Response should not be null ", retWithPidc);

    ntUserSet.add("msp5cob");
    NodeAccessDetails retWithNt = servClient.getAdGroupDetails("PIDC", ntUserSet, pidcIdSet);
    assertNotNull("Response should not be null ", retWithNt);


  }


  /**
   * Test output
   *
   * @param reportData output object
   */
  private void testOutput(final NodeAccessDetails nodeAccDet) {

    NodeAccess access = nodeAccDet.getNodeAccessMap().values().iterator().next().iterator().next();

    LOG.info("Node ID = " + access.getNodeId());
    LOG.info("User Name = " + nodeAccDet.getUserMap().get(access.getUserId()).getName());
    assertFalse("node access details not empty",
        (nodeAccDet.getNodeAccessMap() == null) || (nodeAccDet.getUserMap() == null));

  }

  /**
   * Test output
   *
   * @param reportData output object
   */
  private void testOutputExt(final NodeAccessDetailsExt nodeAccDet) {

    testOutput(nodeAccDet);

    assertFalse("user info details not empty",
        (nodeAccDet.getNodeAccessMap() == null) || (nodeAccDet.getUserMap() == null));

    LOG.info("User Info display name = " + nodeAccDet.getUserInfoMap().values().iterator().next().getDisplayName());

  }


}
