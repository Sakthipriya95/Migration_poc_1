/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttrNValueDependencyCommand;
import com.bosch.caltool.icdm.bo.apic.attr.AttrValueCommand;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.general.TopLevelEntityCommand;
import com.bosch.caltool.icdm.bo.uc.UsecaseFavoriteCommand;
import com.bosch.caltool.icdm.bo.uc.UsecaseFavoriteLoader;
import com.bosch.caltool.icdm.bo.user.NodeAccessCommand;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TUsecaseFavorite;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcCreationData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;
import com.bosch.caltool.icdm.model.user.NodeAccess;

/**
 * @author dja7cob
 */
public class PidcCreationCommand extends AbstractSimpleCommand {

  private final PidcCreationData inputData;

  /**
   * @param serviceData
   * @param pidcCreationData
   * @throws IcdmException
   */
  public PidcCreationCommand(final ServiceData serviceData, final PidcCreationData pidcCreationData)
      throws IcdmException {
    super(serviceData);
    this.inputData = pidcCreationData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {

    AttributeValue newPidcNameAttrVal = createPidcNameAttrVal();

    Pidc pidcCreated = createPidc(newPidcNameAttrVal);
    PidcLoader pidcLoader = new PidcLoader(getServiceData());
    TabvProjectidcard dbPidc = pidcLoader.getEntityObject(pidcCreated.getId());
    createStructAttrDep(dbPidc);
    createPidcVer(pidcCreated);
    createNodeAccess(dbPidc);
    createProjectUcItems(dbPidc);
    getInputData().getPidc().setId(dbPidc.getProjectId());
    TopLevelEntityCommand tleCmd = new TopLevelEntityCommand(getServiceData(), ApicConstants.TOP_LVL_ENT_ID_PIDC);
    executeChildCommand(tleCmd);
  }


  /**
   * @param dbPidc
   * @throws IcdmException
   */
  private void createStructAttrDep(final TabvProjectidcard dbPidc) throws IcdmException {
    for (Entry<Long, Long> entry : getInputData().getStructAttrValMap().entrySet()) {
      AttributeLoader attrLoader = new AttributeLoader(getServiceData());
      if (attrLoader.getDataObjectByID(entry.getKey()).getLevel() == Long.valueOf(ApicConstants.PIDC_ROOT_LEVEL + 1)) {
        Long nameValId = dbPidc.getTabvAttrValue().getValueId();
        createValDependency(nameValId, entry.getKey(), entry.getValue());
      }
    }
  }

  /**
   * @param nameValId
   * @param id
   * @param id2
   * @throws IcdmException
   */
  private void createValDependency(final Long nameValId, final Long attrId, final Long valId) throws IcdmException {
    AttrNValueDependency depn = new AttrNValueDependency();
    depn.setValueId(nameValId);
    depn.setDependentAttrId(attrId);
    if (null != valId) {
      depn.setDependentValueId(valId);
    }
    AttrNValueDependencyCommand attrValDepCmd = new AttrNValueDependencyCommand(getServiceData(), depn, false);
    executeChildCommand(attrValDepCmd);
  }

  /**
   * @param dbPidc
   * @param selUcItems
   * @throws IcdmException
   */
  private void createProjectUcItems(final TabvProjectidcard dbPidc) throws IcdmException {
    Set<TUsecaseFavorite> tUcFavSet = new HashSet<>();
    UsecaseFavoriteLoader ucFavLoader = new UsecaseFavoriteLoader(getServiceData());
    for (UsecaseFavorite ucFav : getInputData().getSelUcFav()) {
      ucFav.setProjectId(dbPidc.getProjectId());
      UsecaseFavoriteCommand ucFavCmd = new UsecaseFavoriteCommand(getServiceData(), ucFav, false, false, false);
      executeChildCommand(ucFavCmd);
      tUcFavSet.add(ucFavLoader.getEntityObject(ucFavCmd.getNewData().getId()));
    }
    dbPidc.setTUsecaseFavorites(tUcFavSet);
  }

  /**
   * @param pidcCreated
   * @param dbPidc
   * @return
   * @throws IcdmException
   * @throws CommandException
   * @throws DataException
   */
  private PidcVersion createPidcVer(final Pidc pidcCreated) throws IcdmException {
    PidcVersionLoader versLoader = new PidcVersionLoader(getServiceData());
    Set<PidcVersion> pidcVerSet = new HashSet<>();
    for (PidcVersion ver : getInputData().getAllVersionSet()) {
      ver.setPidcId(pidcCreated.getId());
      if (null != getInputData().getCopiedPidcVer()) {
        ver.setParentPidcVerId(getInputData().getCopiedPidcVer().getId());
      }
      PidcVersionCommand pidcVerCmd =
          new PidcVersionCommand(getServiceData(), ver, null != getInputData().getCopiedPidcVer(), true, false);
      executeChildCommand(pidcVerCmd);
      PidcVersion newPidcVer = pidcVerCmd.getNewData();
      pidcVerSet.add(newPidcVer);
      TPidcVersion dbPidcVer = versLoader.getEntityObject(newPidcVer.getId());
      createProjStructAttrs(dbPidcVer);
    }
    return pidcVerSet.iterator().next();
  }


  /**
   * @param dbPidcVer
   * @throws IcdmException
   */
  private void createProjStructAttrs(final TPidcVersion dbPidcVer) throws IcdmException {
    List<TabvProjectAttr> projAttrSet = new ArrayList<>();
    for (Entry<Long, Long> entry : getInputData().getStructAttrValMap().entrySet()) {
      projAttrSet.add(createProjAttrCmd(dbPidcVer, entry.getKey(), entry.getValue()));
    }
    dbPidcVer.getTabvProjectAttrs().addAll(projAttrSet);
  }

  /**
   * @param dbPidcVer
   * @param key
   * @param valId
   * @return
   * @throws IcdmException
   */
  private TabvProjectAttr createProjAttrCmd(final TPidcVersion dbPidcVer, final Long attrId, final Long valId)
      throws IcdmException {
    PidcVersionAttribute pidcVerAttr = new PidcVersionAttribute();
    pidcVerAttr.setPidcVersId(dbPidcVer.getPidcVersId());
    pidcVerAttr.setAttrId(attrId);
    if (null != valId) {
      pidcVerAttr.setValueId(valId);
    }
    pidcVerAttr.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType());
    pidcVerAttr.setAtChildLevel(false);
    PidcVersionAttributeCommand verAttrCmd =
        new PidcVersionAttributeCommand(getServiceData(), pidcVerAttr, false, false);
    executeChildCommand(verAttrCmd);
    PidcVersionAttributeLoader pidcAttrLoader = new PidcVersionAttributeLoader(getServiceData());
    return pidcAttrLoader.getEntityObject(verAttrCmd.getNewData().getId());
  }


  /**
   * @param newPidcNameAttrVal
   * @return
   * @throws IcdmException
   * @throws DataException
   */
  private Pidc createPidc(final AttributeValue newPidcNameAttrVal) throws IcdmException {

    Pidc inputPidcObj = getInputData().getPidc();
    inputPidcObj.setNameValueId(newPidcNameAttrVal.getId());

    PidcCommand pidcCommand = new PidcCommand(getServiceData(), inputPidcObj, false, false);
    executeChildCommand(pidcCommand);

    return pidcCommand.getNewData();
  }

  /**
   * @return
   * @throws DataException
   * @throws IcdmException
   */
  private AttributeValue createPidcNameAttrVal() throws IcdmException {
    AttributeValue newAttrVal = createAttrValObj();
    AttrValueCommand cmd = new AttrValueCommand(getServiceData(), newAttrVal, false, false);
    executeChildCommand(cmd);
    return cmd.getNewData();
  }

  /**
   * @param dbPidc
   * @throws IcdmException
   */
  private void createNodeAccess(final TabvProjectidcard dbPidc) throws IcdmException {
    NodeAccess pidcNodeAccess = new NodeAccess();
    pidcNodeAccess.setOwner(true);
    pidcNodeAccess.setGrant(true);
    pidcNodeAccess.setWrite(true);
    pidcNodeAccess.setRead(true);
    pidcNodeAccess.setNodeId(dbPidc.getProjectId());
    pidcNodeAccess.setNodeType(ApicConstants.PIDC_NODE_TYPE);
    pidcNodeAccess.setUserId(getInputData().getOwner().getId());
    pidcNodeAccess.setVersion(1L);
    NodeAccessCommand nodeAccessCmd = new NodeAccessCommand(getServiceData(), pidcNodeAccess);
    executeChildCommand(nodeAccessCmd);
    getInputData().setCreatedNodeAccess(nodeAccessCmd.getNewData());
  }

  /**
   * @return
   * @throws DataException
   */
  private AttributeValue createAttrValObj() throws DataException {
    AttributeValue newAttrVal = new AttributeValue();
    newAttrVal.setTextValueEng(getInputData().getPidc().getNameEng());
    newAttrVal.setDescriptionEng(getInputData().getPidc().getDescEng());
    newAttrVal.setDescriptionGer(getInputData().getPidc().getDescGer());
    newAttrVal.setClearingStatus("Y");
    newAttrVal.setDeleted(false);
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    Attribute projNameAttr = attrLoader.getAllLevelAttributes().get(new Long(ApicConstants.PROJECT_NAME_ATTR));
    newAttrVal.setAttributeId(projNameAttr.getId());
    return newAttrVal;
  }

  /**
   * @return
   */
  private PidcCreationData getInputData() {
    return this.inputData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // NA

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }
}
