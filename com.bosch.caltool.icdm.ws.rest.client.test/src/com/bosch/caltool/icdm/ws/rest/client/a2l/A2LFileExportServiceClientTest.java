/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.A2LFileExportServiceInput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class A2LFileExportServiceClientTest extends AbstractRestClientTest {


  /**
   * vg1 variant group from a2l file 2605822770
   */
  private static final long A2L_VARIANT_GRP = 2748258527l;
  /**
   * icdm:a2lid,2605822770-202740695
   */
  private static final long PIDC_A2L_ID = 2605822770l;
  /**
   * V3 wp definition version id for the a2l
   */
  private static final long ACTIVE_WP_DEF_VER_ID = 2720874502l;

  /**
   * V3 wp definition version id for the a2l
   */
  private static final long INVALID_WP_DEF_VER_ID = 0l;

  /**
   * Invalid vg id for the a2l
   */
  private static final long INVALID_A2L_VARIANT_GRP = 0l;

  /**
   * Test case to export a2l file with default level
   *
   * @throws ApicWebServiceException as exception
   */
  @Test
  public void testExportA2lFileDeFaultLevel() throws ApicWebServiceException {

    String expA2lZipName = "009-Fiat-Fire-Mx17_3B0_Tb_S2_with_Default.a2l";


    String tempDir = System.getProperty("java.io.tmpdir") + File.separator + "A2L_EXPORT";
    String exportA2lFilePath = tempDir;


    A2LFileExportServiceInput a2lExportObj = new A2LFileExportServiceInput();
    a2lExportObj.setWpDefVersId(ACTIVE_WP_DEF_VER_ID);
    a2lExportObj.setVarGrpId(null);

    String exportA2lFile =
        new A2LFileExportServiceClient().exportA2lFile(a2lExportObj, exportA2lFilePath, expA2lZipName);
    assertTrue("Exported A2l File", new File(exportA2lFile).exists());

  }

  /**
   * Test case to export a2l with Variant Group Level
   *
   * @throws ApicWebServiceException as exception
   */
  @Test
  public void testExportA2lFileVarGrpLevel() throws ApicWebServiceException {

    String expA2lZipName = "009-Fiat-Fire-Mx17_3B0_Tb_S2_with_Variantgroups.a2l";


    String tempDir = System.getProperty("java.io.tmpdir") + File.separator + "A2L_EXPORT";
    String exportA2lFilePath = tempDir;


    A2LFileExportServiceInput a2lExportObj = new A2LFileExportServiceInput();
    a2lExportObj.setWpDefVersId(ACTIVE_WP_DEF_VER_ID);
    a2lExportObj.setVarGrpId(A2L_VARIANT_GRP);

    String exportA2lFile =
        new A2LFileExportServiceClient().exportA2lFile(a2lExportObj, exportA2lFilePath, expA2lZipName);
    assertTrue("Exported A2l File", new File(exportA2lFile).exists());
  }


  /**
   * Negative Testcase with invalid wp definition id
   *
   * @throws ApicWebServiceException as exception
   */
  @Test
  public void testExportA2lFileNegative1() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(startsWith("A2l WP Definition Version with ID"));

    String expA2lZipName = "009-Fiat-Fire-Mx17_3B0_Tb_S2_with_groups.a2l";
    String tempDir = System.getProperty("java.io.tmpdir") + File.separator + "A2L_EXPORT";
    String exportA2lFilePath = tempDir;

    A2LFileExportServiceInput a2lExportObj = new A2LFileExportServiceInput();
    a2lExportObj.setWpDefVersId(INVALID_WP_DEF_VER_ID);
    a2lExportObj.setVarGrpId(A2L_VARIANT_GRP);

    new A2LFileExportServiceClient().exportA2lFile(a2lExportObj, exportA2lFilePath, expA2lZipName);
    fail("Expected exception is not thrown");
  }

  /**
   * Negative Testcase with invalid Vg id
   *
   * @throws ApicWebServiceException as exception
   */
  @Test
  public void testExportA2lFileNegative2() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(startsWith("A2l Variant Group with ID "));

    String expA2lZipName = "009-Fiat-Fire-Mx17_3B0_Tb_S2_with_groups.a2l";
    String tempDir = System.getProperty("java.io.tmpdir") + File.separator + "A2L_EXPORT";
    String exportA2lFilePath = tempDir;

    A2LFileExportServiceInput a2lExportObj = new A2LFileExportServiceInput();
    a2lExportObj.setWpDefVersId(ACTIVE_WP_DEF_VER_ID);
    a2lExportObj.setVarGrpId(INVALID_A2L_VARIANT_GRP);

    new A2LFileExportServiceClient().exportA2lFile(a2lExportObj, exportA2lFilePath, expA2lZipName);
    fail("Expected exception is not thrown");
  }

  /**
   * Negative Testcase with invalid folder path
   *
   * @throws ApicWebServiceException as exception
   */
  @Test
  public void testExportA2lFileNegative3() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage(startsWith("Error unzipping file"));
    String expA2lZipName = "009-Fiat-Fire-Mx17_3B0_Tb_S2_with_Variantgroups.a2l";

    String exportA2lFilePath = "";

    A2LFileExportServiceInput a2lExportObj = new A2LFileExportServiceInput();
    a2lExportObj.setWpDefVersId(ACTIVE_WP_DEF_VER_ID);
    a2lExportObj.setVarGrpId(A2L_VARIANT_GRP);

    new A2LFileExportServiceClient().exportA2lFile(a2lExportObj, exportA2lFilePath, expA2lZipName);
    fail("Expected exception is not thrown");

  }
}
