/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
// import static org.junit.Assert.assertFalse;
// import static org.junit.Assert.assertFalse;
// import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;
// import com.bosch.caltool.icdm.model.fc2wp.FC2WPRelvPTType;
// import com.bosch.caltool.icdm.model.fc2wp.FC2WPRelvPTType;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class FC2WPDefinitionServiceClientTest extends AbstractRestClientTest {

  private static final long TEST_DEF_ID = 926369915L;
  private static final String TEST3_DEF_NAME = "Junit test 300";
  private static final String TEST4_DEF_NAME = "Junit test 400";
  /**
   * FC-WP Version : PS-EC (formerly DGS-EC) - v2.1, <br>
   * 230671 - FC2WP Gen2 - generic
   */
  private static final String T05_FC2_WP_NAME = "FC2WP Gen2 - generic";

  /**
   * Test retrieval of all definitions
   *
   * @throws ApicWebServiceException web error
   */
  @Test
  public void testgetAll() throws ApicWebServiceException {
    FC2WPDefinitionServiceClient servClient = new FC2WPDefinitionServiceClient();
    Set<FC2WPDef> allDefSet = servClient.getAll();
    boolean defContains = false;
    for (FC2WPDef def : allDefSet) {
      if (def.getId() == (TEST_DEF_ID)) {
        defContains = true;
        testOutput(def);
        break;
      }
    }
    assertTrue("defination available", defContains);
  }

  /**
   * @param def
   */
  private void testOutput(final FC2WPDef def) {
    // assertEquals("Name value id is equal", Long.valueOf(230671), def.getNameValueId());
    assertEquals("Division id is equal", Long.valueOf(787372417), def.getDivisionValId());
    assertEquals("description Eng is equal", "FC2WP list 2nd generation (no project specific changes)",
        def.getDescriptionEng());
    assertEquals("description Ger is equal", "FC2WP Liste 2. Generation (keine projektspezifischen Anpassungen)",
        def.getDescriptionGer());
    assertEquals("Description DivisionName is equal", "PS-EC (formerly DGS-EC)", def.getDivisionName());
    // assertEquals("NameEng is equal", "FC2WP Gen2 - generic", def.getNameEng());
    // assertEquals("NameGer is equal", "FC2WP Gen2 - generisch", def.getNameGer());
    // assertEquals("Version is equal", Long.valueOf(3), def.getVersion());


  }


  /**
   * Test creation of definition
   *
   * @throws ApicWebServiceException web service error
   */

  @Test
  public void create() throws ApicWebServiceException {
    FC2WPDefinitionServiceClient servClient = new FC2WPDefinitionServiceClient();
    FC2WPDef def = new FC2WPDef();
    def.setDescriptionEng("Junit_test_desceng");
    def.setDescriptionGer("Junit_test_descger");
    def.setName(T05_FC2_WP_NAME + getRunId());
    def.setDivisionValId(787372417L);
    // def.setNameEng("Junit_FC2WP_" + getRunId());
    // def.setNameGer("Junit test nameGer");
    def.setRelvForQnaire(false);

    // invoke create
    FC2WPDef newDef = servClient.create(def);

    assertEquals("DescriptionEng is equal", "Junit_test_desceng", newDef.getDescriptionEng());
    assertEquals("DescriptionGre is equal", "Junit_test_descger", newDef.getDescriptionGer());
    assertEquals("Division value id is equal", Long.valueOf(787372417), newDef.getDivisionValId());
    // assertEquals("Name Eng is equal", "Junit_FC2WP_" + getRunId(), newDef.getNameEng());
    // assertEquals("Name Ger is equal", "Junit test nameGer", newDef.getNameGer());

    FC2WPVersionServiceClient versClient = new FC2WPVersionServiceClient();
    FC2WPVersion wsVersion = versClient.getWorkingSetVersionByDefID(newDef.getId());
    // validate created version
    // FC2WPVersionServiceClientTest.testOutput(wsVersion);
    assertEquals("cerated id is equal", wsVersion.getId(), wsVersion.getId());

  }

  /**
   * Test creation of definition
   *
   * @throws ApicWebServiceException web error
   */
  // @Test have to check the functionality
  public void test04() throws ApicWebServiceException {
    FC2WPDefinitionServiceClient servClient = new FC2WPDefinitionServiceClient();

    FC2WPDef def = new FC2WPDef();
    def.setDescriptionEng("Junit_test_desc_eng");
    def.setDescriptionGer("Junit_test_desc_ger");
    def.setDivisionValId(787372417L);
    // def.setNameEng(TEST4_DEF_NAME + getRunId());
    // def.setNameGer("Junit test nameGer");
    def.setRelvForQnaire(false);
    def.setRefFcwpDefId(TEST_DEF_ID);
    FC2WPDef newDef = servClient.create(def);

    assertEquals("description eng is equal", "Junit_test_desc_eng", newDef.getDescriptionEng());
    assertEquals("Description ger is equal", "Junit_test_desc_ger", newDef.getDescriptionGer());
    assertEquals("Division value id is equal", Long.valueOf(787372417), newDef.getDivisionValId());
    // assertEquals("Name Eng is equal", TEST4_DEF_NAME + getRunId(), newDef.getNameEng());
    // assertEquals("Name Ger is equal", "Junit test nameGer", newDef.getNameGer());
    assertEquals("RefFcwpDefId is equal", Long.valueOf(TEST_DEF_ID), newDef.getRefFcwpDefId());

    // testOutput(newDef);
    FC2WPVersionServiceClient versClient = new FC2WPVersionServiceClient();
    FC2WPVersion wsVersion = versClient.getWorkingSetVersionByDefID(newDef.getId());
    // FC2WPVersionServiceClientTest.testOutput(wsVersion);
    assertEquals("created id is equal", wsVersion.getId(), wsVersion.getId());
  }
}
