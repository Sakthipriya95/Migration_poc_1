/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.ProjectAttributesUpdationCommand;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjSubVariantsAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvVariantsAttr;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributesUpdationModel;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;

/**
 * @author mkl2cob
 */
public class PidcSubVarCopyCommand extends AbstractCommand<PidcSubVariant, PidcSubVariantLoader> {

  /**
   * PidcVariantCreationData
   */
  private final PidcSubVariantData pidcSubVarData;
  /**
   * PidcVariant
   */
  private PidcSubVariant destSubVariant;

  /**
   * ArrayList<PidcVariantAttribute>
   */
  private List<PidcSubVariantAttribute> valAlreadyExists;
  private boolean predefAttrAllowedForCopy;
  private Long grpAttrValId = null;
  /**
   * will be initialized only for update command
   */
  private PidcVersionWithDetails pidcVersWithDetails;

  /**
   * @param serviceData service data
   * @param inputData input data
   * @param isCreateNewSubVar true, if call is to create new sub variant
   * @throws IcdmException exception
   */
  public PidcSubVarCopyCommand(final ServiceData serviceData, final PidcSubVariantData inputData,
      final boolean isCreateNewSubVar) throws IcdmException {
    super(serviceData, inputData.getDestPidcSubVar(), new PidcSubVariantLoader(serviceData),
        isCreateNewSubVar ? COMMAND_MODE.CREATE : COMMAND_MODE.UPDATE);
    this.pidcSubVarData = inputData;
    this.destSubVariant = inputData.getDestPidcSubVar();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {

    createSubVariant();
    this.valAlreadyExists = new ArrayList<>();

    PidcSubVariantLoader pidcSubVarloader = new PidcSubVariantLoader(getServiceData());
    PidcSubVariant srcPidcSubVar = this.pidcSubVarData.getSrcPidcSubVar();
    PidcSubVariantAttributeLoader pidcSubVarAttrLoader = new PidcSubVariantAttributeLoader(getServiceData());

    if (!this.pidcSubVarData.isToUpdateSrcSubVarNotInDestVar()) {
      createDestSubVariants(pidcSubVarloader, srcPidcSubVar, pidcSubVarAttrLoader);
    }
    else {
      createSourceSubVarNotInDestVar(pidcSubVarloader, srcPidcSubVar);
    }
  }


  /**
   * @param pidcSubVarLoader
   * @param srcPidcSubVar
   * @throws IcdmException
   */
  private void createSourceSubVarNotInDestVar(final PidcSubVariantLoader pidcSubVarLoader,
      final PidcSubVariant srcPidcSubVar)
      throws IcdmException {
    Map<Long, PidcSubVariantAttribute> pidcSrcSubVarAttrMap =
        getSubVarAttrMapForSubVariantId(pidcSubVarLoader, srcPidcSubVar.getId());
    Map<Long, PidcSubVariantAttribute> pidcDestSubVarAttrMap =
        getSubVarAttrMapForSubVariantId(pidcSubVarLoader, this.destSubVariant.getId());

    PidcVariantLoader variantLoader = new PidcVariantLoader(getServiceData());
    TabvProjectVariant destVarEntity = variantLoader.getEntityObject(this.destSubVariant.getPidcVariantId());

    for (TabvVariantsAttr destPidcAttr : destVarEntity.getTabvVariantsAttrs()) {
      if (CommonUtilConstants.CODE_YES.equals(destPidcAttr.getIsSubVariant())) {
        PidcSubVariantAttribute subVarAttrToUpdate =
            pidcDestSubVarAttrMap.get(destPidcAttr.getTabvAttribute().getAttrId());
        PidcSubVariantAttribute subVarAttrToCopy =
            pidcSrcSubVarAttrMap.get(destPidcAttr.getTabvAttribute().getAttrId());
        if (CommonUtils.isNull(subVarAttrToCopy)) {
          subVarAttrToCopy = new PidcSubVariantAttribute();
          subVarAttrToCopy.setAttrId(destPidcAttr.getTabvAttribute().getAttrId());
          subVarAttrToCopy.setValueId(null);
          subVarAttrToCopy.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
          subVarAttrToCopy.setPartNumber("");
          subVarAttrToCopy.setSpecLink("");
          subVarAttrToCopy.setAdditionalInfoDesc("");
        }

        createOrUpdateSubVarAttr(subVarAttrToCopy, subVarAttrToUpdate);
        // remove attribute that is available in destination
        pidcDestSubVarAttrMap.remove(destPidcAttr.getTabvAttribute().getAttrId());
      }
    }
    // remove set of attributes that is in source not in destination variant
    if (CommonUtils.isNotEmpty(pidcDestSubVarAttrMap)) {
      deleteAttrNotInDestOrSrcSubVar(pidcDestSubVarAttrMap);
    }
  }


  /**
   * @param pidcSubVarLoader
   * @param srcPidcSubVar
   * @param pidcSubVarAttrLoader
   * @throws DataException
   * @throws IcdmException
   */
  private void createDestSubVariants(final PidcSubVariantLoader pidcSubVarLoader, final PidcSubVariant srcPidcSubVar,
      final PidcSubVariantAttributeLoader pidcSubVarAttrLoader)
      throws IcdmException {
    Map<Long, PidcSubVariantAttribute> pidcSubVarAttrMap =
        getSubVarAttrMapForSubVariantId(pidcSubVarLoader, this.destSubVariant.getId());
    TabvProjectSubVariant srcSubVarEntity = pidcSubVarLoader.getEntityObject(srcPidcSubVar.getId());
    for (TabvProjSubVariantsAttr pidcAttr : srcSubVarEntity.getTabvProjSubVariantsAttrs()) {
      PidcSubVariantAttribute subVarAttrToCopy = pidcSubVarAttrLoader.getDataObjectByID(pidcAttr.getSubVarAttrId());

      if (!isValidSubVarAttr(pidcAttr)) {
        continue;
      }

      final PidcSubVariantAttribute subVarAttrToUpdate = pidcSubVarAttrMap.get(pidcAttr.getTabvAttribute().getAttrId());

      createOrUpdateSubVarAttr(subVarAttrToCopy, subVarAttrToUpdate);
    }
  }

  /**
   * @param pidcSubVarAttrMap sub variant attribute map
   * @throws IcdmException exception
   */
  public void deleteAttrNotInDestOrSrcSubVar(final Map<Long, PidcSubVariantAttribute> pidcSubVarAttrMap)
      throws IcdmException {

    Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubVariantAttributeDeleteMap = new HashMap<>();
    pidcSubVariantAttributeDeleteMap.put(this.destSubVariant.getId(), pidcSubVarAttrMap);

    ProjectAttributesUpdationModel attrUpdationModel = new ProjectAttributesUpdationModel();
    attrUpdationModel.setPidcSubVariantAttributeDeletedMap(pidcSubVariantAttributeDeleteMap);

    ProjectAttributesUpdationCommand mainCmd =
        new ProjectAttributesUpdationCommand(getServiceData(), attrUpdationModel);
    executeChildCommand(mainCmd);
  }

  /**
   * Method to fetch list of pidc sub variant attributes for given sub variant id. The map also contains deleted and
   * invisible (due to attr dependencies) objects
   *
   * @param subVariantId
   * @param loader
   * @param variantId
   * @return map of subvariant attributes for given subvariant id Key - attr id, value - pidc subvariant attr obj
   * @throws DataException
   */
  private Map<Long, PidcSubVariantAttribute> getSubVarAttrMapForSubVariantId(final PidcSubVariantLoader loader,
      final Long variantId)
      throws DataException {
    Map<Long, PidcSubVariantAttribute> subVarAttrMap = new HashMap<>();
    List<TabvProjSubVariantsAttr> subVariantsAttrEntities =
        loader.getEntityObject(variantId).getTabvProjSubVariantsAttrs();
    if (subVariantsAttrEntities != null) {
      PidcSubVariantAttributeLoader pidcSubVarAttrLoader = new PidcSubVariantAttributeLoader(getServiceData());
      for (TabvProjSubVariantsAttr tabvSubVariantsAttr : subVariantsAttrEntities) {
        subVarAttrMap.put(tabvSubVariantsAttr.getTabvAttribute().getAttrId(),
            pidcSubVarAttrLoader.getDataObjectByID(tabvSubVariantsAttr.getSubVarAttrId()));
      }
    }
    return subVarAttrMap;
  }

  /**
   * Update the variant attribute
   *
   * @param varAttrToCopy reference attribute
   * @param varAttrToUpdate attribute to update
   * @throws IcdmException
   */
  private void updateSubVariantAttr(final PidcSubVariantAttribute varAttrToCopy,
      final PidcSubVariantAttribute varAttrToUpdate)
      throws IcdmException {

    if (this.pidcSubVarData.isFlagToUpdateDestSubVar()) {
      varAttrToUpdate.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
      varAttrToUpdate.setValueId(null);
    }
    else {
      varAttrToUpdate.setUsedFlag(varAttrToCopy.getUsedFlag());
      varAttrToUpdate.setValueId(varAttrToCopy.getValueId());
    }
    varAttrToUpdate.setPartNumber(varAttrToCopy.getPartNumber());
    varAttrToUpdate.setSpecLink(varAttrToCopy.getSpecLink());
    varAttrToUpdate.setAdditionalInfoDesc(varAttrToCopy.getAdditionalInfoDesc());
    varAttrToUpdate.setAttrId(varAttrToCopy.getAttrId());
    varAttrToUpdate.setAttrHidden(varAttrToCopy.isAttrHidden());
    PidcSubVariantAttributeCommand pidcSubVarAttrCmd =
        new PidcSubVariantAttributeCommand(getServiceData(), varAttrToUpdate, true, false);
    executeChildCommand(pidcSubVarAttrCmd);

  }

  /**
   * @param pidcAttr
   * @param varAttrToUpdate
   * @throws IcdmException
   */
  private void createOrUpdateSubVarAttr(final PidcSubVariantAttribute varAttrToCopy,
      final PidcSubVariantAttribute varAttrToUpdate)
      throws IcdmException {

    if (varAttrToUpdate == null) {
      PidcVariantAttribute varAttrVal =
          getVarAttrMapForVariantId(this.destSubVariant.getPidcVariantId()).get(varAttrToCopy.getAttrId());
      if (!this.pidcSubVarData.isSubVarCopiedAlongWithVariant() || isVarAttrAtChildLevel(varAttrVal) ||
          this.pidcSubVarData.isOverrideAll()) {
        createSubVariantAttr(varAttrToCopy);
      }
    }
    else {
      if ((varAttrToUpdate.getValueId() == null) || isAttrValidForUpdate(varAttrToCopy)) {
        updateSubVariantAttr(varAttrToCopy, varAttrToUpdate);
      }
      else if (!this.pidcSubVarData.isOverrideAll()) {
        this.valAlreadyExists.add(varAttrToUpdate);
      }
    }

  }

  /**
   * @param subvarAttrToCopy
   * @return
   */
  private boolean isAttrValidForUpdate(final PidcSubVariantAttribute subvarAttrToCopy) {
    boolean isAttrValidForUpdate = false;
    if ((this.pidcSubVarData.isOverrideAll() && isNotPverAttr(subvarAttrToCopy)) || this.predefAttrAllowedForCopy) {
      isAttrValidForUpdate = true;
    }
    return isAttrValidForUpdate;
  }


  /**
   * @param pidcVersWithDetails
   * @param subvarAttrToCopy
   * @return
   */
  private boolean isNotPverAttr(final PidcSubVariantAttribute subvarAttrToCopy) {
    return !(this.pidcVersWithDetails.getAllAttributeMap().get(subvarAttrToCopy.getAttrId()).getLevel()
        .equals(Long.valueOf(ApicConstants.SDOM_PROJECT_NAME_ATTR)));
  }

  /**
   * @param varAttrVal
   * @return
   */
  private boolean isVarAttrAtChildLevel(final PidcVariantAttribute varAttrVal) {
    boolean isChildAttrInVar = false;
    if (this.pidcSubVarData.isSubVarCopiedAlongWithVariant() && CommonUtils.isNotNull(varAttrVal) &&
        varAttrVal.isAtChildLevel()) {
      isChildAttrInVar = true;
    }
    return isChildAttrInVar;
  }


  /**
   * Method to fetch list of pidc variant attributes for given variant id. The map also contains deleted and
   * invisible(due to attr dependencies) objects
   *
   * @param variantId
   * @param loader
   * @return map of variant attributes for given variant id Key - attr id, value - pidc variant attr obj
   * @throws DataException
   */
  private Map<Long, PidcVariantAttribute> getVarAttrMapForVariantId(final Long variantId) throws DataException {
    PidcVariantLoader loader = new PidcVariantLoader(getServiceData());
    Map<Long, PidcVariantAttribute> varAttrMap = new HashMap<>();
    List<TabvVariantsAttr> variantsAttrEntities = loader.getEntityObject(variantId).getTabvVariantsAttrs();
    if (variantsAttrEntities != null) {
      PidcVariantAttributeLoader pidcVarAttrLoader = new PidcVariantAttributeLoader(getServiceData());
      for (TabvVariantsAttr tabvVariantsAttr : variantsAttrEntities) {
        varAttrMap.put(tabvVariantsAttr.getTabvAttribute().getAttrId(),
            pidcVarAttrLoader.getDataObjectByID(tabvVariantsAttr.getVarAttrId()));
      }
    }
    return varAttrMap;
  }


  /**
   * Create the PIDC Variant attribute
   *
   * @param varAttrToCopy Variant attribute to copy
   * @throws IcdmException
   */
  private void createSubVariantAttr(final IProjectAttribute varAttrToCopy) throws IcdmException {

    PidcSubVariantAttribute newPIDCVarAttr = new PidcSubVariantAttribute();
    if (this.pidcSubVarData.isFlagToUpdateDestSubVar()) {
      newPIDCVarAttr.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
      newPIDCVarAttr.setValueId(null);
    }
    else {
      newPIDCVarAttr.setUsedFlag(varAttrToCopy.getUsedFlag());
      newPIDCVarAttr.setValueId(varAttrToCopy.getValueId());
    }
    newPIDCVarAttr.setPartNumber(varAttrToCopy.getPartNumber());
    newPIDCVarAttr.setSpecLink(varAttrToCopy.getSpecLink());
    newPIDCVarAttr.setAdditionalInfoDesc(varAttrToCopy.getAdditionalInfoDesc());
    newPIDCVarAttr.setAttrId(varAttrToCopy.getAttrId());
    newPIDCVarAttr.setVariantId(this.destSubVariant.getPidcVariantId());
    newPIDCVarAttr.setSubVariantId(this.destSubVariant.getId());
    newPIDCVarAttr.setPidcVersionId(this.destSubVariant.getPidcVersionId());
    newPIDCVarAttr.setAttrHidden(varAttrToCopy.isAttrHidden());
    PidcSubVariantAttributeCommand pidcVarAttrCmd =
        new PidcSubVariantAttributeCommand(getServiceData(), newPIDCVarAttr, false, false);
    executeChildCommand(pidcVarAttrCmd);
  }

  /**
   * Checks whether the Variant attribute can be copied to the new Variant
   *
   * @param pidcAttr the Variant attribute to copy
   * @return true/false
   * @throws DataException
   */
  private boolean isValidSubVarAttr(final TabvProjSubVariantsAttr pidcAttr) throws DataException {

    PidcSubVariantAttributeLoader pidcVarAttrLoader = new PidcSubVariantAttributeLoader(getServiceData());
    PidcSubVariantAttribute varAttr = pidcVarAttrLoader.getDataObjectByID(pidcAttr.getSubVarAttrId());
    if (varAttr.isAtChildLevel()) {
      return false;
    }
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    Attribute attr = attrLoader.getDataObjectByID(pidcAttr.getTabvAttribute().getAttrId());
    final int attrLevel = attr.getLevel().intValue();
    // Ignore name, variant attributes
    return attrLevel != ApicConstants.SUB_VARIANT_CODE_ATTR;
  }

  /**
   * Create the Sub-Variant
   *
   * @throws IcdmException
   */
  private void createSubVariant() throws IcdmException {
    PidcSubVariantCommand pidcVarCommand = new PidcSubVariantCommand(getServiceData(), this.pidcSubVarData, false);
    executeChildCommand(pidcVarCommand);

    this.destSubVariant = pidcVarCommand.getNewData();
  }

  /**
   * update the Sub-Variant
   *
   * @throws IcdmException
   */
  private void undoDeleteNupdateSubVariant() throws IcdmException {
    PidcSubVariantCommand pidcVarCommand = new PidcSubVariantCommand(getServiceData(), this.pidcSubVarData, true);
    executeChildCommand(pidcVarCommand);
    // execute update to proceed with further process
    getEm().flush();

    this.destSubVariant = pidcVarCommand.getNewData();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {

    if (this.pidcSubVarData.isUndoDeleteNUpdateSubVar()) {
      undoDeleteNupdateSubVariant();
    }

    this.valAlreadyExists = new ArrayList<>();

    PidcSubVariantLoader pidcSubVarloader = new PidcSubVariantLoader(getServiceData());
    PidcSubVariant srcPidcSubVar = this.pidcSubVarData.getSrcPidcSubVar();
    TabvProjectSubVariant srcSubVarEntity = pidcSubVarloader.getEntityObject(srcPidcSubVar.getId());

    PidcSubVariantAttributeLoader pidcSubVarAttrLoader = new PidcSubVariantAttributeLoader(getServiceData());
    Map<Long, PidcSubVariantAttribute> pidcSubVarAttrMap =
        getSubVarAttrMapForSubVariantId(pidcSubVarloader, this.destSubVariant.getId());

    this.pidcVersWithDetails = getPidcVersionWithDetails(this.pidcSubVarData.getPidcVersionId());
    List<Long> attrToRemoveList = new ArrayList<>();

    for (TabvProjSubVariantsAttr srcPidcSubVarAttr : srcSubVarEntity.getTabvProjSubVariantsAttrs()) {
      PidcSubVariantAttribute subVarAttrToCopy =
          pidcSubVarAttrLoader.getDataObjectByID(srcPidcSubVarAttr.getSubVarAttrId());

      if (!isValidSubVarAttr(srcPidcSubVarAttr) || isSrcAttrInvisible(subVarAttrToCopy)) {
        continue;
      }

      // ICDM-1780
      final PidcSubVariantAttribute subVarAttrToUpdate =
          pidcSubVarAttrMap.get(srcPidcSubVarAttr.getTabvAttribute().getAttrId());


      if (CommonUtils.isNotNull(subVarAttrToUpdate)) {
        handleGroupAttribute(subVarAttrToUpdate, pidcSubVarAttrMap);
        // adding the attributes in destination to remove from pidcSubVarAttrMap after updating
        attrToRemoveList.add(subVarAttrToUpdate.getAttrId());
      }

      createOrUpdateSubVarAttr(subVarAttrToCopy, subVarAttrToUpdate);
    }
    // removing the updated attributes
    for (Long attrToRemove : attrToRemoveList) {
      pidcSubVarAttrMap.remove(attrToRemove);
    }
    // delete the sub-variant attributes which are not in source sub-variant but in destination sub-variant
    if (CommonUtils.isNotEmpty(pidcSubVarAttrMap) && this.pidcSubVarData.isOverrideAll()) {
      deleteAttrNotInDestOrSrcSubVar(pidcSubVarAttrMap);
    }
  }

  /**
   * @param pidcVersionId
   * @throws IcdmException
   */
  private PidcVersionWithDetails getPidcVersionWithDetails(final Long pidcVersionId) throws IcdmException {
    PidcVersionLoader pidcVerLoader = new PidcVersionLoader(getServiceData());
    return pidcVerLoader.getPidcVersionWithDetails(pidcVersionId);
  }


  private boolean isSrcAttrInvisible(final PidcSubVariantAttribute srcSubVarAttr) {
    boolean isAttrInvisible = false;
    Set<Long> subVarInvisibleAttrSet =
        this.pidcVersWithDetails.getSubVariantInvisbleAttributeMap().get(srcSubVarAttr.getSubVariantId());
    return CommonUtils.isNotEmpty(subVarInvisibleAttrSet) ? subVarInvisibleAttrSet.contains(srcSubVarAttr.getAttrId())
        : isAttrInvisible;
  }

  /**
   * @param varAttr
   * @param pidcDestVarAttrMap
   * @throws DataException
   */
  private void handleGroupAttribute(final PidcSubVariantAttribute varAttr,
      final Map<Long, PidcSubVariantAttribute> pidcDestVarAttrMap) {
    this.predefAttrAllowedForCopy = false;
    // check if the attribute is predefined attribute
    if (isPredefAttribute(varAttr)) {
      AttributeValue attributeValue = this.pidcVersWithDetails.getAttributeValueMap().get(this.grpAttrValId);
      Long destVarGrpAttrId = CommonUtils.isNotNull(attributeValue) ? attributeValue.getAttributeId() : null;

      // check whether the sub-variant which has predefined attribute, also has grouped attribute within it
      // if group attr has no value & predefined attr has value, then override the predefined attr value
      if (CommonUtils.isNotNull(destVarGrpAttrId) && pidcDestVarAttrMap.containsKey(destVarGrpAttrId) &&
          CommonUtils.isNotNull(varAttr.getValue()) &&
          CommonUtils.isNull(pidcDestVarAttrMap.get(destVarGrpAttrId).getValue())) {
        this.predefAttrAllowedForCopy = true;
      }
    }
  }


  private boolean isPredefAttribute(final PidcSubVariantAttribute varAttr) {
    for (Entry<Long, Map<Long, PredefinedAttrValue>> entryMap : this.pidcVersWithDetails.getPreDefAttrValMap()
        .entrySet()) {
      for (Entry<Long, PredefinedAttrValue> entry : entryMap.getValue().entrySet()) {
        if (entry.getValue().getPredefinedAttrId().equals(varAttr.getAttrId())) {
          this.grpAttrValId = entryMap.getKey();
          // check whether this predefined attribute is valid for this pidc version
          if (isPredefAttrValidForPidcVersion()) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   */
  private boolean isPredefAttrValidForPidcVersion() {
    boolean isPredefAttrValidForPidcVersion = true;
    for (Entry<Long, PredefinedValidity> predefValMapEntry : this.pidcVersWithDetails.getPredefinedValidityMap()
        .entrySet()) {
      PredefinedValidity preDefVal = predefValMapEntry.getValue();
      if (this.grpAttrValId.equals(preDefVal.getGrpAttrValId())) {
        isPredefAttrValidForPidcVersion = false;
        PidcVersionAttribute pidcVersAttr =
            this.pidcVersWithDetails.getPidcVersionAttributeMap().get(preDefVal.getValidityAttrId());
        // more than one PredefinedValidity model will have same grpAttrValId. So should not directly return the .equals
        // result
        if (preDefVal.getValidityValueId().equals(pidcVersAttr.getValueId())) {
          return true;
        }
      }
    }
    return isPredefAttrValidForPidcVersion;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // NA

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // NA

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

  /**
   * @return the destVariant
   */
  public PidcSubVariant getDestVariant() {
    return this.destSubVariant;
  }

  /**
   * @return List<PidcVariantAttribute>
   */
  public List<PidcSubVariantAttribute> getValAlreadyExists() {
    return new ArrayList<>(this.valAlreadyExists);
  }
}
