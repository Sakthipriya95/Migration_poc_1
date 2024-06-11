/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.vcdm;

import com.bosch.caltool.apic.vcdminterface.VCDMInterface;
import com.bosch.caltool.apic.vcdminterface.VCDMInterfaceException;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.logger.A2LLogger;
import com.bosch.caltool.icdm.logger.EASEELogger;
import com.bosch.caltool.security.Decryptor;

/**
 * @author dja7cob
 */
public class VcdmInterfaceProvider extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData instance
   */
  public VcdmInterfaceProvider(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * Creates vCDM Interface instance for logged in user
   *
   * @param encPwd username , encrypted pwd
   * @return Vcdm interface instance for user
   * @throws UnAuthorizedAccessException Exception when vcdm user login fails
   */
  public VCDMInterface createInterfaceUser(final String encPwd) throws UnAuthorizedAccessException {

    getLogger().debug("Create vCDM Interface for user : {}", getServiceData().getUsername());
    try {
      VCDMInterface vcdmInterface = new VCDMInterface(getServiceData().getUsername(),
          Decryptor.getInstance().decrypt(encPwd, getLogger()), EASEELogger.getInstance());

      getLogger().debug("vCDM Interface instance created for user");

      return vcdmInterface;
    }
    catch (VCDMInterfaceException e) {
      throw new UnAuthorizedAccessException("Error in vCDM Login. " + e.getMessage(), e);
    }
  }

  /**
   * Creates vCDM Interface instance for super user
   *
   * @return Vcdm interface instance for user
   * @throws IcdmException Exception in vcdm login
   */
  public VCDMInterface createInterfaceSuperUser() throws IcdmException {

    getLogger().debug("Create vCDM Interface for super user : {}", getServiceData().getUsername());
    try {
      VCDMInterface vcdmInterface = new VCDMInterface(EASEELogger.getInstance(), A2LLogger.getInstance());

      getLogger().debug("vCDM Interface instance created for super user");

      return vcdmInterface;
    }
    catch (VCDMInterfaceException e) {
      throw new IcdmException("Error in vCDM Login. " + e.getMessage(), e);
    }
  }
}
