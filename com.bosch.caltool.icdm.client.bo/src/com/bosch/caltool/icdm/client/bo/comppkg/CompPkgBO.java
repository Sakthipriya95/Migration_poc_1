/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.comppkg;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.LinkServiceClient;

/**
 * @author bne4cob
 */
public class CompPkgBO {

  private final CompPackage compPkg;

  /**
   * @param compPkg component package
   */
  public CompPkgBO(final CompPackage compPkg) {
    this.compPkg = compPkg;
  }

  /**
   * @return true if this component package is modifiable
   */
  public boolean isModifiable() {
    try {
      return new CurrentUserBO().hasNodeWriteAccess(this.compPkg.getId()) && !this.compPkg.isDeleted();
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
      return false;
    }
  }


  /**
   * Get the links for this component package
   *
   * @return sorted links
   */
  public SortedSet<Link> getLinks() {
    SortedSet<Link> retSet = new TreeSet<>();
    try {
      Map<Long, Link> respMap = new LinkServiceClient().getAllLinksByNode(this.compPkg.getId(), MODEL_TYPE.COMP_PKG);
      retSet.addAll(respMap.values());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    return retSet;
  }
}
