/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.CommandExecuter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * @author rgo7cob
 */
public class A2LWpDefVersionChecker extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData serviceData to access loader class
   */
  public A2LWpDefVersionChecker(final ServiceData serviceData) {
    super(serviceData);
  }


  /**
   * @param pidcA2lId input pidc A2l id for which the working set and Active verion to be created
   * @return the active version being created or which is already available
   * @throws IcdmException exception from the command exeution
   */
  public A2lWpDefnVersion ensureActiveWpDefVerForA2l(final long pidcA2lId) throws IcdmException {

    A2lWpDefnVersionLoader a2lWpDefLoader = new A2lWpDefnVersionLoader(getServiceData());
    Map<Long, A2lWpDefnVersion> retMap = a2lWpDefLoader.getWPDefnVersionsForPidcA2lId(pidcA2lId);

    if (retMap.isEmpty()) {
      A2lWpDefnVersion a2lWpDefnVersion = new A2lWpDefnVersion();
      a2lWpDefnVersion.setVersionName(ApicConstants.WORKING_SET_NAME);
      a2lWpDefnVersion.setActive(false);
      a2lWpDefnVersion.setWorkingSet(true);
      a2lWpDefnVersion.setParamLevelChgAllowedFlag(false);
      a2lWpDefnVersion.setPidcA2lId(pidcA2lId);
      A2lWpDefaultDefinitionCommand cmd =
          new A2lWpDefaultDefinitionCommand(getServiceData(), a2lWpDefnVersion, false, true);
      CommandExecuter cmdExecute = getServiceData().getCommandExecutor();
      cmdExecute.execute(cmd);
    }

    return a2lWpDefLoader.getActiveVersion(pidcA2lId);
  }

}
