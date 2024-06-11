/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import com.bosch.caltool.icdm.model.wp.WorkPkg;
import com.bosch.caltool.icdm.model.wp.WorkPkgInput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class WorkPackageServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static String JUNIT_PFX = "Junit_" + getRunId() + " ";
  private final static Long WP_ID = 797213265L;
  private final static Long QNAIRE_ID = 908612498L;
  private final static Long INVALID_WP_ID = -100L;


  /**
   * Test method for {@link WorkPackageServiceClient#findById(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testFindById() throws ApicWebServiceException {
    WorkPackageServiceClient servClient = new WorkPackageServiceClient();
    WorkPkg ret = servClient.findById(WP_ID);
    assertNotNull("Response should not be null", ret);
    testOutput("testFindById", ret);
  }

  /**
   * Negative Test method for {@link WorkPackageServiceClient#findById(Long)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testFindByIdNegative() throws ApicWebServiceException {
    WorkPackageServiceClient servClient = new WorkPackageServiceClient();
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("Work Package with ID '" + INVALID_WP_ID + "' not found");
    servClient.findById(INVALID_WP_ID);
    fail("Expected exception not thrown");
  }


  /**
   * Test method for {@link WorkPackageServiceClient#create(WorkPkg)} and
   * {@link WorkPackageServiceClient#update(WorkPkg)}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testCreateNUpdate() throws ApicWebServiceException {
    WorkPackageServiceClient servClient = new WorkPackageServiceClient();
    WorkPkg wpkg = new WorkPkg();

    wpkg.setWpNameEng(JUNIT_PFX + "eng");
    wpkg.setWpNameGer(JUNIT_PFX + "ger");
    wpkg.setWpDescEng(JUNIT_PFX + "desc eng");
    wpkg.setWpDescGer(JUNIT_PFX + "desc ger");

    WorkPkg creWpkg = servClient.create(wpkg);
    testOutput("WorkPkg created", creWpkg);
    assertEquals("Version initialized ", Long.valueOf(1L), creWpkg.getVersion());

    creWpkg.setWpNameEng(JUNIT_PFX + "eng upd");
    creWpkg.setWpNameGer(JUNIT_PFX + "ger upd");
    creWpkg.setWpDescEng(JUNIT_PFX + "desc eng upd");
    creWpkg.setWpDescGer(JUNIT_PFX + "desc ger upd");

    creWpkg.setDeleted(true);

    WorkPkg upd1Wpkg = servClient.update(creWpkg);
    assertEquals("Version incremented ", Long.valueOf(creWpkg.getVersion() + 1), upd1Wpkg.getVersion());
    testOutput("updated 1", upd1Wpkg);
  }

  /**
   * Test method for {@link WorkPackageServiceClient#findAll()}.
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testFindAll() throws ApicWebServiceException {
    WorkPackageServiceClient servClient = new WorkPackageServiceClient();
    Set<WorkPkg> allSet = servClient.findAll();
    assertFalse("Response should not be empty", (allSet == null) || allSet.isEmpty());
    LOG.info("Response size = {}", allSet.size());

    testOutput("Test First Work Package", allSet.iterator().next());
  }

  /**
   * Test output
   *
   * @param testObjName testObjName
   * @param wpkg workpackage
   */
  public static void testOutput(final String testObjName, final WorkPkg wpkg) {
    LOG.info(testObjName + ": WorkPkg = " + wpkg.getName() + "; " + wpkg);
    assertNotNull(testObjName + ": object not null", wpkg);
    assertFalse(testObjName + ": Name not empty", (wpkg.getName() == null) || wpkg.getName().isEmpty());
  }

  /**
   * Test method for {@link WorkPackageServiceClient#getWorkRespMap(WorkPkgInput)}
   *
   * @throws ApicWebServiceException web servcie error
   */
  @Test
  public void testGetWorkRespMap() throws ApicWebServiceException {

    WorkPackageServiceClient servClient = new WorkPackageServiceClient();
    WorkPkgInput workPkgInput = new WorkPkgInput();
    workPkgInput.setDivValId(787372417l);
    WorkPkg workPkg1 = new WorkPkg();
    workPkg1.setId(789118515l);
    workPkg1.setName("10) Component Protection");
    WorkPkg workPkg2 = new WorkPkg();
    workPkg2.setId(789117565l);
    workPkg2.setName("100) Diagnosis Piston Cooling");
    SortedSet<WorkPkg> workPkgSet = new TreeSet<>();
    workPkgSet.add(workPkg1);
    workPkgSet.add(workPkg2);
    workPkgInput.setWorkPkgSet(workPkgSet);

    Map<Long, String> responseMap = servClient.getWorkRespMap(workPkgInput);

    assertFalse("Response should not be empty", (responseMap == null) || responseMap.isEmpty());
    LOG.info("Response size = {}", responseMap.size());
  }


  /**
   * To fetch the map of qnaire id and workpkg name for a set of qnaire ids
   * {@link WorkPackageServiceClient#getWorkPkgNameUsingQnaireIDSet(Set)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetWorkPkgNameUsingQnaireIdSet() throws ApicWebServiceException {
    Set<Long> qnaireIds = new HashSet<>();
    qnaireIds.add(795414163l);
    qnaireIds.add(795146697l);
    Map<Long, String> workPkgNameMap = new WorkPackageServiceClient().getWorkPkgNameUsingQnaireIDSet(qnaireIds);
    assertFalse("Response should not be null or empty", (workPkgNameMap == null) || workPkgNameMap.isEmpty());
    LOG.info("Size : {}", workPkgNameMap.size());
  }

  /**
   * Negative Test with an empty set {@link WorkPackageServiceClient#getWorkPkgNameUsingQnaireIDSet(Set)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetWorkPkgNameUsingQnaireIdSetNegative() throws ApicWebServiceException {
    Set<Long> qnaireIds = new HashSet<>();
    Map<Long, String> workPkgNameMap = new WorkPackageServiceClient().getWorkPkgNameUsingQnaireIDSet(qnaireIds);
    assertTrue("Response should be empty", workPkgNameMap.isEmpty());
    LOG.info("Size : {}", workPkgNameMap.size());// no workpackages returned
  }


  /**
   * Test method for {@link WorkPackageServiceClient#getWorkPkgbyQnaireID(Long)}
   *
   * @throws ApicWebServiceException web service error
   */
  @Test
  public void testGetWorkPkgbyQnaireID() throws ApicWebServiceException {
    WorkPackageServiceClient servClient = new WorkPackageServiceClient();

    WorkPkg workPkg = servClient.getWorkPkgbyQnaireID(QNAIRE_ID);
    assertNotNull("Response should not be null", workPkg);
    testWorkPkg(workPkg);
  }


  /**
   * @param workPkg
   */
  private void testWorkPkg(final WorkPkg workPkg) {
    assertEquals("Wp_Group is equal", "Diagnose 2-Punkt Lambdasonde", workPkg.getWpNameEng());
  }
}
