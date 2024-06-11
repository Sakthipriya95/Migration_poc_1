/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.bo.test.AbstractIcdmBOTest;
import com.bosch.caltool.icdm.common.exception.IcdmException;


/**
 * @author bne4cob
 */
public class ProjectAttributeLoaderTest extends AbstractIcdmBOTest {

  private static final Long PIDC_VERS_ID = 1423099081L;
  private static final Long PIDC_ID = 1423099079L;

  /**
   * Test method for {@link ProjectAttributeLoader#createModel(java.lang.Long)}.
   * 
   * @throws IcdmException error from loader
   */
  @Test
  public void testCreateModelLong() throws IcdmException {
    ProjectAttributeLoader loader = new ProjectAttributeLoader(getServiceData());
    PidcVersionAttributeModel model = loader.createModel(PIDC_VERS_ID);
    assertEquals("Check PIDC Version ID", PIDC_VERS_ID, model.getPidcVersion().getId());
  }

  /**
   * Test method for {@link ProjectAttributeLoader#createModel(java.lang.Long, LOAD_LEVEL)}.
   *
   * @throws IcdmException error from loader
   */
  @Test
  public void testCreateModelLongLoadLevel() throws IcdmException {
    ProjectAttributeLoader loader = new ProjectAttributeLoader(getServiceData());
    PidcVersionAttributeModel model = loader.createModel(PIDC_VERS_ID, LOAD_LEVEL.L6_ALL);
    assertEquals("Check PIDC Version ID", PIDC_VERS_ID, model.getPidcVersion().getId());
  }

  /**
   * Test method for {@link ProjectAttributeLoader#createModel(java.lang.Long, LOAD_LEVEL, boolean)}.
   *
   * @throws IcdmException error from loader
   */
  @Test
  public void testCreateModelLongLoadLevelBoolean() throws IcdmException {
    ProjectAttributeLoader loader = new ProjectAttributeLoader(getServiceData());
    PidcVersionAttributeModel model = loader.createModel(PIDC_VERS_ID, LOAD_LEVEL.L6_ALL, true);
    assertEquals("Check PIDC Version ID", PIDC_VERS_ID, model.getPidcVersion().getId());
  }

  /**
   * Test method for {@link ProjectAttributeLoader#createModelByPidc(java.lang.Long, java.lang.Long)}.
   *
   * @throws IcdmException error from loader
   */
  @Test
  public void testCreateModelByPidc1() throws IcdmException {
    ProjectAttributeLoader loader = new ProjectAttributeLoader(getServiceData());
    PidcVersionAttributeModel model = loader.createModelByPidc(null, PIDC_VERS_ID);
    assertEquals("Check PIDC Version ID", PIDC_VERS_ID, model.getPidcVersion().getId());
  }

  /**
   * Test method for {@link ProjectAttributeLoader#createModelByPidc(java.lang.Long, java.lang.Long)}.
   *
   * @throws IcdmException error from loader
   */
  @Test
  public void testCreateModelByPidc2() throws IcdmException {
    ProjectAttributeLoader loader = new ProjectAttributeLoader(getServiceData());
    PidcVersionAttributeModel model = loader.createModelByPidc(PIDC_ID, null);
    assertEquals("Check PIDC Version ID", PIDC_VERS_ID, model.getPidcVersion().getId());
  }

  /**
   * Test method for {@link ProjectAttributeLoader#createModelByPidc(java.lang.Long, java.lang.Long)}.
   *
   * @throws IcdmException error from loader
   */
  @Test
  public void testCreateModelByPidcNegative() throws IcdmException {
    ProjectAttributeLoader loader = new ProjectAttributeLoader(getServiceData());
    this.thrown.expectMessage("Either PIDC ID or PIDC Version ID should be passed as input");
    PidcVersionAttributeModel model = loader.createModelByPidc(null, null);
    assertEquals("Check PIDC Version ID", PIDC_VERS_ID, model.getPidcVersion().getId());
  }

}
