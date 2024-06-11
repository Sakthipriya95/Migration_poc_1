/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.vcdm;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;

/**
 * @author dja7cob
 */
public class VcdmAprjLoader extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData instance
   */
  public VcdmAprjLoader(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * Get APRJ Id using APRJ name
   *
   * @param aprjName APRJ name
   * @return APRJ Id
   * @throws IcdmException Exception in retrieving APRJ name
   */
  public String getAprjIdByName(final String aprjName) throws IcdmException {
    String aprjId = new VcdmInterfaceProvider(getServiceData()).createInterfaceSuperUser().getAPRJID(aprjName);
    getLogger().debug("APRJ ID for given name {} is : {}", aprjName, aprjId);

    return aprjId;
  }
}
