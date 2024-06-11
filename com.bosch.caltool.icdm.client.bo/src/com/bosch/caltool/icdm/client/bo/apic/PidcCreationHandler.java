/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.apic.pidc.PIDCCreationWizardData;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcCreationData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcCreationDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcCreationRespData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcCreationServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class PidcCreationHandler {

  /**
   * @return Pidc Creation Details
   */
  public PidcCreationDetails getPidcCreationDetails() {
    PidcCreationDetails existingPidcNames = new PidcCreationDetails();
    PidcCreationServiceClient createPidcSerClient = new PidcCreationServiceClient();
    try {
      existingPidcNames = createPidcSerClient.getPidcCreationDetails(ApicConstants.PROJECT_NAME_ATTR);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return existingPidcNames;
  }

  /**
   * @param wizardData Creation Wizard Data
   * @param lvlAttrTreeNode level Attr Tree Node
   * @return PIDC that was created
   */
  public Pidc createPidc(final PIDCCreationWizardData wizardData, final PidcTreeNode lvlAttrTreeNode) {
    PidcServiceClient pidcSerClient = new PidcServiceClient();
    PidcCreationRespData pidcCreated = null;
    try {
      PidcCreationData pidcCreationData = getPidcCreationData(wizardData, lvlAttrTreeNode);
      pidcCreated = pidcSerClient.createPidc(pidcCreationData);
      return pidcCreated.getPidc();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * @param wizardData
   * @param lvlAttrTreeNode
   * @return
   * @throws ApicWebServiceException
   */
  private PidcCreationData getPidcCreationData(final PIDCCreationWizardData wizardData,
      final PidcTreeNode lvlAttrTreeNode) throws ApicWebServiceException {
    PidcCreationData pidcCreationData = new PidcCreationData();

    Pidc newPidc = new Pidc();
    newPidc.setNameEng(wizardData.getNameEng());
    newPidc.setDescEng(wizardData.getDescEng());
    newPidc.setDescGer(wizardData.getDescGer());
    if (null != wizardData.getAliasDefinition()) {
      newPidc.setAliasDefId(wizardData.getAliasDefinition().getId());
    }

    PidcVersion newPidcVer = new PidcVersion();
    newPidcVer.setVersionName(wizardData.getVersionName());
    newPidcVer.setVersDescEng(wizardData.getVersionDescEng());
    newPidcVer.setVersDescGer(wizardData.getVersionDescGer());

    pidcCreationData.setPidc(newPidc);
    pidcCreationData.setAllVersionSet(new HashSet<>(Arrays.asList(newPidcVer)));
    if (null != wizardData.getSelUcFavList()) {
      pidcCreationData.setSelUcFav(wizardData.getSelUcFavList());
    }

    ApicDataBO apicBO = new ApicDataBO();
    getNodeAttrVal(lvlAttrTreeNode, apicBO.getPidcStructMaxLvl(), pidcCreationData);

    CurrentUserBO currUser = new CurrentUserBO();
    pidcCreationData.setOwner(currUser.getUser());
    return pidcCreationData;
  }

  /**
   * @param lvlAttrTreeNode
   * @param pidcCreationData
   * @param i
   */
  private void getNodeAttrVal(final PidcTreeNode lvlAttrTreeNode, final int maxLvl,
      final PidcCreationData pidcCreationData) {
    if ((lvlAttrTreeNode.getLevel() <= maxLvl) && (lvlAttrTreeNode.getLevel() > 0)) {
      pidcCreationData.getStructAttrValMap().put(lvlAttrTreeNode.getNodeAttr().getId(),
          lvlAttrTreeNode.getNodeAttrValue().getId());
      getNodeAttrVal(lvlAttrTreeNode.getParentNode(), maxLvl, pidcCreationData);
    }
  }

  /**
   * @param wizardData PIDC Creation Wizard Data
   * @param lvlAttrTreeNode level Attr Tree Node
   * @param copiedPidcVer copied Pidc Version
   * @return newly created PIDC
   */
  public Pidc pastePidc(final PIDCCreationWizardData wizardData, final PidcTreeNode lvlAttrTreeNode,
      final PidcVersion copiedPidcVer) {
    PidcServiceClient pidcSerClient = new PidcServiceClient();
    PidcCreationRespData pidcCreated = null;
    try {
      PidcCreationData pidcCreationData = getPidcCreationData(wizardData, lvlAttrTreeNode);
      pidcCreationData.setCopiedPidcVer(copiedPidcVer);
      pidcCreated = pidcSerClient.createPidc(pidcCreationData);
      return pidcCreated.getPidc();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * @param newlyCreatedPidc newly Created Pidc
   * @return active Pidc Version
   */
  public PidcVersion getActiveVersion(final Pidc newlyCreatedPidc) {
    PidcVersionServiceClient verSer = new PidcVersionServiceClient();
    Map<Long, PidcVersionInfo> activeVerMap = new HashMap<>();
    try {
      activeVerMap = verSer.getActiveVersionsWithStructure(new HashSet<>(Arrays.asList(newlyCreatedPidc.getId())));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return activeVerMap.values().iterator().next().getPidcVersion();
  }
}
