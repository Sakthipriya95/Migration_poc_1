/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import com.bosch.caltool.apic.vcdminterface.VCDMInterface;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.vcdm.VcdmInterfaceProvider;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.easee.eASEEcdm_Service.EASEEServiceException;

/**
 * The Class A2lFileUploader.
 *
 * @author gge6cob
 */
public class A2lFileUploader extends AbstractSimpleBusinessObject {


  /**
   * Instantiates a new a 2 l file uploader.
   *
   * @param serviceData servicedata
   */
  public A2lFileUploader(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * Calls eASEE service method to upload the a2l file.
   *
   * @param a2lFilePath the a 2 l file path
   * @return the string
   * @throws IcdmException Icdm Exception
   */
  public String uploadA2LToVcdm(final String a2lFilePath) throws IcdmException {
    getLogger().debug("Starting uploading a2l file using eASEE service.. ");
    String a2lVersion = "";
    final VCDMInterface easeeHandler = new VcdmInterfaceProvider(getServiceData()).createInterfaceSuperUser();
    // uploads the a2l file and returns version number
    try {
      a2lVersion = easeeHandler.loadA2LFile(a2lFilePath);
    }
    catch (EASEEServiceException e) {
      throw new IcdmException(e.getMessage(), e);
    }
    getLogger().debug("A2l file uploaded, version number as : {}", a2lVersion);
    return a2lVersion;
  }

  /**
   * Check A 2 l exists.
   *
   * @param a2lFile the a 2 l file
   * @param sdomPver the sdom pver
   * @param varName the var name
   * @param varRev the var rev
   * @throws IcdmException the icdm exception
   */
  public void checkA2lExists(final A2LFile a2lFile, final String sdomPver, final String varName, final long varRev)
      throws IcdmException {

    // get the current SDOM information of the A2L file
    String pverName = a2lFile.getSdomPverName();
    String pverVarName = a2lFile.getSdomPverVariant();
    long pverVarRev = null == a2lFile.getSdomPverRevision() ? 0L : a2lFile.getSdomPverRevision();

    // check, if SDOM infomation is not existing,// A2L is existing with SDOM PVER information
    if (isSdomInfoAvailable(pverName, pverVarName) &&
        (!CommonUtils.isEqual(pverName, sdomPver) || isPverVarNameEqual(varName, varRev, pverVarName, pverVarRev))) {
      // different SDOM information existing => error
      String errorMsg = "A2L File already exists with following info.\nPVER Name :" + pverName +
          "\nPVER Variant Name :" + pverVarName + "\nPVER Variant Revision :" + pverVarRev;
      getLogger().error(errorMsg);
      throw new IcdmException(errorMsg);
    }
  }

  /**
   * @param pverName
   * @param pverVarName
   * @return
   */
  private boolean isSdomInfoAvailable(final String pverName, final String pverVarName) {
    return (null != pverName) || (null != pverVarName);
  }

  /**
   * @param varName
   * @param varRev
   * @param pverVarName
   * @param pverVarRev
   * @return
   */
  private boolean isPverVarNameEqual(final String varName, final long varRev, final String pverVarName,
      final long pverVarRev) {
    return !CommonUtils.isEqual(pverVarName, varName) || (pverVarRev != varRev);
  }
}
