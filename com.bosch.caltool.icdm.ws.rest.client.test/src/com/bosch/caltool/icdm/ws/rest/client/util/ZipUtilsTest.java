/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;

/**
 * @author sng9kor
 */
public class ZipUtilsTest extends AbstractRestClientTest {


  /**
   * Test method for {@link ZipUtils#zip(String, String)}}
   *
   * @throws IOException
   */
  @Test
  public void zipTest() throws IOException {
    String filePath = "testdata/util/CDR_Report_AUDI_DIESEL_v1_AT_P1869_3_0_0_2020_08_18.xlsx";
    String zippedFilePath = ZipUtils.getZipPath(filePath);
    Files.deleteIfExists(Paths.get(zippedFilePath));
    ZipUtils.zip(filePath, zippedFilePath);
    assertTrue("Zipped file does not exists", new File(zippedFilePath).exists());
  }


  /**
   * Test method for {@link ZipUtils#zip(String, String)}} with empty path
   *
   * @throws IOException
   */
  @Test
  public void zipTestNegative() throws IOException {
    String filePath = "";
    String zippedFilePath = ZipUtils.getZipPath(filePath);
    Files.deleteIfExists(Paths.get(zippedFilePath));
    this.thrown.expect(IOException.class);
    ZipUtils.zip(filePath, zippedFilePath);
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }


  /**
   * Test method for {@link ZipUtils#unzip(String, String)}
   *
   * @throws IOException
   */
  @Test
  public void unZipTest() throws IOException {
    String zipFilePath = "testdata\\util\\TestUnzip.zip";
    ZipUtils.unzip(zipFilePath, CommonUtils.getICDMTmpFileDirectoryPath());
    File unzippedFile =
        new File(CommonUtils.getICDMTmpFileDirectoryPath() + File.separator + FilenameUtils.getBaseName(zipFilePath));
    assertTrue("Unzipped file doesnot exist", unzippedFile.exists());
  }


  /**
   * Test method for {@link ZipUtils#unzip(String, String)} with empty path
   *
   * @throws IOException
   */
  @Test
  public void unZipTestNegative01() throws IOException {
    String zipFilePath = "";
    this.thrown.expect(IOException.class);
    ZipUtils.unzip(zipFilePath, CommonUtils.getICDMTmpFileDirectoryPath());
    fail(EXPECTED_MESSAGE_NOT_THROWN);
  }


}
