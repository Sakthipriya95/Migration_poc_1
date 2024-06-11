/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.wp;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TWpResource;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackage;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;


/**
 * @author bne4cob
 */
public class WorkPackageDivisionCommand extends AbstractCommand<WorkPackageDivision, WorkPackageDivisionLoader> {

  /**
   * Constructor for creating a WorkPackageDivision
   *
   * @param serviceData service Data
   * @param inputData WorkPackageDivision to create
   * @throws IcdmException any exception
   */
  public WorkPackageDivisionCommand(final ServiceData serviceData, final WorkPackageDivision inputData)
      throws IcdmException {
    super(serviceData, inputData, new WorkPackageDivisionLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * Constructor for updating/delete a WorkPackageDivision
   *
   * @param serviceData service Data
   * @param inputData WorkPackageDivision to update
   * @param update if true updates
   * @throws IcdmException any exception
   */
  public WorkPackageDivisionCommand(final ServiceData serviceData, final WorkPackageDivision inputData,
      final boolean update) throws IcdmException {
    super(serviceData, inputData, new WorkPackageDivisionLoader(serviceData),
        update ? COMMAND_MODE.UPDATE : COMMAND_MODE.DELETE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TWorkpackageDivision dbWorkPackageDiv = new TWorkpackageDivision();

    UserLoader userLdr = new UserLoader(getServiceData());
    dbWorkPackageDiv.setTabvApicContactUser(userLdr.getEntityObject(getInputData().getContactPersonId()));
    dbWorkPackageDiv.setTabvApicSecondContactUser(userLdr.getEntityObject(getInputData().getContactPersonSecondId()));
    dbWorkPackageDiv
        .setTabvAttrValue(new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getDivAttrValId()));
    dbWorkPackageDiv.setTWorkpackage(getEm().find(TWorkpackage.class, getInputData().getWpId()));
    if (null != getInputData().getWpResId()) {
      dbWorkPackageDiv.setTWpResource(getEm().find(TWpResource.class, getInputData().getWpResId()));
    }
    else {
      dbWorkPackageDiv.setTWpResource(null);
    }
    dbWorkPackageDiv.setWpIdMcr(getInputData().getWpIdMcr());
    dbWorkPackageDiv.setWpdComment(getInputData().getWpdComment());
    dbWorkPackageDiv.setIccRelevantFlag(getInputData().getIccRelevantFlag());
    dbWorkPackageDiv.setCrpObdRelevantFlag(
        isCrpObdRelevantFlagValid() ? getInputData().getCrpObdRelevantFlag() : CommonUtilConstants.CODE_NO);
    dbWorkPackageDiv.setCrpObdComment(getInputData().getCrpObdComment());
    dbWorkPackageDiv.setCrpEmissionRelevantFlag(
        isCrpEmissionRelevantFlagValid() ? getInputData().getCrpEmissionRelevantFlag() : CommonUtilConstants.CODE_NO);
    dbWorkPackageDiv.setCrpEmissionComment(getInputData().getCrpEmissionComment());
    dbWorkPackageDiv.setCrpSoundRelevantFlag(
        isCrpSoundRelevantFlagValid() ? getInputData().getCrpSoundRelevantFlag() : CommonUtilConstants.CODE_NO);
    dbWorkPackageDiv.setCrpSoundComment(getInputData().getCrpSoundComment());

    TWorkpackage dbWorkPackage = new WorkPkgLoader(getServiceData()).getEntityObject(getInputData().getWpId());
    dbWorkPackage.addTWorkpackageDivisions(dbWorkPackageDiv);

    setUserDetails(COMMAND_MODE.CREATE, dbWorkPackageDiv);

    persistEntity(dbWorkPackageDiv);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    TWorkpackageDivision dbWorkPackageDiv = getEm().find(TWorkpackageDivision.class, getInputData().getId());

    UserLoader userLdr = new UserLoader(getServiceData());

    if (!CommonUtils.isEqual(getOldData().getContactPersonId(), getInputData().getContactPersonId())) {
      dbWorkPackageDiv.setTabvApicContactUser(userLdr.getEntityObject(getInputData().getContactPersonId()));
    }
    if (!CommonUtils.isEqual(getOldData().getContactPersonSecondId(), getInputData().getContactPersonSecondId())) {
      dbWorkPackageDiv.setTabvApicSecondContactUser(userLdr.getEntityObject(getInputData().getContactPersonSecondId()));
    }
    if (!CommonUtils.isEqual(getOldData().getDivAttrValId(), getInputData().getDivAttrValId())) {
      dbWorkPackageDiv.setTabvAttrValue(getEm().find(TabvAttrValue.class, getInputData().getDivAttrValId()));
    }
    if (!CommonUtils.isEqual(getOldData().getWpId(), getInputData().getWpId())) {
      dbWorkPackageDiv.setTWorkpackage(getEm().find(TWorkpackage.class, getInputData().getWpId()));
    }
    if (!CommonUtils.isEqual(getOldData().getWpResId(), getInputData().getWpResId())) {
      if (null != getInputData().getWpResId()) {
        dbWorkPackageDiv.setTWpResource(getEm().find(TWpResource.class, getInputData().getWpResId()));
      }
      else {
        dbWorkPackageDiv.setTWpResource(null);
      }
    }
    if (!CommonUtils.isEqual(getOldData().getWpIdMcr(), getInputData().getWpIdMcr())) {
      dbWorkPackageDiv.setWpIdMcr(getInputData().getWpIdMcr());
    }
    if (!CommonUtils.isEqual(getOldData().getDeleted(), getInputData().getDeleted())) {
      dbWorkPackageDiv.setDeleteFlag(getInputData().getDeleted());
    }
    if (!CommonUtils.isEqual(getOldData().getWpdComment(), getInputData().getWpdComment())) {
      dbWorkPackageDiv.setWpdComment(getInputData().getWpdComment());
    }
    if (!CommonUtils.isEqual(getOldData().getIccRelevantFlag(), getInputData().getIccRelevantFlag())) {
      dbWorkPackageDiv.setIccRelevantFlag((getInputData().getIccRelevantFlag()));
    }
    updateComplianceData(dbWorkPackageDiv);
    setUserDetails(COMMAND_MODE.UPDATE, dbWorkPackageDiv);
  }

  private void updateComplianceData(final TWorkpackageDivision dbWorkPackageDiv) {
    if (!CommonUtils.isEqual(getOldData().getCrpObdRelevantFlag(), getInputData().getCrpObdRelevantFlag())) {
      dbWorkPackageDiv.setCrpObdRelevantFlag(
          isCrpObdRelevantFlagValid() ? getInputData().getCrpObdRelevantFlag() : CommonUtilConstants.CODE_NO);
    }
    if (!CommonUtils.isEqual(getOldData().getCrpObdComment(), getInputData().getCrpObdComment())) {
      dbWorkPackageDiv.setCrpObdComment(getInputData().getCrpObdComment());
    }
    if (!CommonUtils.isEqual(getOldData().getCrpEmissionRelevantFlag(), getInputData().getCrpEmissionRelevantFlag())) {
      dbWorkPackageDiv.setCrpEmissionRelevantFlag(
          isCrpEmissionRelevantFlagValid() ? getInputData().getCrpEmissionRelevantFlag() : CommonUtilConstants.CODE_NO);
    }
    if (!CommonUtils.isEqual(getOldData().getCrpEmissionComment(), getInputData().getCrpEmissionComment())) {
      dbWorkPackageDiv.setCrpEmissionComment(getInputData().getCrpEmissionComment());
    }
    if (!CommonUtils.isEqual(getOldData().getCrpSoundRelevantFlag(), getInputData().getCrpSoundRelevantFlag())) {
      dbWorkPackageDiv.setCrpSoundRelevantFlag(
          isCrpSoundRelevantFlagValid() ? getInputData().getCrpSoundRelevantFlag() : CommonUtilConstants.CODE_NO);
    }
    if (!CommonUtils.isEqual(getOldData().getCrpSoundComment(), getInputData().getCrpSoundComment())) {
      dbWorkPackageDiv.setCrpSoundComment(getInputData().getCrpSoundComment());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    /**
     * Method not implemented
     */
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // No action
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return isDataChangedContactPerson() || isDataChangedWpIdWpResIdWpIdMcr() ||
        isDataChangesDivAttrValIdDelFlagComment() || isDataChangedIccRelevantFlag() || isComplianceDataChanged();
  }

  /**
   * @return
   */
  private boolean isDataChangesDivAttrValIdDelFlagComment() {
    return !CommonUtils.isEqual(getOldData().getDivAttrValId(), getInputData().getDivAttrValId()) ||
        !CommonUtils.isEqual(getOldData().getDeleted(), getInputData().getDeleted()) ||
        !CommonUtils.isEqual(getOldData().getWpdComment(), getInputData().getWpdComment());
  }

  /**
   * @return
   */
  private boolean isDataChangedWpIdWpResIdWpIdMcr() {
    return !CommonUtils.isEqual(getOldData().getWpId(), getInputData().getWpId()) ||
        !CommonUtils.isEqual(getOldData().getWpResId(), getInputData().getWpResId()) ||
        !CommonUtils.isEqual(getOldData().getWpIdMcr(), getInputData().getWpIdMcr());
  }

  /**
   * @return
   */
  private boolean isDataChangedContactPerson() {
    return !CommonUtils.isEqual(getOldData().getContactPersonId(), getInputData().getContactPersonId()) ||
        !CommonUtils.isEqual(getOldData().getContactPersonSecondId(), getInputData().getContactPersonSecondId());
  }

  private boolean isDataChangedIccRelevantFlag() {
    return !CommonUtils.isEqual(getOldData().getIccRelevantFlag(), getInputData().getIccRelevantFlag());
  }

  private boolean isComplianceDataChanged() {
    return !CommonUtils.isEqual(getOldData().getCrpObdRelevantFlag(), getInputData().getCrpObdRelevantFlag()) ||
        !CommonUtils.isEqual(getOldData().getCrpObdComment(), getInputData().getCrpObdComment()) ||
        !CommonUtils.isEqual(getOldData().getCrpEmissionRelevantFlag(), getInputData().getCrpEmissionRelevantFlag()) ||
        !CommonUtils.isEqual(getOldData().getCrpEmissionComment(), getInputData().getCrpEmissionComment()) ||
        !CommonUtils.isEqual(getOldData().getCrpSoundRelevantFlag(), getInputData().getCrpSoundRelevantFlag()) ||
        !CommonUtils.isEqual(getOldData().getCrpSoundComment(), getInputData().getCrpSoundComment());
  }

  private boolean isCrpObdRelevantFlagValid() {
    return CommonUtils.isNotEmptyString(getInputData().getCrpObdRelevantFlag()) &&
        CommonUtils.isEqual(CommonUtilConstants.CODE_YES, getInputData().getCrpObdRelevantFlag());
  }

  private boolean isCrpEmissionRelevantFlagValid() {
    return CommonUtils.isNotEmptyString(getInputData().getCrpEmissionRelevantFlag()) &&
        CommonUtils.isEqual(CommonUtilConstants.CODE_YES, getInputData().getCrpEmissionRelevantFlag());
  }

  private boolean isCrpSoundRelevantFlagValid() {
    return CommonUtils.isNotEmptyString(getInputData().getCrpSoundRelevantFlag()) &&
        CommonUtils.isEqual(CommonUtilConstants.CODE_YES, getInputData().getCrpSoundRelevantFlag());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    /**
     * Method not implemented
     */
  }

}
