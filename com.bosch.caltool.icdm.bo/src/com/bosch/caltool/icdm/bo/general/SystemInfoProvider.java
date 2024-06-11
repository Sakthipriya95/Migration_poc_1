/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import java.util.Arrays;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * @author bne4cob
 */
public final class SystemInfoProvider extends AbstractSimpleBusinessObject {


  /**
   * @param serviceData ServiceData
   */
  public SystemInfoProvider(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * @return list of work paths in the server group (PRO/BETA/DEV...) as configured in common parameter
   *         SERVER_GROUP_WORK_PATHS
   * @throws IcdmException data retrieval error
   */
  public List<String> getServerGroupWorkPaths() throws IcdmException {
    // Local paths for workspace server installations can also be added to the DB config
    String serverPaths = new CommonParamLoader(getServiceData()).getValue(CommonParamKey.SERVER_GROUP_WORK_PATHS);
    if (null == serverPaths) {
      throw new IcdmException("GENERAL.SERVER_PATH_MISSING");
    }
    String[] allServerPath = serverPaths.split(";");

    return Arrays.asList(allServerPath);
  }
}
