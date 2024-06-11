/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class FC2WPVersionServiceClientTest extends AbstractRestClientTest {

  private static final String FC2WP_NAME = "BEG specific FC2WP";
  private static final long FC2WP_DIV_VALUE_ID = 787372419L;
  private static final long FC2WP_DEF_ID = 926369965L;
  // private static final long INVALID_ID = -1;

  /**
   * Test retrieval of all versions for the given defintion
   *
   * @throws ApicWebServiceException web error
   */
  @Test
  public void testgetVersionsByDefID() throws ApicWebServiceException {
    FC2WPVersionServiceClient servClient = new FC2WPVersionServiceClient();
    Set<FC2WPVersion> allVersSet = servClient.getVersionsByDefID(FC2WP_DEF_ID);
    boolean versAvailable = false;
    for (FC2WPVersion vers : allVersSet) {
      if (vers.getId().equals(926370065L)) {
        versAvailable = true;
        testOutput(vers);
        break;
      }
    }
    assertTrue(versAvailable);
  }

  /**
   * Test retrieval of version by id
   *
   * @throws ApicWebServiceException web error
   */
  @Test
  public void testgetWorkingSetVersionByDefID() throws ApicWebServiceException {
    FC2WPVersionServiceClient servClient = new FC2WPVersionServiceClient();
    FC2WPVersion wsVers = servClient.getWorkingSetVersionByDefID(FC2WP_DEF_ID);
    assertEquals("CreatedUser is equal", "IMI2SI", wsVers.getCreatedUser());
    testOutput(wsVers);
  }

  /**
   * @throws ApicWebServiceException web error
   */
  @Test
  public void testgetActiveVersionByValueID() throws ApicWebServiceException {
    FC2WPVersionServiceClient servClient = new FC2WPVersionServiceClient();
    FC2WPVersion actVers = servClient.getActiveVersionByValueID(FC2WP_NAME, FC2WP_DIV_VALUE_ID);
    if (actVers.isActive() == false) {
      assertEquals("CreatedUser is equal", "IMI2SI", actVers.getCreatedUser());
      testOutput(actVers);
    }
  }

  /**
   * Finding the object by primary key
   *
   * @throws ApicWebServiceException web error
   */
  @Test
  public void Testget() throws ApicWebServiceException {
    FC2WPVersionServiceClient servClient = new FC2WPVersionServiceClient();
    FC2WPVersion vers = servClient.get(926370065L);
    assertEquals("CreatedUser is equal", "IMI2SI", vers.getCreatedUser());
    testOutput(vers);
  }


  /**
   * Test output
   *
   * @param vers
   */
  public static void testOutput(final FC2WPVersion vers) {

  
    assertNotNull("CreatedDate is not null", vers.getCreatedDate());
    assertEquals("Desc_eng is equal", "BEG specific FC2WP", vers.getDescEng());
    assertEquals("FCWP_WP_ID is equal", Long.valueOf(926369965), vers.getFcwpDefId());
  }


  /**
   * @throws ApicWebServiceException web error
   */
  @Test
  public void createUpdate() throws ApicWebServiceException {
    FC2WPVersionServiceClient servClient = new FC2WPVersionServiceClient();
    Set<FC2WPVersion> allVersSet = servClient.getVersionsByDefID(FC2WP_DEF_ID);

    FC2WPVersion vers = new FC2WPVersion();
    vers.setActive(false);
    vers.setArchReleaseSdom("junit_Arch release ABC");
    vers.setDescEng("junit_Vers eng desc");
    vers.setDescGer("junit_Vers ger desc");
    vers.setFcwpDefId(FC2WP_DEF_ID);
    vers.setMajorVersNo(5L);
    vers.setMinorVersNo(Long.valueOf(allVersSet.size()));

    FC2WPVersion creVers = servClient.create(vers);
    assertEquals("setDescEng", "junit_Vers eng desc", creVers.getDescEng());
    assertEquals("setDescGer", "junit_Vers ger desc", creVers.getDescGer());
    assertEquals("setFcwpDefId is equal", Long.valueOf(FC2WP_DEF_ID), creVers.getFcwpDefId());
    assertEquals("setMajorVersNo is equal", Long.valueOf(5L), creVers.getMajorVersNo());
    assertEquals("setMinorVerNo is equal", Long.valueOf(Long.valueOf(allVersSet.size())), creVers.getMinorVersNo());

    creVers.setArchReleaseSdom("junit_Arch release ABC updated");
    creVers.setDescEng("junit_Vers eng desc updated");
    creVers.setDescGer("junit_Vers ger desc updated");
    creVers.setActive(false);

    FC2WPVersion upd1Vers = servClient.update(creVers);
    assertEquals("setDescEng", "junit_Vers eng desc updated", upd1Vers.getDescEng());
    assertEquals("setDescGer", "junit_Vers ger desc updated", upd1Vers.getDescGer());
    assertTrue("set Activity is true", upd1Vers.isActive() == false);
    assertEquals("ArchReleaseSdom", "junit_Arch release ABC updated", upd1Vers.getArchReleaseSdom());
    // Set the version created just now as active
    upd1Vers.setActive(true);
    FC2WPVersion upd2Vers = servClient.update(upd1Vers);
    assertTrue("set Activity is true", upd2Vers.isActive() == true);
  }

  /**
   * @throws ApicWebServiceException web error
   */
  @Test
  public void TestgetActiveVersionByPidcVersion() throws ApicWebServiceException {
    FC2WPVersionServiceClient servClient = new FC2WPVersionServiceClient();
    FC2WPVersion pidcVersion = servClient.getActiveVersionByPidcVersion(773517115L);
    assertEquals("CreatedUser is equal", "BNE4COB", pidcVersion.getCreatedUser());
    TestOutput(pidcVersion);

  }


  /**
   * @param validateImport
   */
  private void TestOutput(final FC2WPVersion validateImport) {
    assertEquals("setDescEng", "junit_Vers eng desc updated", validateImport.getDescEng());
    assertEquals("FCWP_WP_ID is equal", Long.valueOf(926369965), validateImport.getFcwpDefId());
    assertEquals("setDescGer", "junit_Vers ger desc updated", validateImport.getDescGer());
    assertNotNull("CreatedDate is not null", validateImport.getCreatedDate());
  }
}


/*
 *
*//**
    * internal server error contact icdm, when tried to execute negative case
    *
    * @throws ApicWebServiceException web error
    */
/*
 * @Test public void testgetWorkingSetVersionByDefIDNegative() throws ApicWebServiceException {
 * FC2WPVersionServiceClient servClient = new FC2WPVersionServiceClient();
 * this.thrown.expect(ApicWebServiceException.class); this.thrown.expectMessage("ID '" + INVALID_ID + "' not found");
 * servClient.getWorkingSetVersionByDefID(INVALID_ID); fail("Expected exception not thrown"); }
 *//**
    * @throws ApicWebServiceException web error
    *//*
       * @Test public void testgetActiveVersionByValueIDNegative() throws ApicWebServiceException {
       * FC2WPVersionServiceClient servClient = new FC2WPVersionServiceClient();
       * this.thrown.expect(ApicWebServiceException.class); this.thrown.expectMessage("ID '" + INVALID_ID +
       * "' not found"); servClient.getActiveVersionByValueID(INVALID_ID, FC2WP_DIV_VALUE_ID);
       * fail("Expected exception not thrown");} }
       */

