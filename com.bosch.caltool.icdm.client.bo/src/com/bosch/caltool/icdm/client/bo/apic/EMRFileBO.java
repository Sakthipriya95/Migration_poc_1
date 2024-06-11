/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;


import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.emr.EmrFileMapping;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.apic.emr.EmrFileServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author apj4cob
 */
public class EMRFileBO {

  private final Long pidcVerId;
  private final PidcVersionBO pidcVersionBO;
  private Map<Long, EmrFileMapping> fileMap = new HashMap<>();

  /**
   * @param pidcVerId Long
   * @param pidcVersionBO PidcVersionBO;
   */
  public EMRFileBO(final Long pidcVerId, final PidcVersionBO pidcVersionBO) {
    this.pidcVerId = pidcVerId;
    this.pidcVersionBO = pidcVersionBO;
  }

  /**
   * Gets the codex excel files result WS.
   *
   * @return SortedSet of EmrFileMapping
   */
  public Map<Long, EmrFileMapping> getCodexExcelFileResultWS() {
    if (isPidcVersionReadable() || checkIfUserHasEMRAccess()) {
      EmrFileServiceClient client = new EmrFileServiceClient();
      try {
        this.fileMap = client.getPidcEmrFileMapping(this.pidcVerId);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    return this.fileMap;
  }

  /**
   * @return boolean
   */
  public boolean isPidcVersionReadable() {
    boolean pidcVersionReadable = false;
    try {
      NodeAccess nodeAccessRight = new CurrentUserBO().getNodeAccessRight(
          this.pidcVersionBO.getPidcDataHandler().getPidcVersionInfo().getPidcVersion().getPidcId());
      if ((nodeAccessRight != null) && nodeAccessRight.isRead() &&
          (!this.pidcVersionBO.getPidcDataHandler().getPidcVersionInfo().getPidc().isDeleted())) {
        pidcVersionReadable = true;
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return pidcVersionReadable;
  }

  private boolean checkIfUserHasEMRAccess() {
    boolean isRead = false;
    try {
      Long emrNodeId = Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.EMR_NODE_ID));
      isRead = new CurrentUserBO().hasNodeReadAccess(emrNodeId);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return isRead;
  }

  /**
   * @return the pidcVerId
   */
  public Long getPidcVerId() {
    return this.pidcVerId;
  }


  /**
   * @return the fileSet
   */
  public Map<Long, EmrFileMapping> getFileMap() {
    return this.fileMap;
  }

  /**
   * @return the pidcVersionBO
   */
  public PidcVersionBO getPidcVersionBO() {
    return this.pidcVersionBO;
  }


}
