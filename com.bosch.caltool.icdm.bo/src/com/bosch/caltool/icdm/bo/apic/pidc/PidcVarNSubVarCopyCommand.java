/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantData;

/**
 * @author ukt1cob
 */
public class PidcVarNSubVarCopyCommand extends AbstractCommand<PidcVariant, PidcVariantLoader> {

  /**
   * PidcVariantCopyData
   */
  private final PidcVariantData pidcVarData;
  /**
   * PidcVariant
   */
  private PidcVariant destVariant;
  /**
   * ArrayList<PidcVariantAttribute>
   */
  private List<PidcVariantAttribute> valAlreadyExistsForVariant;

  private Map<String, List<PidcSubVariantAttribute>> valAlreadyExistsForSubVarMap;

  /**
   * @param serviceData service data
   * @param inputData input data
   * @param isCreateNewVar if true,create new variant
   * @throws IcdmException exception
   */
  public PidcVarNSubVarCopyCommand(final ServiceData serviceData, final PidcVariantData inputData,
      final boolean isCreateNewVar) throws IcdmException {
    super(serviceData, inputData.getDestPidcVar(), new PidcVariantLoader(serviceData),
        isCreateNewVar ? COMMAND_MODE.CREATE : COMMAND_MODE.UPDATE);
    this.pidcVarData = inputData;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {

    createOrUpdateVariant(this.pidcVarData, true);

    setValAlreadyExistsForSubVarMap(new HashMap<String, List<PidcSubVariantAttribute>>());
    for (PidcSubVariant pidcSubVariantToBeCopied : this.pidcVarData.getSrcSubVarSet()) {

      AttributeValueLoader loader = new AttributeValueLoader(getServiceData());
      AttributeValue subVarAttrVal = loader.getDataObjectByID(pidcSubVariantToBeCopied.getNameValueId());

      PidcSubVariantData pidcSubVarData = new PidcSubVariantData();
      pidcSubVarData.setSrcPidcSubVar(pidcSubVariantToBeCopied);
      pidcSubVarData.setSubvarNameAttrValue(subVarAttrVal);
      pidcSubVarData.setPidcVariantId(getDestVariant().getId());
      pidcSubVarData.setPidcVersionId(this.pidcVarData.getPidcVersion().getId());
      pidcSubVarData.setSubVarCopiedAlongWithVariant(true);

      createOrUpdateSubVariant(pidcSubVarData, true);
    }

  }


  /**
   * @param pidcVariantData
   * @param isCreate
   * @throws IcdmException
   */
  private void createOrUpdateVariant(final PidcVariantData pidcVariantData, final boolean isCreate)
      throws IcdmException {
    PidcVarCopyCommand pidcVarCopyCommand = new PidcVarCopyCommand(getServiceData(), pidcVariantData, isCreate);
    executeChildCommand(pidcVarCopyCommand);

    this.destVariant = pidcVarCopyCommand.getDestVariant();
    this.valAlreadyExistsForVariant = pidcVarCopyCommand.getValAlreadyExists();
  }


  /**
   * @param pidcSubVarData
   * @param isCreate
   * @throws IcdmException
   */
  private void createOrUpdateSubVariant(final PidcSubVariantData pidcSubVarData, final boolean isCreate)
      throws IcdmException {
    PidcSubVarCopyCommand pidcSubVarCopyCommand = new PidcSubVarCopyCommand(getServiceData(), pidcSubVarData, isCreate);
    executeChildCommand(pidcSubVarCopyCommand);

    getValAlreadyExistsForSubVarMap().put((CommonUtils.isNotNull(pidcSubVarData.getDestPidcSubVar())
        ? pidcSubVarData.getDestPidcSubVar().getName() : pidcSubVarData.getSubvarNameAttrValue().getName()),
        pidcSubVarCopyCommand.getValAlreadyExists());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {

    PidcSubVariantLoader subVarLoader = new PidcSubVariantLoader(getServiceData());

    createOrUpdateVariant(this.pidcVarData, false);


    Map<Long, PidcSubVariant> subVariantToUpdateMap = new HashMap<Long, PidcSubVariant>();
    for (PidcSubVariant subVariantToUpdate : subVarLoader.getSubVariants(getDestVariant().getId(), true).values()) {
      subVariantToUpdateMap.put(subVariantToUpdate.getNameValueId(), subVariantToUpdate);
    }

    setValAlreadyExistsForSubVarMap(new HashMap<String, List<PidcSubVariantAttribute>>());
    for (PidcSubVariant pidcSubVariantToBeCopied : this.pidcVarData.getSrcSubVarSet()) {

      PidcSubVariant pidcSubVarToUpdate = subVariantToUpdateMap.get(pidcSubVariantToBeCopied.getNameValueId());
      PidcSubVariantData pidcSubVarData = new PidcSubVariantData();
      pidcSubVarData.setSrcPidcSubVar(pidcSubVariantToBeCopied);
      pidcSubVarData.setDestPidcSubVar(pidcSubVarToUpdate);
      pidcSubVarData.setPidcVariantId(getDestVariant().getId());
      pidcSubVarData.setPidcVersionId(this.pidcVarData.getPidcVersion().getId());
      pidcSubVarData.setSubVarCopiedAlongWithVariant(true);
      if (this.pidcVarData.isUndoDeleteNUpdateSubVar() &&
          (CommonUtils.isNotNull(pidcSubVarToUpdate) && pidcSubVarToUpdate.isDeleted())) {
        pidcSubVarToUpdate.setDeleted(false);
        pidcSubVarData.setUndoDeleteNUpdateSubVar(true);
      }
      if (this.pidcVarData.isOverrideAll()) {
        pidcSubVarData.setOverrideAll(true);
      }

      createOrUpdateSubVar(pidcSubVarToUpdate, pidcSubVarData, pidcSubVariantToBeCopied);
    }
    // update sub-variants which is not available in source variant
    updateDestSubVar();

  }


  /**
   * @param pidcSubVarToUpdate destination sub variant
   * @param pidcSubVarData sub variant data
   * @param pidcSubVariantToBeCopied sub variant to be copied
   * @throws IcdmException exception
   */
  public void createOrUpdateSubVar(final PidcSubVariant pidcSubVarToUpdate, final PidcSubVariantData pidcSubVarData,
      final PidcSubVariant pidcSubVariantToBeCopied)
      throws IcdmException {
    // sub-variant available in source variant not in destination variant
    if (CommonUtils.isNull(pidcSubVarToUpdate)) {
      AttributeValueLoader loader = new AttributeValueLoader(getServiceData());
      AttributeValue subVarAttrVal = loader.getDataObjectByID(pidcSubVariantToBeCopied.getNameValueId());

      pidcSubVarData.setSubvarNameAttrValue(subVarAttrVal);
      pidcSubVarData.setToUpdateSrcSubVarNotInDestVar(true);
      createOrUpdateSubVariant(pidcSubVarData, true);
    }
    else if (((CommonUtils.isNotNull(pidcSubVarToUpdate) && (!pidcSubVarToUpdate.isDeleted())) ||
        this.pidcVarData.isUndoDeleteNUpdateSubVar())) {
      createOrUpdateSubVariant(pidcSubVarData, false);
    }

  }

  /**
   * @throws IcdmException exception
   */
  public void updateDestSubVar() throws IcdmException {
    final SortedSet<Long> sourcePidcSubVariantIdSet = new TreeSet<>();
    PidcSubVariantLoader subVarLoader = new PidcSubVariantLoader(getServiceData());

    for (PidcSubVariant sourcePidcSubVariant : this.pidcVarData.getSrcSubVarSet()) {
      sourcePidcSubVariantIdSet.add(sourcePidcSubVariant.getNameValueId());
    }

    // updating all the sub-variant which is in destination variant but not in source variant
    for (PidcSubVariant destPidcSubVariant : subVarLoader.getSubVariants(getDestVariant().getId(), true).values()) {

      if (!(sourcePidcSubVariantIdSet.contains(destPidcSubVariant.getNameValueId()))) {
        // send any one source variant's sub-variant to update the attribute(s) of dest sub variant
        // update only sub-var's attribute if destPidcSubVariant.isDeleted() == true. Deleted flag should remain true
        PidcSubVariantData pidcSubVarData = new PidcSubVariantData();
        pidcSubVarData.setSrcPidcSubVar(this.pidcVarData.getSrcSubVarSet().first());
        pidcSubVarData.setDestPidcSubVar(destPidcSubVariant);
        pidcSubVarData.setPidcVariantId(getDestVariant().getId());
        pidcSubVarData.setPidcVersionId(this.pidcVarData.getPidcVersion().getId());
        pidcSubVarData.setSubVarCopiedAlongWithVariant(true);
        pidcSubVarData.setFlagToUpdateDestSubVar(true);
        if (this.pidcVarData.isOverrideAll()) {
          pidcSubVarData.setOverrideAll(true);
        }
        createOrUpdateSubVariant(pidcSubVarData, false);
      }
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // Auto-generated method stub

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
    // Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // Auto-generated method stub

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
  public List<PidcVariantAttribute> getValAlreadyExistsForVariant() {
    return new ArrayList<>(this.valAlreadyExistsForVariant);
  }


  /**
   * @return the valAlreadyExistsForSubVarMap
   */
  public Map<String, List<PidcSubVariantAttribute>> getValAlreadyExistsForSubVarMap() {
    return this.valAlreadyExistsForSubVarMap;
  }


  /**
   * @param valAlreadyExistsForSubVarMap the valAlreadyExistsForSubVarMap to set
   */
  public void setValAlreadyExistsForSubVarMap(
      final Map<String, List<PidcSubVariantAttribute>> valAlreadyExistsForSubVarMap) {
    this.valAlreadyExistsForSubVarMap = valAlreadyExistsForSubVarMap;
  }
}
