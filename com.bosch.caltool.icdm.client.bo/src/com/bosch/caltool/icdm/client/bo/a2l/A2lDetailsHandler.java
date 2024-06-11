/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lVariantGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class A2lDetailsHandler {

  /**
   * Method to create a2l variant group
   *
   * @param varGrpName name
   * @param varGrpDesc description
   * @param wpDefnVersId wp defn version id
   * @param pidcA2l
   */
  public void createA2lVarGroup(final String varGrpName, final String varGrpDesc, final Long wpDefnVersId,
      final PidcA2l pidcA2l) {
    // create new a2l variant group object and set the details
    A2lVariantGroup a2lVarGrp = new A2lVariantGroup();
    a2lVarGrp.setName(varGrpName);
    a2lVarGrp.setDescription(varGrpDesc);
    a2lVarGrp.setWpDefnVersId(wpDefnVersId);
    A2lVariantGroupServiceClient a2lVarGrpServc = new A2lVariantGroupServiceClient();
    try {
      // call create service
      a2lVarGrpServc.create(a2lVarGrp, pidcA2l);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * Method to edit variant group details
   *
   * @param a2lVarGrp a2l var grp
   */
  public void editA2lVarGroup(final A2lVariantGroup a2lVarGrp, final PidcA2l pidcA2l) {
    A2lVariantGroupServiceClient a2lVarGrpServc = new A2lVariantGroupServiceClient();
    try {
      a2lVarGrpServc.update(a2lVarGrp, pidcA2l);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * Method to delete variant group details
   *
   * @param a2lVarGrp a2l var grp
   */
  public void deleteA2lVarGroup(final A2lVariantGroup a2lVarGrp, final PidcA2l pidcA2l) {
    A2lVariantGroupServiceClient a2lVarGrpServc = new A2lVariantGroupServiceClient();
    try {
      a2lVarGrpServc.delete(a2lVarGrp, pidcA2l);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

}
