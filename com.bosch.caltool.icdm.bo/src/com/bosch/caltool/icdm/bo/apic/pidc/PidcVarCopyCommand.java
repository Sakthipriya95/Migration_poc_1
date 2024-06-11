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
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvVariantsAttr;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributesUpdationModel;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;

/**
 * @author mkl2cob
 */
public class PidcVarCopyCommand extends AbstractCommand<PidcVariant, PidcVariantLoader> {

  /**
   * PidcVariantCreationData
   */
  private final PidcVariantData pidcVarData;
  /**
   * PidcVariant
   */
  private PidcVariant destVariant;

  /**
   * ArrayList<PidcVariantAttribute>
   */
  private List<PidcVariantAttribute> valAlreadyExists;
  private Long grpAttrValId = null;
  private boolean predefAttrAllowedForCopy;
  /**
   * will be initialized only for update command
   */
  private PidcVersionWithDetails pidcVersWithDetails;

  /**
   * @param serviceData service data
   * @param inputData input data
   * @param isCreateNewVar true, if call is to create new variant
   * @throws IcdmException exception
   */
  public PidcVarCopyCommand(final ServiceData serviceData, final PidcVariantData inputData,
      final boolean isCreateNewVar) throws IcdmException {
    super(serviceData, inputData.getDestPidcVar(), new PidcVariantLoader(serviceData),
        isCreateNewVar ? COMMAND_MODE.CREATE : COMMAND_MODE.UPDATE);
    this.pidcVarData = inputData;
    this.destVariant = inputData.getDestPidcVar();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {

    createVariant();
    this.valAlreadyExists = new ArrayList<>();

    PidcVariantLoader loader = new PidcVariantLoader(getServiceData());
    PidcVariant srcPidcVar = this.pidcVarData.getSrcPidcVar();
    TabvProjectVariant srcVarEntity = loader.getEntityObject(srcPidcVar.getId());

    PidcVariantAttributeLoader pidcVarAttrLoader = new PidcVariantAttributeLoader(getServiceData());

    // getting attribute and value mapped to destination variant
    Map<Long, PidcVariantAttribute> pidcVarAttrMap = getVarAttrMapForVariantId(loader, this.destVariant);

    for (TabvVariantsAttr pidcAttr : srcVarEntity.getTabvVariantsAttrs()) {
      PidcVariantAttribute varAttrToCopy = pidcVarAttrLoader.getDataObjectByID(pidcAttr.getVarAttrId());

      if (!this.pidcVarData.isSubVarCopiedAlongWithVar() && !isValidVarAttr(pidcAttr)) {
        continue;
      }

      final PidcVariantAttribute varAttrToUpdate = pidcVarAttrMap.get(pidcAttr.getTabvAttribute().getAttrId());

      createOrUpdateVarAttr(varAttrToCopy, varAttrToUpdate);
    }

  }

  /**
   * Method to fetch list of pidc variant attributes for given variant id. The map also contains deleted and
   * invisible(due to attr dependencies) objects
   *
   * @param variantId
   * @param loader
   * @param
   * @return map of variant attributes for given variant id Key - attr id, value - pidc variant attr obj
   * @throws DataException
   */
  private Map<Long, PidcVariantAttribute> getVarAttrMapForVariantId(final PidcVariantLoader loader,
      final PidcVariant variant)
      throws DataException {
    Map<Long, PidcVariantAttribute> varAttrMap = new HashMap<>();
    List<TabvVariantsAttr> variantsAttrEntities = loader.getEntityObject(variant.getId()).getTabvVariantsAttrs();
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
   * Update the variant attribute
   *
   * @param varAttrToCopy reference attribute
   * @param varAttrToUpdate attribute to update
   * @throws IcdmException
   */
  private void updateVariantAttr(final PidcVariantAttribute varAttrToCopy, final PidcVariantAttribute varAttrToUpdate)
      throws IcdmException {

    varAttrToUpdate.setUsedFlag(varAttrToCopy.getUsedFlag());
    varAttrToUpdate.setValueId(varAttrToCopy.getValueId());
    varAttrToUpdate.setPartNumber(varAttrToCopy.getPartNumber());
    varAttrToUpdate.setSpecLink(varAttrToCopy.getSpecLink());
    varAttrToUpdate.setAdditionalInfoDesc(varAttrToCopy.getAdditionalInfoDesc());
    varAttrToUpdate.setAttrId(varAttrToCopy.getAttrId());
    varAttrToUpdate.setAttrHidden(varAttrToCopy.isAttrHidden());
    varAttrToUpdate.setAtChildLevel(varAttrToCopy.isAtChildLevel());
    PidcVariantAttributeCommand pidcVarAttrCmd =
        new PidcVariantAttributeCommand(getServiceData(), varAttrToUpdate, true, false);
    executeChildCommand(pidcVarAttrCmd);

  }

  /**
   * @param pidcAttr
   * @param varAttrToUpdate
   * @throws IcdmException
   */
  private void createOrUpdateVarAttr(final PidcVariantAttribute varAttrToCopy,
      final PidcVariantAttribute varAttrToUpdate)
      throws IcdmException {

    if (varAttrToUpdate == null) {
      createVariantAttr(varAttrToCopy);
    }
    else {
      if (isAttrValEmpty(varAttrToUpdate) || isVarAttrValidForUpdate(varAttrToCopy, varAttrToUpdate)) {
        updateVariantAttr(varAttrToCopy, varAttrToUpdate);
      }
      else if (!this.pidcVarData.isOverrideAll() && !this.pidcVarData.isOverrideAllVarAttr()) {
        this.valAlreadyExists.add(varAttrToUpdate);
      }
    }

  }

  /**
   * @param varAttrToCopy
   * @param varAttrToUpdate
   * @return
   */
  private boolean isVarAttrValidForUpdate(final PidcVariantAttribute varAttrToCopy,
      final PidcVariantAttribute varAttrToUpdate) {
    boolean isVarAttrValidForUpdate = false;
    if (toBeOverride(varAttrToUpdate, varAttrToCopy) || this.predefAttrAllowedForCopy) {
      isVarAttrValidForUpdate = true;
    }
    return isVarAttrValidForUpdate;
  }


  /**
   * @param varAttrToUpdate
   * @param varAttrToCopy
   * @return
   */
  private boolean toBeOverride(final PidcVariantAttribute varAttrToUpdate, final PidcVariantAttribute varAttrToCopy) {
    boolean isToOverride = false;
    if ((this.pidcVarData.isOverrideAll() || canOverrideVarAttr(varAttrToUpdate)) && isNotPverAttr(varAttrToCopy)) {
      isToOverride = true;
    }
    return isToOverride;
  }

  /**
   * @param varAttrToCopy
   * @return
   */
  private boolean isNotPverAttr(final PidcVariantAttribute varAttrToCopy) {
    return !(this.pidcVersWithDetails.getAllAttributeMap().get(varAttrToCopy.getAttrId()).getLevel()
        .equals(Long.valueOf(ApicConstants.SDOM_PROJECT_NAME_ATTR)));
  }

  /**
   * @param varAttrToUpdate
   * @return
   */
  private boolean canOverrideVarAttr(final PidcVariantAttribute varAttrToUpdate) {
    return (this.pidcVarData.isOverrideAllVarAttr() &&
        !(varAttrToUpdate.getValue().equals(ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME)));
  }


  /**
   * @param varAttrToUpdate
   * @return
   */
  private boolean isAttrValEmpty(final PidcVariantAttribute varAttrToUpdate) {
    boolean isAttrValEmpty = false;
    if (CommonUtils.isNull(varAttrToUpdate.getValueId()) && CommonUtils.isEmptyString(varAttrToUpdate.getValue())) {
      isAttrValEmpty = true;
    }
    return isAttrValEmpty;
  }


  /**
   * Create the PIDC Variant attribute
   *
   * @param varAttrToCopy Variant attribute to copy
   * @throws IcdmException
   */
  private void createVariantAttr(final IProjectAttribute varAttrToCopy) throws IcdmException {

    PidcVariantAttribute newPIDCVarAttr = new PidcVariantAttribute();
    newPIDCVarAttr.setUsedFlag(varAttrToCopy.getUsedFlag());
    newPIDCVarAttr.setValueId(varAttrToCopy.getValueId());
    newPIDCVarAttr.setPartNumber(varAttrToCopy.getPartNumber());
    newPIDCVarAttr.setSpecLink(varAttrToCopy.getSpecLink());
    newPIDCVarAttr.setAdditionalInfoDesc(varAttrToCopy.getAdditionalInfoDesc());
    newPIDCVarAttr.setAttrId(varAttrToCopy.getAttrId());
    newPIDCVarAttr.setVariantId(this.destVariant.getId());
    newPIDCVarAttr.setPidcVersionId(this.destVariant.getPidcVersionId());
    newPIDCVarAttr.setAttrHidden(varAttrToCopy.isAttrHidden());
    PidcVariantAttributeCommand pidcVarAttrCmd =
        new PidcVariantAttributeCommand(getServiceData(), newPIDCVarAttr, false, false);
    executeChildCommand(pidcVarAttrCmd);
  }

  /**
   * Checks whether the Variant attribute can be copied to the new Variant
   *
   * @param pidcAttr the Variant attribute to copy
   * @return true/false
   * @throws DataException
   */
  private boolean isValidVarAttr(final TabvVariantsAttr pidcAttr) throws DataException {

    PidcVariantAttributeLoader pidcVarAttrLoader = new PidcVariantAttributeLoader(getServiceData());
    PidcVariantAttribute varAttr = pidcVarAttrLoader.getDataObjectByID(pidcAttr.getVarAttrId());
    if (varAttr.isAtChildLevel()) {
      return false;
    }
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    Attribute attr = attrLoader.getDataObjectByID(pidcAttr.getTabvAttribute().getAttrId());
    final int attrLevel = attr.getLevel().intValue();
    // Ignore name, variant attributes
    return attrLevel != ApicConstants.VARIANT_CODE_ATTR;
  }

  /**
   * Create the Variant
   *
   * @throws IcdmException
   */
  private void createVariant() throws IcdmException {
    PidcVariantCommand pidcVarCommand = new PidcVariantCommand(getServiceData(), this.pidcVarData, false);
    executeChildCommand(pidcVarCommand);

    this.destVariant = pidcVarCommand.getNewData();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {

    if (this.pidcVarData.isUndoDeleteNUpdateVar()) {
      undoDeleteNUpdateVariant();
    }

    this.valAlreadyExists = new ArrayList<>();

    PidcVariantLoader pidcVarloader = new PidcVariantLoader(getServiceData());
    PidcVariant srcPidcVar = this.pidcVarData.getSrcPidcVar();
    TabvProjectVariant srcVarEntity = pidcVarloader.getEntityObject(srcPidcVar.getId());

    PidcVariantAttributeLoader pidcVarAttrLoader = new PidcVariantAttributeLoader(getServiceData());
    // get all the attributes of destination variant
    Map<Long, PidcVariantAttribute> pidcVarAttrMap = getVarAttrMapForVariantId(pidcVarloader, this.destVariant);

    this.pidcVersWithDetails = getPidcVersionWithDetails(this.pidcVarData.getPidcVersion().getId());
    List<Long> attrToRemoveList = new ArrayList<>();

    for (TabvVariantsAttr pidcAttr : srcVarEntity.getTabvVariantsAttrs()) {
      PidcVariantAttribute varAttrToCopy = pidcVarAttrLoader.getDataObjectByID(pidcAttr.getVarAttrId());

      if ((!this.pidcVarData.isSubVarCopiedAlongWithVar() && !isValidVarAttr(pidcAttr)) ||
          isSrcAttrInvisible(varAttrToCopy)) {
        continue;
      }

      // ICDM-1780
      final PidcVariantAttribute varAttrToUpdate = pidcVarAttrMap.get(pidcAttr.getTabvAttribute().getAttrId());

      if (CommonUtils.isNotNull(varAttrToUpdate)) {
        handleGroupAttribute(varAttrToUpdate, pidcVarAttrMap);
        // adding the attributes in destination to remove from pidcVarAttrMap after updating
        attrToRemoveList.add(varAttrToUpdate.getAttrId());
      }

      createOrUpdateVarAttr(varAttrToCopy, varAttrToUpdate);
    }

    // removing the updated attributes
    for (Long attrToRemove : attrToRemoveList) {
      pidcVarAttrMap.remove(attrToRemove);
    }

    // delete the variant attributes which are not in source variant but in destination variant
    if (CommonUtils.isNotEmpty(pidcVarAttrMap) && this.pidcVarData.isOverrideAll()) {
      deleteAttributesNotInSourceVar(pidcVarAttrMap);
    }
  }


  private boolean isSrcAttrInvisible(final PidcVariantAttribute srcVarAttr) {
    boolean isAttrInvisible = false;
    Set<Long> varInvisibleAttrSet =
        this.pidcVersWithDetails.getVariantInvisbleAttributeMap().get(srcVarAttr.getVariantId());
    return CommonUtils.isNotEmpty(varInvisibleAttrSet) ? varInvisibleAttrSet.contains(srcVarAttr.getAttrId())
        : isAttrInvisible;
  }

  /**
   * @param pidcVersionId
   * @throws IcdmException
   */
  private PidcVersionWithDetails getPidcVersionWithDetails(final Long pidcVersionId) throws IcdmException {
    PidcVersionLoader pidcVerLoader = new PidcVersionLoader(getServiceData());
    return pidcVerLoader.getPidcVersionWithDetails(pidcVersionId);
  }

  /**
   * @param varAttr
   * @param pidcDestVarAttrMap
   * @throws DataException
   */
  private void handleGroupAttribute(final PidcVariantAttribute varAttr,
      final Map<Long, PidcVariantAttribute> pidcDestVarAttrMap)
      throws DataException {
    // when user select not to override,the value for grp attr will be copied if it has no value initially. In this
    // case, if predef has some other value then this leads to inconsistent data so overriding predef valin this case.
    this.predefAttrAllowedForCopy = false;
    // check if the attribute is predefined attribute
    if (isPredefAttribute(varAttr)) {
      AttributeValue attributeValue = this.pidcVersWithDetails.getAttributeValueMap().get(this.grpAttrValId);
      Long destVarGrpAttrId = CommonUtils.isNotNull(attributeValue) ? attributeValue.getAttributeId() : null;

      // check whether the variant which has predefined attribute, also has grouped attribute within it
      // if group attr has no value & predefined attr has value, then override the predefined attr value
      if (CommonUtils.isNotNull(destVarGrpAttrId) && pidcDestVarAttrMap.containsKey(destVarGrpAttrId) &&
          CommonUtils.isNotNull(varAttr.getValue()) &&
          CommonUtils.isNull(pidcDestVarAttrMap.get(destVarGrpAttrId).getValue())) {
        this.predefAttrAllowedForCopy = true;

      }
    }
  }


  private boolean isPredefAttribute(final PidcVariantAttribute varAttr) throws DataException {
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
   * @param pidcVarAttrMap pidc attribute map
   * @throws IcdmException exception
   */
  public void deleteAttributesNotInSourceVar(final Map<Long, PidcVariantAttribute> pidcVarAttrMap)
      throws IcdmException {

    Map<Long, Map<Long, PidcVariantAttribute>> pidcVariantAttributeDeleteMap = new HashMap<>();
    pidcVariantAttributeDeleteMap.put(this.destVariant.getId(), pidcVarAttrMap);

    ProjectAttributesUpdationModel attrUpdationModel = new ProjectAttributesUpdationModel();
    attrUpdationModel.setPidcVariantAttributeDeletedMap(pidcVariantAttributeDeleteMap);

    ProjectAttributesUpdationCommand mainCmd =
        new ProjectAttributesUpdationCommand(getServiceData(), attrUpdationModel);
    executeChildCommand(mainCmd);
  }


  /**
   * Update the Variant
   *
   * @throws IcdmException
   */
  private void undoDeleteNUpdateVariant() throws IcdmException {
    PidcVariantCommand pidcVarCommand = new PidcVariantCommand(getServiceData(), this.pidcVarData, true);
    executeChildCommand(pidcVarCommand);
    // execute update to proceed with further process
    getEm().flush();

    this.destVariant = pidcVarCommand.getNewData();
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
  public PidcVariant getDestVariant() {
    return this.destVariant;
  }

  /**
   * @return List<PidcVariantAttribute>
   */
  public List<PidcVariantAttribute> getValAlreadyExists() {
    return new ArrayList<>(this.valAlreadyExists);
  }
}
