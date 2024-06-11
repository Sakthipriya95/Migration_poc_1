/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic.pidc;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributesUpdationModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionAttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dmo5cob
 */
public class ProjectAttributesMovementModel extends ProjectAttributesUpdationModel {


  private Map<Long, PidcVersionAttribute> pidcAttrsToBeMovedDown = new HashMap<>();

  private Map<Long, PidcVariantAttribute> pidcVarAttrsToBeMovedDown = new HashMap<>();

  private Map<Long, PidcVariantAttribute> pidcVarAttrsToBeMovedUp = new HashMap<>();

  private Map<Long, PidcSubVariantAttribute> pidcSubVarAttrsToBeMovedToVariant = new HashMap<>();

  private final PidcVersionBO pidcVersionBO;


  /**
   * @param pidcVersionBO
   */
  public ProjectAttributesMovementModel(final PidcVersionBO pidcVersionBO) {
    this.pidcVersionBO = pidcVersionBO;
  }


  /**
   * @return the pidcAttrsToBeMovedDown
   */
  public Map<Long, PidcVersionAttribute> getPidcAttrsToBeMovedDown() {
    return this.pidcAttrsToBeMovedDown;
  }


  /**
   * @param pidcAttrsToBeMovedDown the pidcAttrsToBeMovedDown to set
   */
  public void setPidcAttrsToBeMovedDown(final Map<Long, PidcVersionAttribute> pidcAttrsToBeMovedDown) {
    this.pidcAttrsToBeMovedDown = pidcAttrsToBeMovedDown;
  }


  /**
   * @return the pidcVarAttrsToBeMovedDown
   */
  public Map<Long, PidcVariantAttribute> getPidcVarAttrsToBeMovedDown() {
    return this.pidcVarAttrsToBeMovedDown;
  }


  /**
   * @param pidcVarAttrsToBeMovedDown the pidcVarAttrsToBeMovedDown to set
   */
  public void setPidcVarAttrsToBeMovedDown(final Map<Long, PidcVariantAttribute> pidcVarAttrsToBeMovedDown) {
    this.pidcVarAttrsToBeMovedDown = pidcVarAttrsToBeMovedDown;
  }


  /**
   * @return the pidcVarAttrsToBeMovedUp
   */
  public Map<Long, PidcVariantAttribute> getPidcVarAttrsToBeMovedUp() {
    return this.pidcVarAttrsToBeMovedUp;
  }


  /**
   * @param pidcVarAttrsToBeMovedUp the pidcVarAttrsToBeMovedUp to set
   */
  public void setPidcVarAttrsToBeMovedUp(final Map<Long, PidcVariantAttribute> pidcVarAttrsToBeMovedUp) {
    this.pidcVarAttrsToBeMovedUp = pidcVarAttrsToBeMovedUp;
  }


  /**
   * @return the pidcSubVarAttrsToBeMovedToVariant
   */
  public Map<Long, PidcSubVariantAttribute> getPidcSubVarAttrsToBeMovedToVariant() {
    return this.pidcSubVarAttrsToBeMovedToVariant;
  }


  /**
   * @param pidcSubVarAttrsToBeMovedToVariant the pidcSubVarAttrsToBeMovedToVariant to set
   */
  public void setPidcSubVarAttrsToBeMovedToVariant(
      final Map<Long, PidcSubVariantAttribute> pidcSubVarAttrsToBeMovedToVariant) {
    this.pidcSubVarAttrsToBeMovedToVariant = pidcSubVarAttrsToBeMovedToVariant;
  }


  /**
   * @return ProjectAttributesUpdationModel
   */
  public ProjectAttributesUpdationModel loadUpdationModel() {

    ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
    updationModel.setPidcVersion(this.pidcVersionBO.getPidcVersion());
    if (!getPidcAttrsToBeMovedDown().isEmpty()) {
      for (PidcVersionAttribute pidcAttr : getPidcAttrsToBeMovedDown().values()) {
        for (PidcVariant var : this.pidcVersionBO.getVariantsSet(true)) {
          createVarAttr(updationModel, pidcAttr, var);
          updationModel.getPidcVarsToBeUpdated().put(var.getId(), var);
        }
        updatePIDCAttr(updationModel, pidcAttr, true);
      }
    }
    if (!getPidcVarAttrsToBeMovedDown().isEmpty()) {

      for (PidcVariantAttribute varAttr : getPidcVarAttrsToBeMovedDown().values()) {
        PidcVariantBO varHan = new PidcVariantBO(this.pidcVersionBO.getPidcVersion(),
            this.pidcVersionBO.getPidcDataHandler().getVariantMap().get(varAttr.getVariantId()),
            this.pidcVersionBO.getPidcDataHandler());
        for (PidcSubVariant subvar : varHan.getSubVariantsSet(true)) {
          createSubVarAttr(updationModel, varAttr, subvar);
          updationModel.getPidcSubVarsToBeUpdated().put(subvar.getId(), subvar);
        }
        updatePIDCVarAttr(updationModel, varAttr, true);
        updationModel.getPidcVarsToBeUpdated().put(varHan.getPidcVariant().getId(), varHan.getPidcVariant());
      }
    }

    if (!getPidcVarAttrsToBeMovedUp().isEmpty()) {
      for (PidcVariantAttribute varAttr : getPidcVarAttrsToBeMovedUp().values()) {

        PidcVersionAttribute pidcAttr =
            this.pidcVersionBO.getPidcDataHandler().getPidcVersAttrMap().get(varAttr.getAttrId());
        updatePIDCAttr(updationModel, pidcAttr, false);

        for (PidcVariant variant : this.pidcVersionBO.getVariantsSet(true)) {
          PidcVariantBO varHan = new PidcVariantBO(this.pidcVersionBO.getPidcVersion(),
              this.pidcVersionBO.getPidcDataHandler().getVariantMap().get(variant.getId()),
              this.pidcVersionBO.getPidcDataHandler());
          // ICDM-1360
          for (PidcVariantAttribute vAttr : varHan.getAttributesAll().values()) {

            removeAttributeFromSubvariantIfDeleted(vAttr.getAttrId(), varHan, updationModel);

            if (vAttr.getAttrId().equals(varAttr.getAttrId())) {
              removeVarAttr(vAttr, updationModel);
            }
            updationModel.getPidcVarsToBeUpdated().put(variant.getId(), variant);
          }
        }
      }
    }
    if (!getPidcSubVarAttrsToBeMovedToVariant().isEmpty()) {
      for (PidcSubVariantAttribute subvarAttr : getPidcSubVarAttrsToBeMovedToVariant().values()) {

        Map<Long, PidcVariantAttribute> varAttrsMap =
            this.pidcVersionBO.getPidcDataHandler().getVariantAttributeMap().get(subvarAttr.getVariantId());
        final PidcVariantAttribute pidcVarAttr = varAttrsMap.get(subvarAttr.getAttrId());

        // mark the variant attribute as not sub-variant
        updatePIDCVarAttr(updationModel, pidcVarAttr, false);
        updationModel.getPidcVarsToBeUpdated().put(pidcVarAttr.getVariantId(),
            this.pidcVersionBO.getPidcDataHandler().getVariantMap().get(pidcVarAttr.getVariantId()));
        // For moving attribute from sub-variant to variant, only the
        // sub-variant attribute delete is done. Variant
        // attribute is not created as the not defined flag is to be set to
        // ???

        PidcVariantBO varHandler = new PidcVariantBO(this.pidcVersionBO.getPidcVersion(),
            this.pidcVersionBO.getPidcDataHandler().getVariantMap().get(pidcVarAttr.getVariantId()),
            this.pidcVersionBO.getPidcDataHandler());
        for (PidcSubVariant subVariant : varHandler.getSubVariantsSet(true)) {
          PidcSubVariantBO varHan = new PidcSubVariantBO(this.pidcVersionBO.getPidcVersion(),
              this.pidcVersionBO.getPidcDataHandler().getSubVariantMap().get(subVariant.getId()),
              this.pidcVersionBO.getPidcDataHandler());
          // ICDM-1360
          for (PidcSubVariantAttribute vAttr : varHan.getAttributesAll().values()) {


            if (vAttr.getAttrId().equals(pidcVarAttr.getAttrId())) {
              removeSubVarAttr(vAttr, updationModel);
            }
            updationModel.getPidcSubVarsToBeUpdated().put(subVariant.getId(), subVariant);
          }
        }


      }
    }
    return updationModel;
  }


  /**
   * @param vAttr
   * @param updationModel
   */
  private void removeSubVarAttr(final PidcSubVariantAttribute vAttr,
      final ProjectAttributesUpdationModel updationModel) {

    Map<Long, PidcSubVariantAttribute> mapOfAttrs =
        updationModel.getPidcSubVariantAttributeDeletedMap().get(vAttr.getSubVariantId());
    if (null == mapOfAttrs) {
      mapOfAttrs = new HashMap<>();
    }
    mapOfAttrs.put(vAttr.getAttrId(), vAttr);
    updationModel.getPidcSubVariantAttributeDeletedMap().put(vAttr.getSubVariantId(), mapOfAttrs);


  }


  /**
   * @param varAttr
   * @param updationModel
   */
  private void removeVarAttr(final PidcVariantAttribute varAttr, final ProjectAttributesUpdationModel updationModel) {
    Map<Long, PidcVariantAttribute> mapOfAttrs =
        updationModel.getPidcVariantAttributeDeletedMap().get(varAttr.getVariantId());
    if (null == mapOfAttrs) {
      mapOfAttrs = new HashMap<>();
    }
    mapOfAttrs.put(varAttr.getAttrId(), varAttr);
    updationModel.getPidcVariantAttributeDeletedMap().put(varAttr.getVariantId(), mapOfAttrs);
  }


  /**
   * @param variant
   * @param varHan
   * @param updationModel
   */
  private void removeAttributeFromSubvariantIfDeleted(final Long attrId, final PidcVariantBO varHan,
      final ProjectAttributesUpdationModel updationModel) {
    if (isSubVariantDeleted(varHan, attrId)) {
      for (PidcSubVariant subVar : varHan.getSubVariantsMap().values()) {
        PidcSubVariantBO subvarHan = new PidcSubVariantBO(varHan.getPidcVersion(), subVar, varHan.getPidcDataHandler());
        for (PidcSubVariantAttribute subVarAttr : subvarHan.getAttributesAll().values()) {
          removeAttrFromSubvariant(attrId, subVarAttr, updationModel);
        }
        updationModel.getPidcSubVarsToBeUpdated().put(subVar.getId(), subVar);
      }
    }

  }

  /**
   * Method to invoke removal of attribute from subvariant level
   *
   * @param attribute
   * @param subVarAttr
   * @param updationModel
   * @throws CommandException
   */
  private void removeAttrFromSubvariant(final Long attributeId, final PidcSubVariantAttribute subVarAttr,
      final ProjectAttributesUpdationModel updationModel) {
    if (attributeId.equals(subVarAttr.getAttrId())) {
      Map<Long, PidcSubVariantAttribute> mapOfAttrs =
          updationModel.getPidcSubVariantAttributeDeletedMap().get(subVarAttr.getSubVariantId());
      if (null == mapOfAttrs) {
        mapOfAttrs = new HashMap<>();
      }
      mapOfAttrs.put(subVarAttr.getAttrId(), subVarAttr);
      updationModel.getPidcSubVariantAttributeDeletedMap().put(subVarAttr.getSubVariantId(), mapOfAttrs);

    }
  }

  /**
   * Method that checks if variant attribute is part of any undeleted subvariant If it is part of undeleted subvariant,
   * throw error If it is part of deleted subvariant, do nothing
   *
   * @param varHan
   * @param attribute - pidc attribute to check
   * @return true if subvariant is deleted, false if not deleted
   */
  private boolean isSubVariantDeleted(final PidcVariantBO varHan, final Long attrId) {
    for (PidcSubVariant subVar : varHan.getSubVariantsMap().values()) {
      PidcSubVariantBO subvarHan = new PidcSubVariantBO(varHan.getPidcVersion(), subVar, varHan.getPidcDataHandler());
      if (subVar.isDeleted()) {
        continue;
      }
      if (!subvarHan.getAttributes().isEmpty() && (subvarHan.getAttributes().get(attrId) != null)) {
        return false;
      }

    }
    return true;
  }

  /**
   * @param updationModel
   * @param varAttr
   */
  private void updatePIDCVarAttr(final ProjectAttributesUpdationModel updationModel, final PidcVariantAttribute varAttr,
      final boolean moveDown) {
    varAttr.setAtChildLevel(moveDown);
    varAttr.setPartNumber("");
    varAttr.setSpecLink("");
    varAttr.setAdditionalInfoDesc("");
    varAttr.setUsedFlag(ApicConstants.CODE_YES);

    if (varAttr.getId() == null) {
      Map<Long, PidcVariantAttribute> varAttrMap =
          updationModel.getPidcVarAttrsToBeCreated().get(varAttr.getVariantId());
      if (null == varAttrMap) {
        varAttrMap = new HashMap<Long, PidcVariantAttribute>();
      }
      varAttrMap.put(varAttr.getAttrId(), varAttr);
      updationModel.getPidcVarAttrsToBeCreated().put(varAttr.getVariantId(), varAttrMap);
    }
    else {
      Map<Long, PidcVariantAttribute> varAttrMap =
          updationModel.getPidcVarAttrsToBeUpdated().get(varAttr.getVariantId());
      if (null == varAttrMap) {
        varAttrMap = new HashMap<Long, PidcVariantAttribute>();
      }
      varAttrMap.put(varAttr.getAttrId(), varAttr);
      updationModel.getPidcVarAttrsToBeUpdated().put(varAttr.getVariantId(), varAttrMap);
    }

  }


  /**
   * @param updationModel
   * @param varAttr
   * @param subvar
   */
  private void createSubVarAttr(final ProjectAttributesUpdationModel updationModel, final PidcVariantAttribute varAttr,
      final PidcSubVariant subvar) {

    PidcSubVariantAttribute subvarAttr = new PidcSubVariantAttribute();
    subvarAttr.setSubVariantId(subvar.getId());
    subvarAttr.setVariantId(subvar.getPidcVariantId());
    if (CommonUtils.isEqual(ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR.getUiType(), varAttr.getUsedFlag()) ||
        (CommonUtils.isEmptyString(varAttr.getUsedFlag()))) {
      subvarAttr.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
    }
    else {
      subvarAttr.setUsedFlag(varAttr.getUsedFlag());
    }
    subvarAttr.setAttrId(varAttr.getAttrId());
    subvarAttr.setValueId(varAttr.getValueId());
    subvarAttr.setValue(varAttr.getValue());
    subvarAttr.setAdditionalInfoDesc(varAttr.getAdditionalInfoDesc());
    subvarAttr.setPartNumber(varAttr.getPartNumber());
    subvarAttr.setSpecLink(varAttr.getSpecLink());
    subvarAttr.setAttrHidden(varAttr.isAttrHidden());
    Map<Long, PidcSubVariantAttribute> subvarAttrMap =
        updationModel.getPidcSubVarAttrsToBeCreated().get(subvarAttr.getSubVariantId());
    if (null == subvarAttrMap) {
      subvarAttrMap = new HashMap<>();
    }
    subvarAttrMap.put(subvarAttr.getAttrId(), subvarAttr);
    updationModel.getPidcSubVarAttrsToBeCreated().put(subvarAttr.getSubVariantId(), subvarAttrMap);

  }


  /**
   * @param updationModel
   * @param pidcAttr
   */
  private void updatePIDCAttr(final ProjectAttributesUpdationModel updationModel, PidcVersionAttribute pidcAttr,
      final boolean toVariant) {

    if ((pidcAttr.getId() == null) &&
        !this.pidcVersionBO.getPidcDataHandler().getPidcVersInvisibleAttrSet().contains(pidcAttr.getAttrId())) {
      try {
        PidcVersionAttribute projAttr = new PidcVersionAttributeServiceClient()
            .getPidcVersionAttribute(pidcAttr.getPidcVersId(), pidcAttr.getAttrId());
        pidcAttr = (null != projAttr) ? projAttr : pidcAttr;
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }

    pidcAttr.setAtChildLevel(toVariant);
    pidcAttr.setPartNumber("");
    pidcAttr.setSpecLink("");
    pidcAttr.setAdditionalInfoDesc("");
    pidcAttr.setUsedFlag(ApicConstants.CODE_YES);

    if (!((pidcAttr.getValueType().equals(AttributeValueType.DATE.getDisplayText())) ||
        (pidcAttr.getValueType().equals(AttributeValueType.HYPERLINK.getDisplayText())))) {
      pidcAttr.setTransferToVcdm(toVariant);
    }
    if (pidcAttr.getId() == null) {
      updationModel.getPidcAttrsToBeCreated().put(pidcAttr.getAttrId(), pidcAttr);
    }
    else {
      updationModel.getPidcAttrsToBeUpdated().put(pidcAttr.getAttrId(), pidcAttr);
    }
  }


  /**
   * @param updationModel
   * @param pidcAttr
   * @param var
   */
  private void createVarAttr(final ProjectAttributesUpdationModel updationModel, final PidcVersionAttribute pidcAttr,
      final PidcVariant var) {
    PidcVariantAttribute varAttr = new PidcVariantAttribute();
    varAttr.setVariantId(var.getId());
    if (CommonUtils.isEqual(ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR.getUiType(), pidcAttr.getUsedFlag()) ||
        (CommonUtils.isEmptyString(pidcAttr.getUsedFlag()))) {
      varAttr.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
    }
    else {
      varAttr.setUsedFlag(pidcAttr.getUsedFlag());
    }
    varAttr.setAttrId(pidcAttr.getAttrId());
    varAttr.setValueId(pidcAttr.getValueId());
    varAttr.setValue(pidcAttr.getValue());
    varAttr.setAdditionalInfoDesc(pidcAttr.getAdditionalInfoDesc());
    varAttr.setPartNumber(pidcAttr.getPartNumber());
    varAttr.setSpecLink(pidcAttr.getSpecLink());
    varAttr.setAttrHidden(pidcAttr.isAttrHidden());

    Map<Long, PidcVariantAttribute> varAttrMap = updationModel.getPidcVarAttrsToBeCreated().get(varAttr.getVariantId());
    if (null == varAttrMap) {
      varAttrMap = new HashMap<>();
    }
    varAttrMap.put(varAttr.getAttrId(), varAttr);
    updationModel.getPidcVarAttrsToBeCreated().put(varAttr.getVariantId(), varAttrMap);
  }
}
