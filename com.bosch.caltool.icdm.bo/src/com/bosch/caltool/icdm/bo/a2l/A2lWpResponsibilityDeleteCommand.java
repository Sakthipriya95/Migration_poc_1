/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpParamMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMappingUpdateModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpRespDeleteModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * @author pdh2cob
 */
public class A2lWpResponsibilityDeleteCommand extends AbstractSimpleCommand {


  /**
   * Set of ids to delete
   */
  private final Set<Long> a2lWpResponsibilityIds;

  /**
   * Model with deleted A2lWpResp and updated A2lWpParamMappings
   */
  private final A2lWpRespDeleteModel a2lWpRespDeleteModel = new A2lWpRespDeleteModel();

  private boolean isResetWorkSplit;

  private A2lWpDefnVersion workingSetVersion;

  private List<A2lVariantGroup> deleteVarGroupsList = new ArrayList<>();

  /**
   * @param serviceData service data
   * @param a2lWpResponsibilityIds input set of ids
   * @throws IcdmException exception
   */
  public A2lWpResponsibilityDeleteCommand(final ServiceData serviceData, final Set<Long> a2lWpResponsibilityIds)
      throws IcdmException {
    super(serviceData);
    this.a2lWpResponsibilityIds = new HashSet<>(a2lWpResponsibilityIds);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {

    // If isResetWorkSplit flag is set to true, create new A2L wp definition version before resetting all the wp params
    // with _DEFAULT_WP
    if (isResetWorkSplit()) {
      performResetWorkSplit();
    }

    A2lWpResponsibilityLoader wpRespLoader = new A2lWpResponsibilityLoader(getServiceData());
    A2lWpParamMappingLoader a2lWpParamMappingLoader = new A2lWpParamMappingLoader(getServiceData());

    for (Long a2lWpResponsibilityId : this.a2lWpResponsibilityIds) {


      // delete or update param mappings first, then delete the A2lWpResponsibility
      A2lWpParamMappingUpdateModel a2lWpParamMappingUpdateModel = new A2lWpParamMappingUpdateModel();

      TA2lWpResponsibility ta2lWpResponsibility = wpRespLoader.getEntityObject(a2lWpResponsibilityId);


      if (ta2lWpResponsibility.getA2lWp().getWpName().equals(ApicConstants.DEFAULT_A2L_WP_NAME)) {
        continue;
      }

      // fetch all param mappings, add respective maps
      for (TA2lWpParamMapping tA2lWpParamMapping : ta2lWpResponsibility.getTA2lWpParamMappings()) {

        // if mapping is at pidc level, update the mapping to default wp
        if (tA2lWpParamMapping.getTA2lWpResponsibility().getVariantGroup() == null) {
          A2lWpResponsibility defaultWpResp = wpRespLoader.getDefaultWpRespPal(
              tA2lWpParamMapping.getTA2lWpResponsibility().gettA2lWpDefnVersion().getWpDefnVersId());
          A2lWpParamMapping a2lWpParamMapping = a2lWpParamMappingLoader.createDataObject(tA2lWpParamMapping);
          a2lWpParamMapping.setWpRespId(defaultWpResp.getId());
          a2lWpParamMapping.setParA2lRespId(null);
          a2lWpParamMapping.setWpRespInherited(true);
          a2lWpParamMappingUpdateModel.getA2lWpParamMappingToBeUpdated().put(a2lWpParamMapping.getId(),
              a2lWpParamMapping);
        }
        else {
          // if mapping is at variant level , delete it
          A2lWpParamMapping a2lWpParamMapping = a2lWpParamMappingLoader.createDataObject(tA2lWpParamMapping);
          a2lWpParamMappingUpdateModel.getA2lWpParamMappingToBeDeleted().put(a2lWpParamMapping.getId(),
              a2lWpParamMapping);

        }
      }

      // update mappings
      A2lWpParamMappingUpdateCommand a2lWpParamMappingUpdateCommand =
          new A2lWpParamMappingUpdateCommand(getServiceData(), a2lWpParamMappingUpdateModel);
      executeChildCommand(a2lWpParamMappingUpdateCommand);


      // delete A2lWpResponsibility

      A2lWpResponsibility a2lWpResponsibility = wpRespLoader.getDataObjectByID(a2lWpResponsibilityId);
      A2lWpResponsibilityCommand a2lWpResponsibilityCommand =
          new A2lWpResponsibilityCommand(getServiceData(), a2lWpResponsibility, false, true);
      executeChildCommand(a2lWpResponsibilityCommand);

      // set deleted wp resp data
      this.a2lWpRespDeleteModel.getDeletedA2lWpResponsibilitySet().add(a2lWpResponsibility);

      // set updated,deleted data to deleteModel
      for (A2lWpParamMapping a2lWpParamMapping : a2lWpParamMappingUpdateModel.getA2lWpParamMappingToBeUpdated()
          .values()) {
        this.a2lWpRespDeleteModel.getUpdatedA2lWpParamMapping().put(a2lWpParamMapping.getId(),
            a2lWpParamMappingLoader.getDataObjectByID(a2lWpParamMapping.getId()));
      }

      for (A2lWpParamMapping a2lWpParamMapping : a2lWpParamMappingUpdateModel.getA2lWpParamMappingToBeDeleted()
          .values()) {
        this.a2lWpRespDeleteModel.getDeletedA2lWpParamMapping().put(a2lWpParamMapping.getId(), a2lWpParamMapping);
      }
    }

    deleteVariantGroups();

  }

  /**
   * @throws IcdmException
   */
  private void performResetWorkSplit() throws IcdmException {

    // Create new A2LWPDefinition version
    A2lWpDefnVersion a2lWpDefnVersion = new A2lWpDefnVersion();
    a2lWpDefnVersion.setVersionName("Version created before resetting work split");
    a2lWpDefnVersion
        .setDescription("WP definition Version created before resetting all the wp params with _DEFAULT_WP");
    a2lWpDefnVersion.setActive(true);
    a2lWpDefnVersion.setParamLevelChgAllowedFlag(this.workingSetVersion.isParamLevelChgAllowedFlag());
    a2lWpDefnVersion.setPidcA2lId(this.workingSetVersion.getPidcA2lId());

    // Command to create new active A2LWPDefinition version
    A2lWpDefinitionActiveVersionCommand activeVersCmd =
        new A2lWpDefinitionActiveVersionCommand(getServiceData(), a2lWpDefnVersion);
    executeChildCommand(activeVersCmd);
  }

  /**
   * @throws IcdmException
   */
  private void deleteVariantGroups() throws IcdmException {

    // Command class to delete the variant groups
    for (A2lVariantGroup deleVarGroup : this.deleteVarGroupsList) {
      A2lVariantGroupCommand cmd = new A2lVariantGroupCommand(getServiceData(), deleVarGroup, false, true);
      executeChildCommand(cmd);
    }
  }

  /**
   * @return the a2lWpRespDeleteModel
   */
  public A2lWpRespDeleteModel getA2lWpRespDeleteModel() {
    return this.a2lWpRespDeleteModel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // No Implementation

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

  /**
   * @return the isResetWorkSplit
   */
  public boolean isResetWorkSplit() {
    return this.isResetWorkSplit;
  }

  /**
   * @param isResetWorkSplit the isResetWorkSplit to set
   */
  public void setResetWorkSplit(final boolean isResetWorkSplit) {
    this.isResetWorkSplit = isResetWorkSplit;
  }

  /**
   * @return the workingSetVersion
   */
  public A2lWpDefnVersion getWorkingSetVersion() {
    return this.workingSetVersion;
  }

  /**
   * @param workingSetVersion the workingSetVersion to set
   */
  public void setWorkingSetVersion(final A2lWpDefnVersion workingSetVersion) {
    this.workingSetVersion = workingSetVersion;
  }


  /**
   * @return the deleteVarGroups
   */
  public List<A2lVariantGroup> getDeleteVarGroups() {
    return this.deleteVarGroupsList;
  }


  /**
   * @param deleteVarGroups the deleteVarGroups to set
   */
  public void setDeleteVarGroups(final List<A2lVariantGroup> deleteVarGroups) {
    this.deleteVarGroupsList = deleteVarGroups;
  }
}
