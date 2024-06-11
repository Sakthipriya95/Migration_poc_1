/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.compli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.SystemInfoProvider;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ZipUtils;

/**
 * @author dmr1cob
 */
public class CompliReviewInputDataProvider extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData {@link ServiceData}
   */
  public CompliReviewInputDataProvider(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * @param executionId execution id is used to find folder path
   * @return input files byte stream
   * @throws IcdmException exception
   */
  public byte[] getInputData(final String executionId) throws IcdmException {
    byte[] fileData = null;
    for (String serverPath : new SystemInfoProvider(getServiceData()).getServerGroupWorkPaths()) {
      String folderPath = serverPath + File.separator + "COMPLI_REVIEWS" + File.separator + executionId;
      String zipFileName = folderPath + ".zip";
      File file = new File(folderPath);
      if (file.exists()) {
        try {
          ZipUtils.zip(Paths.get(folderPath), Paths.get(zipFileName));
        }
        catch (IOException e) {
          throw new IcdmException(e.getLocalizedMessage(), e);
        }
      }
      File zipFile = new File(zipFileName);
      if (zipFile.exists()) {
        try (FileInputStream fin = new FileInputStream(zipFile)) {
          fileData = readBytes(fin);
          break;
        }
        catch (IOException e) {
          throw new IcdmException("COMPLI_REVIEW.FILE_DOWNLOAD_ERROR", e, executionId);
        }
      }
    }
    if (null == fileData) {
      throw new IcdmException("COMPLI_REVIEW.FILES_NOT_AVAILABLE", executionId);
    }

    return fileData;
  }

  /**
   * Adds the file bytes to map.
   *
   * @param filesStreamMap the files stream map
   * @param filePath the file path
   * @param unzipIfZippedStream the unzip if zipped stream
   * @return
   * @throws IcdmException the icdm exception
   */
  private byte[] readBytes(final InputStream unzipIfZippedStream) throws IcdmException {
    try {
      return IOUtils.toByteArray(unzipIfZippedStream);
    }
    catch (IOException exp) {
      throw new IcdmException(exp.getLocalizedMessage(), exp);
    }
  }

}
