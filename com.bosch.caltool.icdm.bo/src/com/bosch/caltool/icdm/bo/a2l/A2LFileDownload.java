/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.vcdm.VcdmFileDownload;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;

/**
 * @author dja7cob
 */
public class A2LFileDownload extends AbstractSimpleBusinessObject {

  /**
   * Instantiates a new a2L file info loader.
   *
   * @param serviceData serviceData
   */
  public A2LFileDownload(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * Method to get byte array of a2l file based on vcdm a2l file id
   *
   * @param vcdmA2lFileId vcdm a2l file id
   * @return byte array of A2L file
   * @throws IcdmException Exception in downloading A2L file
   */
  public byte[] getA2lFile(final Long vcdmA2lFileId) throws IcdmException {
    authenticateUser();

    if (null == vcdmA2lFileId) {
      throw new InvalidInputException("vCDM A2L File ID is mandatory");
    }

    if (!new A2LFileInfoLoader(getServiceData()).isValidVcdmA2lFileId(vcdmA2lFileId)) {
      throw new InvalidInputException("A2L.INVALID_VCDM_A2L_FILE_ID", vcdmA2lFileId);
    }

    return new VcdmFileDownload(getServiceData()).downloadVcdmFile(vcdmA2lFileId);
  }

  /**
   * Method to authenticate user for downloading a2l file
   *
   * @throws UnAuthorizedAccessException
   */
  void authenticateUser() throws UnAuthorizedAccessException {
    if (!getServiceData().isAuthenticatedUser()) {
      throw new UnAuthorizedAccessException("Insufficient privileges to do this operation");
    }
  }
}
