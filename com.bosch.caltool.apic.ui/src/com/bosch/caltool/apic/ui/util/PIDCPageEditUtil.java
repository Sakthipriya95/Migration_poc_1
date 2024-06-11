/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.dialogs.PIDCVariantValueDialog;
import com.bosch.caltool.apic.ui.editors.compare.ColumnDataMapper;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.apic.ProjectAttributeUtil;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixDataHandler;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixVersionClientBO.FM_REVIEW_STATUS;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.bo.apic.ProjAttrUtil;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributesUpdationModel;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVcdmTransferServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.ProjectAttributesUpdationServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * @author mga1cob
 */

public class PIDCPageEditUtil {


  private final ProjectAttributeUtil handlerUtil = new ProjectAttributeUtil();
  /**
   * PIDCVariantValueDialog instance
   */
  private PIDCVariantValueDialog pidcVarDia;
  private ColumnDataMapper columnDataMapper;

  private final AbstractProjectObjectBO projObjBO;

  /**
   * @param projObjBO
   * @param pidcPage instance
   */
  public PIDCPageEditUtil(final AbstractProjectObjectBO projObjBO) {
    this.projObjBO = projObjBO;
  }

  /**
   * @param pidcVarDia instance
   * @param projObjBO
   */
  public PIDCPageEditUtil(final PIDCVariantValueDialog pidcVarDia, final AbstractProjectObjectBO projObjBO) {
    this.pidcVarDia = pidcVarDia;
    this.projObjBO = projObjBO;
  }


  /**
   * @param columnDataMapper
   * @param projObjBO
   */
  public PIDCPageEditUtil(final ColumnDataMapper columnDataMapper, final AbstractProjectObjectBO projObjBO) {
    this.columnDataMapper = columnDataMapper;
    this.projObjBO = projObjBO;
  }

  /**
   * This method edits pidc attribute not used info
   *
   * @param selectedElement instance
   * @param selectedUsedValue instance
   */

  public void editProjectAttributeNotUsedInfo(final Object selectedElement, final Object selectedUsedValue) {

    IProjectAttribute pidcAttribute = (IProjectAttribute) selectedElement;

    checkPIDCLock();

    AbstractProjectAttributeBO projAttrHandler =
        this.handlerUtil.getProjectAttributeHandler(pidcAttribute, this.projObjBO);

    ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
    updationModel.setPidcVersion(this.projObjBO.getPidcVersion());

    // check if sdom pver name is set and valid
    boolean isPverNameValidToModify = new PIDCActionSet().isPverNameEditable(pidcAttribute, this.projObjBO);
    try {
      if (((pidcAttribute != null) &&
          Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR))
              .equals(pidcAttribute.getAttrId())) &&
          CommonActionSet.isQnaireConfigModifyErrorMessageShown(pidcAttribute, this.projObjBO)) {
        return;
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    if (projAttrHandler.isModifiable() && isPverNameValidToModify && (pidcAttribute != null)) {
      if ((pidcAttribute.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType()) ||
          (pidcAttribute.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType()))) &&
          selectedUsedValue.toString().equalsIgnoreCase(ApicConstants.BOOLEAN_TRUE_STRING)) {
        pidcAttribute.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType());
      }
      else {
        if (selectedUsedValue.toString().equalsIgnoreCase(ApicConstants.BOOLEAN_TRUE_STRING)) {
          pidcAttribute.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType());
        }
        else if (selectedUsedValue.toString().equalsIgnoreCase(ApicConstants.BOOLEAN_FALSE_STRING)) {
          pidcAttribute.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
        }
      }
      resetPredefAttrForGrpdAttr(pidcAttribute, updationModel, projAttrHandler);
      pidcAttribute.setValue("");
      pidcAttribute.setValueId(null);
      updateProjectAttribute(pidcAttribute, updationModel);
    }
    else {

      setLoggerInfo();
    }


  }

  /**
   * @param pidcAttribute
   * @param updationModel
   * @param abstractProjectAttributeBO
   */
  private void resetPredefAttrForGrpdAttr(final IProjectAttribute pidcAttribute,
      final ProjectAttributesUpdationModel updationModel, final AbstractProjectAttributeBO projAttrHandler) {
    // check if attribute is a grouped attribute,then reset predefined attribute
    Attribute attr = this.projObjBO.getPidcDataHandler().getAttributeMap().get(pidcAttribute.getAttrId());
    if (attr.isGroupedAttr() && (pidcAttribute.getValueId() != null)) {
      if (pidcAttribute instanceof PidcVersionAttribute) {
        setValueForPidcLevelPredefAttr(pidcAttribute, updationModel);
      }
      if (pidcAttribute instanceof PidcVariantAttribute) {
        setValueForVarLevelPredefAttr(pidcAttribute, updationModel, projAttrHandler);
      }
      if (pidcAttribute instanceof PidcSubVariantAttribute) {
        setValueForSubVarLevelPredefAttr(pidcAttribute, updationModel, projAttrHandler);
      }
    }
  }

  private void setValueForPidcLevelPredefAttr(final IModel grpAttr, final ProjectAttributesUpdationModel updateModel) {
    PidcVersionAttribute pidcVersionGrpdAttribute = (PidcVersionAttribute) grpAttr;
    PidcDataHandler pidcDataHandler = this.projObjBO.getPidcDataHandler();
    Map<Long, PredefinedAttrValue> predefAttrVal =
        pidcDataHandler.getPreDefAttrValMap().get(pidcVersionGrpdAttribute.getValueId());
    if (!CommonUtils.isNullOrEmpty(predefAttrVal)) {
      for (Entry<Long, PredefinedAttrValue> predefAttrValEntry : predefAttrVal.entrySet()) {
        PidcVersionAttribute versAttr =
            pidcDataHandler.getPidcVersAttrMap().get(predefAttrValEntry.getValue().getPredefinedAttrId());
        if (!pidcDataHandler.getPidcVersInvisibleAttrSet().contains(versAttr.getAttrId())) {
          versAttr.setUsedFlag(ApicConstants.NOT_DEFINED);
          versAttr.setValue(null);
          versAttr.setValueId(null);
          updateModel.getPidcAttrsToBeUpdated().put(versAttr.getAttrId(), versAttr);
        }
      }
    }
  }

  private void setValueForVarLevelPredefAttr(final IModel grpAttr, final ProjectAttributesUpdationModel updateModel,
      final AbstractProjectAttributeBO projAttrHandler) {
    PidcVariantAttribute pidcVariantGrpdAttribute = (PidcVariantAttribute) grpAttr;
    PidcDataHandler pidcDataHandler = this.projObjBO.getPidcDataHandler();
    Map<Long, PredefinedAttrValue> predefAttrVal =
        pidcDataHandler.getPreDefAttrValMap().get(pidcVariantGrpdAttribute.getValueId());
    if (!CommonUtils.isNullOrEmpty(predefAttrVal)) {
      for (Entry<Long, PredefinedAttrValue> predefAttrValEntry : predefAttrVal.entrySet()) {
        PidcVariantAttribute varAttr = pidcDataHandler.getVariantAttributeMap()
            .get(pidcVariantGrpdAttribute.getVariantId()).get(predefAttrValEntry.getValue().getPredefinedAttrId());
        if ((varAttr != null) && projAttrHandler.isNotInvisibleAttr(varAttr.getAttrId(), pidcVariantGrpdAttribute)) {
          varAttr.setUsedFlag(ApicConstants.NOT_DEFINED);
          varAttr.setValue(null);
          varAttr.setValueId(null);

          Map<Long, PidcVariantAttribute> varAttrMap;
          if (updateModel.getPidcVarAttrsToBeUpdated().get(varAttr.getVariantId()) == null) {
            varAttrMap = new HashMap<>();
            updateModel.getPidcVarAttrsToBeUpdated().put(varAttr.getVariantId(), varAttrMap);
          }
          else {
            varAttrMap = updateModel.getPidcVarAttrsToBeUpdated().get(varAttr.getVariantId());
          }
          varAttrMap.put(varAttr.getAttrId(), varAttr);
        }
      }
    }
  }


  private void setValueForSubVarLevelPredefAttr(final IModel grpAttr, final ProjectAttributesUpdationModel updateModel,
      final AbstractProjectAttributeBO projAttrHandler) {
    PidcSubVariantAttribute pidcSubVariantGrpdAttribute = (PidcSubVariantAttribute) grpAttr;
    PidcDataHandler pidcDataHandler = this.projObjBO.getPidcDataHandler();
    Map<Long, PredefinedAttrValue> predefAttrVal =
        pidcDataHandler.getPreDefAttrValMap().get(pidcSubVariantGrpdAttribute.getValueId());
    if (!CommonUtils.isNullOrEmpty(predefAttrVal)) {
      for (Entry<Long, PredefinedAttrValue> predefAttrValEntry : predefAttrVal.entrySet()) {
        PidcSubVariantAttribute subvarAttr =
            pidcDataHandler.getSubVariantAttributeMap().get(((PidcSubVariantAttribute) grpAttr).getSubVariantId())
                .get(predefAttrValEntry.getValue().getPredefinedAttrId());

        if ((subvarAttr != null) &&
            projAttrHandler.isNotInvisibleAttr(subvarAttr.getAttrId(), pidcSubVariantGrpdAttribute)) {
          subvarAttr.setUsedFlag(ApicConstants.NOT_DEFINED);
          subvarAttr.setValue(null);
          subvarAttr.setValueId(null);

          fillSubVarAttrMap(updateModel, subvarAttr);
        }
      }
    }
  }

  /**
   * @param updateModel
   * @param subvarAttr
   */
  private void fillSubVarAttrMap(final ProjectAttributesUpdationModel updateModel,
      final PidcSubVariantAttribute subvarAttr) {
    Map<Long, PidcSubVariantAttribute> subvarAttrMap;
    if (updateModel.getPidcSubVarAttrsToBeUpdated().get(subvarAttr.getSubVariantId()) == null) {
      subvarAttrMap = new HashMap<>();
      updateModel.getPidcSubVarAttrsToBeUpdated().put(subvarAttr.getSubVariantId(), subvarAttrMap);
    }
    else {
      subvarAttrMap = updateModel.getPidcSubVarAttrsToBeUpdated().get(subvarAttr.getSubVariantId());
    }
    subvarAttrMap.put(subvarAttr.getAttrId(), subvarAttr);
  }


  /**
   * Method to create/update project attributes
   *
   * @param projectAttribute - (pidcversion/pidcvariant/pidc subvariant) attributes
   * @param updationModel - model to update
   */
  public void updateProjectAttribute(final IProjectAttribute projectAttribute,
      final ProjectAttributesUpdationModel updationModel) {
    if (projectAttribute instanceof PidcVersionAttribute) {
      if (projectAttribute.getId() == null) {
        updationModel.getPidcAttrsToBeCreated().put(projectAttribute.getAttrId(),
            (PidcVersionAttribute) projectAttribute);
      }
      else {
        updationModel.getPidcAttrsToBeUpdated().put(projectAttribute.getAttrId(),
            (PidcVersionAttribute) projectAttribute);
      }
    }
    else if (projectAttribute instanceof PidcVariantAttribute) {
      Long variantId = ((PidcVariantAttribute) projectAttribute).getVariantId();
      if (projectAttribute.getId() == null) {
        Map<Long, PidcVariantAttribute> varAttrMap = updationModel.getPidcVarAttrsToBeCreated().get(variantId);
        if (null == varAttrMap) {
          varAttrMap = new HashMap<Long, PidcVariantAttribute>();
        }
        varAttrMap.put(projectAttribute.getAttrId(), (PidcVariantAttribute) projectAttribute);
        updationModel.getPidcVarAttrsToBeCreated().put(variantId, varAttrMap);
      }
      else {
        Map<Long, PidcVariantAttribute> varAttrMap = updationModel.getPidcVarAttrsToBeUpdated().get(variantId);
        if (null == varAttrMap) {
          varAttrMap = new HashMap<Long, PidcVariantAttribute>();
        }
        varAttrMap.put(projectAttribute.getAttrId(), (PidcVariantAttribute) projectAttribute);
        updationModel.getPidcVarAttrsToBeUpdated().put(variantId, varAttrMap);
        updationModel.getPidcVarsToBeUpdated().put(variantId,
            this.projObjBO.getPidcDataHandler().getVariantMap().get(variantId));
      }
    }
    else if (projectAttribute instanceof PidcSubVariantAttribute) {
      Long subVariantId = ((PidcSubVariantAttribute) projectAttribute).getSubVariantId();
      if (projectAttribute.getId() == null) {
        Map<Long, PidcSubVariantAttribute> varAttrMap = updationModel.getPidcSubVarAttrsToBeCreated().get(subVariantId);
        if (null == varAttrMap) {
          varAttrMap = new HashMap<Long, PidcSubVariantAttribute>();
        }
        varAttrMap.put(projectAttribute.getAttrId(), (PidcSubVariantAttribute) projectAttribute);

        updationModel.getPidcSubVarAttrsToBeCreated().put(projectAttribute.getAttrId(), varAttrMap);
      }
      else {
        Map<Long, PidcSubVariantAttribute> varAttrMap = updationModel.getPidcSubVarAttrsToBeUpdated().get(subVariantId);
        if (null == varAttrMap) {
          varAttrMap = new HashMap<Long, PidcSubVariantAttribute>();
        }
        varAttrMap.put(projectAttribute.getAttrId(), (PidcSubVariantAttribute) projectAttribute);

        updationModel.getPidcSubVarAttrsToBeUpdated().put(subVariantId, varAttrMap);
        updationModel.getPidcSubVarsToBeUpdated().put(subVariantId,
            this.projObjBO.getPidcDataHandler().getSubVariantMap().get(subVariantId));
      }
    }
    ProjectAttributesUpdationServiceClient upClient = new ProjectAttributesUpdationServiceClient();
    try {
      ProjectAttributesUpdationModel afterUpdateModel = upClient.updatePidcAttrs(updationModel);
      CommonUtils.shallowCopy(updationModel, afterUpdateModel);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

  }


  /**
   * @param selectedElement Object
   */
  public void editProjectAttributeNotDefinedFlag(final Object selectedElement) {
    IProjectAttribute pidcAttribute = (IProjectAttribute) selectedElement;
    checkPIDCLock();

    // check if sdom pver name is set and valid
    boolean isPverNameValidToModify = new PIDCActionSet().isPverNameEditable(pidcAttribute, this.projObjBO);
    try {
      if (((pidcAttribute != null) &&
          Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR))
              .equals(pidcAttribute.getAttrId())) &&
          CommonActionSet.isQnaireConfigModifyErrorMessageShown(pidcAttribute, this.projObjBO)) {
        return;
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
    if ((pidcAttribute != null) && isPverNameValidToModify) {
      updationModel.setPidcVersion(this.projObjBO.getPidcVersion());
      resetPredefAttrForGrpdAttr(pidcAttribute, updationModel,
          this.handlerUtil.getProjectAttributeHandler(pidcAttribute, this.projObjBO));
      pidcAttribute.setUsedFlag(PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
      pidcAttribute.setValueId(null);
      pidcAttribute.setValue("");
      updateProjectAttribute(pidcAttribute, updationModel);
      pidcAttribute.getUsedFlag();
    }

  }

  /**
   * This method edits pidc attribute used info
   *
   * @param selectedElement instance
   * @param selectedUsedValue instance
   */

  public void editProjectAttributeUsedInfo(final Object selectedElement, final Object selectedUsedValue) {
    IProjectAttribute pidcAttribute = (IProjectAttribute) selectedElement;

    checkPIDCLock();

    AbstractProjectAttributeBO projAttrHandler =
        this.handlerUtil.getProjectAttributeHandler(pidcAttribute, this.projObjBO);

    ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
    updationModel.setPidcVersion(this.projObjBO.getPidcVersion());

    boolean validate;
    if (pidcAttribute instanceof PidcVersionAttribute) {
      validate = projAttrHandler.isModifiable() && validateAPRJNameAttr((PidcVersionAttribute) pidcAttribute);
    }
    else {
      validate = projAttrHandler.isModifiable();
    }
    if (validate) {

      if (!projAttrHandler.getIsUsed().equalsIgnoreCase(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType())) {

        if ((projAttrHandler.getIsUsed().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType()) ||
            (projAttrHandler.getIsUsed().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType()))) &&
            selectedUsedValue.toString().equalsIgnoreCase(ApicConstants.BOOLEAN_TRUE_STRING)) {

          pidcAttribute.setUsedFlag(ApicConstants.CODE_YES);
        }
        else {
          if (selectedUsedValue.toString().equalsIgnoreCase(ApicConstants.BOOLEAN_TRUE_STRING)) {

            pidcAttribute.setUsedFlag(ApicConstants.CODE_YES);
          }
          else if (selectedUsedValue.toString().equalsIgnoreCase(ApicConstants.BOOLEAN_FALSE_STRING)) {

            pidcAttribute.setUsedFlag(ApicConstants.CODE_NO);
          }
        }
        updateProjectAttribute(pidcAttribute, updationModel);
      }
      else {
        refreshViewer();

      }
    }
    else {
      refreshViewer();
      setLoggerInfo();
    }

  }


  /**
   *
   */
  private void checkPIDCLock() {
    CurrentUserBO currUser = new CurrentUserBO();
    ApicDataBO apicBo = new ApicDataBO();
    try {
      if (!apicBo.isPidcUnlockedInSession(this.projObjBO.getPidcVersion()) &&
          currUser.hasNodeWriteAccess(this.projObjBO.getPidcVersion().getPidcId())) {
        final PIDCActionSet pidcActionSet = new PIDCActionSet();
        pidcActionSet.showUnlockPidcDialog(this.projObjBO.getPidcVersion());
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }


  /**
   * This method edits pidc variant attribute value
   *
   * @param varValueMap
   * @param partNumMap
   * @param specLinkMap
   * @param commMap
   */
  // ICDM-87
  public void editVariantAttrValue(
      final Map<IProjectAttribute, com.bosch.caltool.icdm.model.apic.attr.AttributeValue> varValueMap,
      final Map<IProjectAttribute, String> partNumMap, final Map<IProjectAttribute, String> specLinkMap,
      final Map<IProjectAttribute, String> commMap) {

    try {

      Iterator<Entry<IProjectAttribute, AttributeValue>> iterator = varValueMap.entrySet().iterator();

      while (iterator.hasNext()) {
        Entry<IProjectAttribute, AttributeValue> entry = iterator.next();

        if ((null != entry.getValue()) && entry.getKey().getAttrId().toString()
            .equals(new CommonDataBO().getParameterValue(CommonParamKey.VARIANT_IN_CUST_CDMS_ATTR_ID))) {

          for (PidcVariant varObj : this.projObjBO.getPidcDataHandler().getVariantMap().values()) {
            PidcVariantBO handler =
                new PidcVariantBO(this.projObjBO.getPidcVersion(), varObj, this.projObjBO.getPidcDataHandler());

            PidcVariantAttribute varAttribute = handler.getVarAttribute(entry.getKey().getAttrId());
            if ((null != varAttribute.getValueId()) && varAttribute.getValueId().equals(entry.getValue().getId())) {
              boolean resultFlag = MessageDialogUtils.getQuestionMessageDialog("Assignment exists !",
                  "This customer variant name is already assigned to variant  : " + varObj.getName() +
                      ". Do you want to reassign it to the current variant and remove the assignment in the other variant?");
              if (resultFlag) {
                // remove the already existing assignment
                entry.setValue(null);
                break;
              }
              else {
                return;
              }
            }
          }

        }

      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

    editVarAttr(varValueMap, partNumMap, specLinkMap, commMap);

  }

  /**
   * Method to create map for single attribute edits, and then pass to edit method
   *
   * @param attrValue
   * @param apicObject
   * @param partNumber
   * @param specLink
   * @param desc
   * @param pageEditUtil
   */
  public void editValue(final AttributeValue attrValue, final IModel apicObject, final String partNumber,
      final String specLink, final String desc) {
    // create value map
    Map<IProjectAttribute, AttributeValue> valueMap = new HashMap<IProjectAttribute, AttributeValue>() {

      {
        put((IProjectAttribute) apicObject, attrValue);
      }
    };

    // create partnumber map
    Map<IProjectAttribute, String> partNumMap = new HashMap<IProjectAttribute, String>() {

      {
        put((IProjectAttribute) apicObject, partNumber);
      }
    };

    // create spec link map
    Map<IProjectAttribute, String> specLinkMap = new HashMap<IProjectAttribute, String>() {

      {
        put((IProjectAttribute) apicObject, specLink);
      }
    };

    // create comm map
    Map<IProjectAttribute, String> commMap = new HashMap<IProjectAttribute, String>() {

      {
        put((IProjectAttribute) apicObject, desc);
      }
    };

    if (apicObject instanceof PidcSubVariantAttribute) {
      editSubVarAttrValue(valueMap, partNumMap, specLinkMap, commMap);
    }
    else if (apicObject instanceof PidcVariantAttribute) {
      editVariantAttrValue(valueMap, partNumMap, specLinkMap, commMap);
    }


  }

  /**
   * @param attributeValue
   * @param apicDataProvider
   * @param editableAttr
   * @param cmdAttrValue
   * @param partNumber
   * @param specLink
   * @param desc
   * @param pidcAttrVar
   */
  private void editVarAttr(
      final Map<IProjectAttribute, com.bosch.caltool.icdm.model.apic.attr.AttributeValue> varValueMap,
      final Map<IProjectAttribute, String> partNumMap, final Map<IProjectAttribute, String> specLinkMap,
      final Map<IProjectAttribute, String> commMap) {

    boolean create = false;
    ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
    updationModel.setPidcVersion(this.projObjBO.getPidcVersion());

    Map<Long, PidcVariantAttribute> varAttrMap;
    // set values for all variant attributes

    for (Entry<IProjectAttribute, com.bosch.caltool.icdm.model.apic.attr.AttributeValue> entry : varValueMap
        .entrySet()) {

      IProjectAttribute projectAttribute = entry.getKey();
      AttributeValue attributeValue = entry.getValue();


      if (attributeValue != null) {
        projectAttribute.setValueId(attributeValue.getId());
        projectAttribute.setValue(attributeValue.getName());
        ProjAttrUtil.setValueToUsedFlag(projectAttribute, attributeValue);
      }
      projectAttribute.setPartNumber(CommonUIConstants.DISP_TEXT_USE_CUR_VAL.equals(partNumMap.get(projectAttribute))
          ? projectAttribute.getPartNumber() : partNumMap.get(projectAttribute));
      projectAttribute.setSpecLink(CommonUIConstants.DISP_TEXT_USE_CUR_VAL.equals(specLinkMap.get(projectAttribute))
          ? projectAttribute.getSpecLink() : specLinkMap.get(projectAttribute));
      projectAttribute
          .setAdditionalInfoDesc(CommonUIConstants.DISP_TEXT_USE_CUR_VAL.equals(commMap.get(projectAttribute))
              ? projectAttribute.getAdditionalInfoDesc() : commMap.get(projectAttribute));

      // if id is null, variant attribute has to be created
      Long variantId = ((PidcVariantAttribute) projectAttribute).getVariantId();
      if (projectAttribute.getId() == null) {
        create = true;
        varAttrMap = updationModel.getPidcVarAttrsToBeCreated().get(variantId);

        if (null == varAttrMap) {
          varAttrMap = new HashMap<Long, PidcVariantAttribute>();
        }

        varAttrMap.put(projectAttribute.getAttrId(), (PidcVariantAttribute) projectAttribute);
        updationModel.getPidcVarAttrsToBeCreated().put(variantId, varAttrMap);
      }
      // id is not null, variant attribute has to be updated
      else {
        create = false;
        varAttrMap = updationModel.getPidcVarAttrsToBeUpdated().get(variantId);

        if (null == varAttrMap) {
          varAttrMap = new HashMap<Long, PidcVariantAttribute>();
        }
        varAttrMap.put(projectAttribute.getAttrId(), (PidcVariantAttribute) projectAttribute);
        updationModel.getPidcVarAttrsToBeUpdated().put(variantId, varAttrMap);
        updationModel.getPidcVarsToBeUpdated().put(variantId,
            this.projObjBO.getPidcDataHandler().getVariantMap().get(variantId));
      }

    }

    // call service client only if its a valid model, ie., input is not null or empty
    if (validateModel(updationModel, create)) {
      try {
        ProjectAttributesUpdationServiceClient upClient = new ProjectAttributesUpdationServiceClient();
        upClient.updatePidcAttrs(updationModel);

      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }


  /**
   * This method edits pidc Sub-variant attribute value
   *
   * @param attributeValue instance
   * @param apicDataProvider instance
   * @param editableAttr defines editable pidc attribute
   * @param cmdAttrValue instance
   * @param desc desc
   * @param specLink specLink
   * @param partNumber partNumber
   */
  // ICDM-122
  public void editSubVarAttrValue(
      final Map<IProjectAttribute, com.bosch.caltool.icdm.model.apic.attr.AttributeValue> subvarValueMap,
      final Map<IProjectAttribute, String> partNumMap, final Map<IProjectAttribute, String> specLinkMap,
      final Map<IProjectAttribute, String> commMap) {

    boolean create = false;
    ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
    updationModel.setPidcVersion(this.projObjBO.getPidcVersion());

    Map<Long, PidcSubVariantAttribute> subvarAttrMap;

    for (Entry<IProjectAttribute, com.bosch.caltool.icdm.model.apic.attr.AttributeValue> entry : subvarValueMap
        .entrySet()) {

      IProjectAttribute projectAttribute = entry.getKey();
      AttributeValue attributeValue = entry.getValue();

      projectAttribute.setValueId(attributeValue.getId());
      projectAttribute.setValue(attributeValue.getName());
      projectAttribute.setPartNumber(CommonUIConstants.DISP_TEXT_USE_CUR_VAL.equals(partNumMap.get(projectAttribute))
          ? projectAttribute.getPartNumber() : partNumMap.get(projectAttribute));
      projectAttribute.setSpecLink(CommonUIConstants.DISP_TEXT_USE_CUR_VAL.equals(specLinkMap.get(projectAttribute))
          ? projectAttribute.getSpecLink() : specLinkMap.get(projectAttribute));
      projectAttribute
          .setAdditionalInfoDesc(CommonUIConstants.DISP_TEXT_USE_CUR_VAL.equals(commMap.get(projectAttribute))
              ? projectAttribute.getAdditionalInfoDesc() : commMap.get(projectAttribute));
      ProjAttrUtil.setValueToUsedFlag(projectAttribute, attributeValue);

      // if id is null, variant attribute has to be created
      Long subVariantId = ((PidcSubVariantAttribute) projectAttribute).getSubVariantId();
      if (projectAttribute.getId() == null) {
        create = true;
        subvarAttrMap = updationModel.getPidcSubVarAttrsToBeCreated().get(subVariantId);

        if (null == subvarAttrMap) {
          subvarAttrMap = new HashMap<Long, PidcSubVariantAttribute>();
        }

        subvarAttrMap.put(projectAttribute.getAttrId(), (PidcSubVariantAttribute) projectAttribute);
        updationModel.getPidcSubVarAttrsToBeCreated().put(subVariantId, subvarAttrMap);
      }
      // id is not null, variant attribute has to be updated
      else {
        create = false;
        subvarAttrMap = updationModel.getPidcSubVarAttrsToBeUpdated().get(subVariantId);

        if (null == subvarAttrMap) {
          subvarAttrMap = new HashMap<Long, PidcSubVariantAttribute>();
        }

        subvarAttrMap.put(projectAttribute.getAttrId(), (PidcSubVariantAttribute) projectAttribute);
        updationModel.getPidcSubVarAttrsToBeUpdated().put(subVariantId, subvarAttrMap);
        updationModel.getPidcSubVarsToBeUpdated().put(subVariantId,
            this.projObjBO.getPidcDataHandler().getSubVariantMap().get(subVariantId));
      }

    }

    // call service client only if its a valid model, ie., input is not null or empty
    if (validateModel(updationModel, create)) {
      try {
        ProjectAttributesUpdationServiceClient upClient = new ProjectAttributesUpdationServiceClient();
        upClient.updatePidcAttrs(updationModel);

      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }

  }


  /**
   * @param projectAttribute
   * @param attributeValue
   */
  private void setValueToUsedFlag(final IProjectAttribute projectAttribute, final AttributeValue attributeValue) {
    if ("".equals(projectAttribute.getUsedFlag()) && (attributeValue.getId() == null)) {
      projectAttribute.setUsedFlag(ApicConstants.NOT_DEFINED);
    }
    else if (attributeValue.getId() != null) {
      projectAttribute.setUsedFlag(ApicConstants.CODE_YES);
    }
    else {
      projectAttribute.setUsedFlag(projectAttribute.getUsedFlag());
    }
  }

  /**
   * @param updationModel
   * @param create
   * @return
   */
  private boolean validateModel(final ProjectAttributesUpdationModel updationModel, final boolean create) {
    if (create) {
      return updationModel.getPidcAttrsToBeCreated().isEmpty() &&
          updationModel.getPidcVarAttrsToBeCreated().isEmpty() &&
          updationModel.getPidcSubVarAttrsToBeCreated().isEmpty() ? false : true;
    }
    return updationModel.getPidcAttrsToBeUpdated().isEmpty() && updationModel.getPidcVarAttrsToBeUpdated().isEmpty() &&
        updationModel.getPidcSubVarAttrsToBeUpdated().isEmpty() ? false : true;
  }


  /**
   * This method edits pidc attribute value
   *
   * @param attributeValue instance
   * @param editableAttr defines editable pidc attribute
   * @param partNumber partNumber
   * @param specLink specLink
   * @param desc description
   */
  public void editPIDCAttrValue(final com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue,
      final IProjectAttribute editableAttr, final String partNumber, final String specLink, final String desc) {
    checkPIDCLock();
    boolean canUpdate = true;
    // ICDM-1506
    if (this.projObjBO.getPidcDataHandler().getAttributeMap().get(editableAttr.getAttrId())
        .getLevel() == ApicConstants.VCDM_APRJ_NAME_ATTR) {
      canUpdate = canModifyAPRJAttributeValue(attributeValue);
    }
    if (canUpdate) {
      ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
      updationModel.setPidcVersion(this.projObjBO.getPidcVersion());
      PidcVersionAttribute pidcVersionAttribute = (PidcVersionAttribute) editableAttr;
      pidcVersionAttribute.setValueId(attributeValue.getId());
      pidcVersionAttribute.setValue(attributeValue.getName());
      pidcVersionAttribute.setPartNumber(partNumber);
      pidcVersionAttribute.setSpecLink(specLink);
      pidcVersionAttribute.setAdditionalInfoDesc(desc);
      pidcVersionAttribute.setAttrHidden(editableAttr.isAttrHidden() ? true : false);
      ProjAttrUtil.setValueToUsedFlag(pidcVersionAttribute, attributeValue);
      if (editableAttr.getId() == null) {
        updationModel.getPidcAttrsToBeCreated().put(editableAttr.getAttrId(), pidcVersionAttribute);
      }
      else {
        updationModel.getPidcAttrsToBeUpdated().put(editableAttr.getAttrId(), pidcVersionAttribute);
      }
      ProjectAttributesUpdationServiceClient upClient = new ProjectAttributesUpdationServiceClient();
      try {
        upClient.updatePidcAttrs(updationModel);

      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }


  private boolean canModifyAPRJAttributeValue(
      final com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue) {
    if ((attributeValue != null) && !CommonUtils.isEmptyString(attributeValue.getName())) {
      return performModifyAprjValidation(attributeValue.getName());
    }
    return false;
  }

  /**
   * @param attrValName Attribute value name
   * @return boolean
   */
  public boolean isVcdmAprjAttrNotModifiable(final String attrValName, final Long newAprjID) {

    AbstractProjectObjectBO projObjBO;

    if (this.columnDataMapper != null) {
      Iterator<IProjectAttribute> iterator = this.columnDataMapper.getIpidcAttrs().iterator();
      IProjectAttribute ipidcAttr = iterator.next();
      projObjBO = this.columnDataMapper.getProjectHandlerMap().get(this.handlerUtil.getID(ipidcAttr));
    }
    else {
      projObjBO = this.projObjBO;
    }
    Pidc pidc = projObjBO.getPidcDataHandler().getPidcVersionInfo().getPidc();
    Long oldAprjID = pidc.getAprjId();
    return (oldAprjID != null) && !CommonUtils.isEqual(oldAprjID, newAprjID);
  }

  /**
   * @param attrValName name of attributeValue
   * @return true if vcdm aprj attribute can be modified
   */
  public boolean performModifyAprjValidation(final String attrValName) {
    Long newAprjID = findAPRJID(attrValName);
    if (newAprjID == null) {
      CDMLogger.getInstance().warnDialog("APRJ not found in vCDM", Activator.PLUGIN_ID);
      return false;
      // Warn message corresponding APRJID not found Should APRJID be set
    }
    if (isVcdmAprjAttrNotModifiable(attrValName, newAprjID)) {
      CDMLogger.getInstance().warnDialog(
          "APRJ value cannot be set, since vCDM transfer has already occured with another APRJ. Please contact iCDM Hotline for support.",
          Activator.PLUGIN_ID);
      return false;
    }
    return true;
  }

  /**
   * @param aprjValue aprj attribute
   * @return aprj id
   */
  public Long findAPRJID(final String aprjValue) {
    PidcVcdmTransferServiceClient client = new PidcVcdmTransferServiceClient();
    Long aprjID = null;
    try {
      String aprjIdStr = client.findAPRJId(aprjValue);
      if (CommonUtils.isNotEmptyString(aprjIdStr)) {
        aprjID = Long.valueOf(aprjIdStr);
      }
    }
    catch (NumberFormatException | ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

    return aprjID;
  }

  /**
   * The method logs the information to CMDLogger
   */
  private void setLoggerInfo() {
    CDMLogger.getInstance().info(ApicUiConstants.EDIT_NOT_ALLOWED, Activator.PLUGIN_ID);
    CurrentUserBO currUser = new CurrentUserBO();
    try {
      if (!currUser.hasApicWriteAccess() && !currUser.hasNodeWriteAccess(this.projObjBO.getPidcVersion().getPidcId())) {
        CDMLogger.getInstance().info(ApicUiConstants.NO_WRITE_ACCESS + this.projObjBO.getPidcVersion().getName(),
            Activator.PLUGIN_ID);
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * Refresh the table viewer for the change
   */
  private void refreshViewer() {
    if (null != this.pidcVarDia) {
      this.pidcVarDia.getPidcAttrTabViewer().refresh();
    }

  }


  /**
   * get the part number
   *
   * @param subVariants subVariants
   * @param variants variants
   * @param apicObject apicObject
   * @return the Part number
   */
  public String getTextForPartNum(final IModel apicObject, final PidcVariant[] variants,
      final PidcSubVariant[] subVariants) {
    String partNum = "";
    IProjectAttribute pidcAttr = (IProjectAttribute) apicObject;
    // Check for Varaint
    if (CommonUtils.isNotNull(variants)) {
      for (PidcVariant pidcVar : variants) {
        partNum = getPartNumFromVar(partNum, pidcAttr, pidcVar);
      }
    }
    // Check for Sub varaint
    else if (CommonUtils.isNotNull(subVariants)) {
      for (PidcSubVariant pidcSubVar : subVariants) {
        partNum = getPartNumFromSubVar(partNum, pidcAttr, pidcSubVar);
      }
    }
    return partNum == null ? "" : partNum;
  }

  /**
   * @param partNum
   * @param pidcAttr
   * @param pidcSubVar
   * @return
   */
  private String getPartNumFromSubVar(String partNum, final IProjectAttribute pidcAttr,
      final PidcSubVariant pidcSubVar) {
    if (partNum == null) {
      partNum = "";
    }
    // If the Part num is empty get the first Sub-varaint part number
    if (partNum.isEmpty()) {

      partNum = this.projObjBO.getPidcDataHandler().getSubVariantAttributeMap().get(pidcSubVar.getId())
          .get(pidcAttr.getAttrId()).getPartNumber();
    }
    // If there is a varaition use the <Current Value>
    else if (!partNum.equals(this.projObjBO.getPidcDataHandler().getSubVariantAttributeMap().get(pidcSubVar.getId())
        .get(pidcAttr.getAttrId()).getPartNumber())) {
      partNum = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
    }
    return partNum;
  }

  /**
   * @param partNum
   * @param pidcAttr
   * @param pidcVar
   * @return
   */
  private String getPartNumFromVar(String partNum, final IProjectAttribute pidcAttr, final PidcVariant pidcVar) {
    if (partNum == null) {
      partNum = "";
    }
    // If the Part num is empty get the first Varaint part number
    if (partNum.isEmpty()) {

      partNum = this.projObjBO.getPidcDataHandler().getVariantAttributeMap().get(pidcVar.getId())
          .get(pidcAttr.getAttrId()).getPartNumber();
    }
    // If there is a varaition use the <Current Value>
    else if (!partNum.equals(this.projObjBO.getPidcDataHandler().getVariantAttributeMap().get(pidcVar.getId())
        .get(pidcAttr.getAttrId()).getPartNumber())) {
      partNum = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
    }
    return partNum;
  }

  /**
   * get the text for comments
   *
   * @param subVariants variants
   * @param variants variants
   * @param apicObject apicObject
   * @return the comments
   */
  public String getTextForComments(final IModel apicObject, final PidcVariant[] variants,
      final PidcSubVariant[] subVariants) {
    String comments = "";
    IProjectAttribute pidcAttr = (IProjectAttribute) apicObject;
    // Check for Varaint
    if (CommonUtils.isNotNull(variants)) {
      for (PidcVariant pidcVar : variants) {
        comments = getCommentsFromVar(comments, pidcAttr, pidcVar);
      }
    }
    // Check for Sub Varaint
    else if (CommonUtils.isNotNull(subVariants)) {
      for (PidcSubVariant pidcSubVar : subVariants) {
        comments = getCommentsFromSubVar(comments, pidcAttr, pidcSubVar);
      }
    }
    return comments == null ? "" : comments;
  }

  /**
   * @param comments
   * @param pidcAttr
   * @param pidcSubVar
   * @return
   */
  private String getCommentsFromSubVar(String comments, final IProjectAttribute pidcAttr,
      final PidcSubVariant pidcSubVar) {
    if (comments == null) {
      comments = "";
    }
    // If the Comment is empty get the first Varaint Comment
    if (comments.isEmpty()) {

      comments = this.projObjBO.getPidcDataHandler().getSubVariantAttributeMap().get(pidcSubVar.getId())
          .get(pidcAttr.getAttrId()).getAdditionalInfoDesc();
    }
    // If there is a varaition use the <Current Value>
    else if (!comments.equals(this.projObjBO.getPidcDataHandler().getSubVariantAttributeMap().get(pidcSubVar.getId())
        .get(pidcAttr.getAttrId()).getAdditionalInfoDesc())) {
      comments = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
    }
    return comments;
  }

  /**
   * @param comments
   * @param pidcAttr
   * @param pidcVar
   * @return
   */
  private String getCommentsFromVar(String comments, final IProjectAttribute pidcAttr, final PidcVariant pidcVar) {
    if (comments == null) {
      comments = "";
    }
    // If the Comment is empty get the first Varaint Comment
    if (comments.isEmpty()) {
      comments = this.projObjBO.getPidcDataHandler().getVariantAttributeMap().get(pidcVar.getId())
          .get(pidcAttr.getAttrId()).getAdditionalInfoDesc();
    }
    // If there is a varaition use the <Current Value>
    else if (!comments.equals(this.projObjBO.getPidcDataHandler().getVariantAttributeMap().get(pidcVar.getId())
        .get(pidcAttr.getAttrId()).getAdditionalInfoDesc())) {
      comments = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
    }
    return comments;
  }

  /**
   * get the text for comments
   *
   * @param subVariants subVariants
   * @param variants variants
   * @param apicObject apicObject
   * @return the Spec Link
   */
  public String getTextForSpecLink(final IModel apicObject, final PidcVariant[] variants,
      final PidcSubVariant[] subVariants) {
    String specLink = "";
    IProjectAttribute pidcAttr = (IProjectAttribute) apicObject;
    // Check for Varaint
    if (CommonUtils.isNotNull(variants)) {
      for (PidcVariant pidcVar : variants) {
        specLink = getSpecLinkFromVar(specLink, pidcAttr, pidcVar);
      }
    }
    // Check for Sub Varaint
    else if (CommonUtils.isNotNull(subVariants)) {
      for (PidcSubVariant pidcSubVar : subVariants) {
        // If the Spec Link is empty get the first Sub Varaint Spec Link
        specLink = getSpecLinkFromSubVar(specLink, pidcAttr, pidcSubVar);
      }
    }
    return specLink == null ? "" : specLink;
  }

  /**
   * @param specLink
   * @param pidcAttr
   * @param pidcSubVar
   * @return
   */
  private String getSpecLinkFromSubVar(String specLink, final IProjectAttribute pidcAttr,
      final PidcSubVariant pidcSubVar) {
    if (specLink == null) {
      specLink = "";
    }
    if (specLink.isEmpty()) {
      specLink = this.projObjBO.getPidcDataHandler().getSubVariantAttributeMap().get(pidcSubVar.getId())
          .get(pidcAttr.getAttrId()).getSpecLink();
    }
    // If there is a varaition use the <Current Value>
    else if (!specLink.equals(this.projObjBO.getPidcDataHandler().getSubVariantAttributeMap().get(pidcSubVar.getId())
        .get(pidcAttr.getAttrId()).getSpecLink())) {
      specLink = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
    }
    return specLink;
  }

  /**
   * @param specLink
   * @param pidcAttr
   * @param pidcVar
   * @return
   */
  private String getSpecLinkFromVar(String specLink, final IProjectAttribute pidcAttr, final PidcVariant pidcVar) {
    if (specLink == null) {
      specLink = "";
    }
    // If the Spec Link is empty get the first Varaint Comment
    if (specLink.isEmpty()) {
      specLink = this.projObjBO.getPidcDataHandler().getVariantAttributeMap().get(pidcVar.getId())
          .get(pidcAttr.getAttrId()).getSpecLink();
    }
    // If there is a varaition use the <Current Value>
    else if (!specLink.equals(this.projObjBO.getPidcDataHandler().getVariantAttributeMap().get(pidcVar.getId())
        .get(pidcAttr.getAttrId()).getSpecLink())) {
      specLink = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
    }
    return specLink;
  }

  /**
   * This method edits pidc attribute used info
   *
   * @param selectedElement instance
   * @param selectedFlagValue instance
   */

  public void editTransferToVcdmFlag(final Object selectedElement, final Object selectedFlagValue) {
    PidcVersionAttribute pidcAttribute = (PidcVersionAttribute) selectedElement;

    checkPIDCLock();
    AbstractProjectAttributeBO projAttrHandler =
        this.handlerUtil.getProjectAttributeHandler(pidcAttribute, this.projObjBO);

    boolean isModifiable = projAttrHandler.isModifiable();
    if (!isModifiable && pidcAttribute.isAtChildLevel()) {
      isModifiable = true;
    }
    if (isModifiable && validateAPRJNameAttr(pidcAttribute)) {
      ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
      updationModel.setPidcVersion(this.projObjBO.getPidcVersion());

      if (!(projAttrHandler.canTransferToVcdm() &&
          selectedFlagValue.toString().equalsIgnoreCase(ApicConstants.BOOLEAN_TRUE_STRING))) {

        pidcAttribute.setTransferToVcdm(
            selectedFlagValue.toString().equalsIgnoreCase(ApicConstants.BOOLEAN_TRUE_STRING) ? true : false);
        if (null == pidcAttribute.getId()) {
          pidcAttribute.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
        }
        updateProjectAttribute(pidcAttribute, updationModel);
      }
    }
    else {
      refreshViewer();
      setLoggerInfo();
    }

  }


  /**
   * This method edits pidc attribute used info
   *
   * @param selectedElement instance
   * @param selectedFlagValue instance
   * @param focusMatrixDataHandler
   */

  public void editFMRelevantFlag(final Object selectedElement, final Object selectedFlagValue,
      final FocusMatrixDataHandler focusMatrixDataHandler) {
    PidcVersionAttribute pidcAttribute = (PidcVersionAttribute) selectedElement;
    AbstractProjectAttributeBO projAttrHandler =
        this.handlerUtil.getProjectAttributeHandler(pidcAttribute, this.projObjBO);


    final PIDCActionSet pidcActionSet = new PIDCActionSet();

    checkPIDCLock();

    PidcVersionBO pidcVersionBO = (PidcVersionBO) this.projObjBO;
    ConcurrentMap<PidcVersionAttribute, Attribute> mapOfApplicableAttrs =
        null == focusMatrixDataHandler ? new FocusMatrixDataHandler(pidcVersionBO).getFocusMatrixApplicableAttrMap()
            : focusMatrixDataHandler.getFocusMatrixApplicableAttrMap();
    boolean isExcluded = false;
    StringBuilder str = new StringBuilder();
    str.append("[");
    if (!mapOfApplicableAttrs.containsKey(pidcAttribute) && !CommonUtils
        .isEmptyString(this.projObjBO.getPidcDataHandler().getAttribute(pidcAttribute.getAttrId()).getCharStr())) {
      str.append(this.projObjBO.getPidcDataHandler().getAttribute(pidcAttribute.getAttrId()).getCharStr() + " ");
      isExcluded = true;
    }

    str.append("]");
    if (isExcluded) {
      CDMLogger.getInstance().info(
          "This attribute cannot be marked as focus matrix relevant since it belongs to one of the excluded attribute class : " +
              str.toString(),
          Activator.PLUGIN_ID);

    }
    else {
      boolean isModifiable = this.projObjBO.isModifiable();

      if (isModifiable && validateAPRJNameAttr(pidcAttribute) &&
          new ApicDataBO().isPidcUnlockedInSession((projAttrHandler).getProjectObjectBO().getPidcVersion())) {

        if (!(pidcAttribute.isFocusMatrixApplicable() &&
            selectedFlagValue.toString().equalsIgnoreCase(ApicConstants.BOOLEAN_TRUE_STRING))) {

          ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
          updationModel.setPidcVersion(this.projObjBO.getPidcVersion());

          pidcAttribute.setFocusMatrixApplicable(
              selectedFlagValue.toString().equalsIgnoreCase(ApicConstants.BOOLEAN_TRUE_STRING) ? true : false);
          if (null == pidcAttribute.getId()) {
            pidcAttribute.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
          }
          updateProjectAttribute(pidcAttribute, updationModel);


          FocusMatrixVersion focusMatrixWorkingSetVersion =
              pidcVersionBO.getFocusMatrixWorkingSetVersion(focusMatrixDataHandler, pidcVersionBO);
          if ((null != focusMatrixWorkingSetVersion.getRvwStatus()) &&
              focusMatrixWorkingSetVersion.getRvwStatus().equals(FM_REVIEW_STATUS.YES.getStatusStr())) {
            pidcActionSet.resetRvwStatusToNo(focusMatrixWorkingSetVersion, null);
          }
        }
      }
      else {

        setLoggerInfo();
      }
    }

  }

  /**
   * @param pidcAttribute
   */
  private boolean validateAPRJNameAttr(final PidcVersionAttribute pidcAttribute) {

    try {
      if ((this.projObjBO.getPidcDataHandler().getAttributeMap().get(pidcAttribute.getAttrId())
          .getLevel() == ApicConstants.VCDM_APRJ_NAME_ATTR) &&
          (new PidcServiceClient().getById(this.projObjBO.getPidcVersion().getPidcId()).getAprjId() != null)) {
        CDMLogger.getInstance().warnDialog(
            "APRJ attribute cannot be modified, since vCDM transfer has already occured with another APRJ. Please contact iCDM Hotline for support.",
            Activator.PLUGIN_ID);

        return false;
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return true;
  }
}
