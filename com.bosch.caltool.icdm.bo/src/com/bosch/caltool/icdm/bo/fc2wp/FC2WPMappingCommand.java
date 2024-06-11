/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.fc2wp;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.BaseComponentLoader;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateUtil;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpDefVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpMapping;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;
import com.bosch.caltool.icdm.database.entity.cdr.TFunction;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapPtType;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;

/**
 * @author bne4cob
 */
public class FC2WPMappingCommand extends AbstractCommand<FC2WPMapping, FC2WPMappingLoader> {


  /**
   * Constructor to create a mapping
   *
   * @param serviceData service Data
   * @param inputData FC2WPMapping to create
   * @throws IcdmException any exception
   */
  public FC2WPMappingCommand(final ServiceData serviceData, final FC2WPMapping inputData) throws IcdmException {
    super(serviceData, inputData, new FC2WPMappingLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * Constructor for updating/delete a mapping
   *
   * @param serviceData service Data
   * @param inputData FC2WPMapping to update
   * @param update if true updates the mapping
   * @throws IcdmException any exception
   */
  public FC2WPMappingCommand(final ServiceData serviceData, final FC2WPMapping inputData, final boolean update)
      throws IcdmException {
    super(serviceData, inputData, new FC2WPMappingLoader(serviceData),
        update ? COMMAND_MODE.UPDATE : COMMAND_MODE.DELETE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TFc2wpMapping dbFcwpMapping = new TFc2wpMapping();


    if (!CommonUtils.isNull(getInputData().getWpDivId())) {
      TWorkpackageDivision dbWpDiv = getEm().find(TWorkpackageDivision.class, getInputData().getWpDivId());
      dbFcwpMapping.setTWorkpackageDivision(dbWpDiv);
    }

    dbFcwpMapping
        .setAgreedDate(DateUtil.calendarToTimestamp(DateUtil.dateToCalendar(getInputData().getAgreeWithCocDate())));
    UserLoader userLdr = new UserLoader(getServiceData());
    dbFcwpMapping.setTabvApicRespForAgrUser(userLdr.getEntityObject(getInputData().getAgreeWithCocRespUserId()));

    dbFcwpMapping.setComments(getInputData().getComments());
    dbFcwpMapping.setFcWpInfo(getInputData().getFc2wpInfo());

    dbFcwpMapping.setTabvApicContactUser(userLdr.getEntityObject(getInputData().getContactPersonId()));
    dbFcwpMapping.setTabvApicSecondContactUser(userLdr.getEntityObject(getInputData().getContactPersonSecondId()));

    dbFcwpMapping.setAgreedWithCocFlag(getInputData().isAgreeWithCoc() ? "Y" : null);
    dbFcwpMapping.setDeleteFlag(getInputData().isDeleted() ? "Y" : null);
    dbFcwpMapping.setInIcdmA2lFlag(getInputData().isUsedInIcdm() ? "Y" : null);
    dbFcwpMapping.setUseWpDefaultsFlag(!getInputData().isUseWpDef() ? "N" : null);
    dbFcwpMapping.setFcWithParams(booleanToYorN(getInputData().isFcWithParams()));

    dbFcwpMapping
        .setTBaseComponent((new BaseComponentLoader(getServiceData())).getEntityObject(getInputData().getBcID()));

    TFc2wpDefVersion dbVers = new FC2WPVersionLoader(getServiceData()).getEntityObject(getInputData().getFcwpVerId());
    dbVers.addTFc2wpMapping(dbFcwpMapping);

    dbFcwpMapping.setTFunction(getEm().find(TFunction.class, getInputData().getFunctionId()));

    setUserDetails(COMMAND_MODE.CREATE, dbFcwpMapping);

    persistEntity(dbFcwpMapping);


    FC2WPMapPtType mapPtType;
    for (Long ptType : getInputData().getPtTypeSet()) {
      mapPtType = new FC2WPMapPtType();
      mapPtType.setPtTypeId(ptType);
      mapPtType.setFcwpMapId(dbFcwpMapping.getFcwpMapId());
      FC2WPMapPtTypeCommand ptTypeMapCmd = new FC2WPMapPtTypeCommand(getServiceData(), mapPtType);
      executeChildCommand(ptTypeMapCmd);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {

    TFc2wpMapping dbFcwpMapping = getEm().find(TFc2wpMapping.class, getInputData().getId());

    if (!CommonUtils.isEqual(getOldData().getWpDivId(), getInputData().getWpDivId())) {
      if (null != getInputData().getWpDivId()) {
        TWorkpackageDivision dbWpDiv = getEm().find(TWorkpackageDivision.class, getInputData().getWpDivId());
        dbFcwpMapping.setTWorkpackageDivision(dbWpDiv);
      }
      else {
        dbFcwpMapping.setTWorkpackageDivision(null);
      }
    }
    if (!CommonUtils.isEqual(getOldData().getAgreeWithCocDate(), getInputData().getAgreeWithCocDate())) {
      if (null != getInputData().getAgreeWithCocDate()) {
        dbFcwpMapping
            .setAgreedDate(DateUtil.calendarToTimestamp(DateUtil.dateToCalendar(getInputData().getAgreeWithCocDate())));
      }
      else {
        dbFcwpMapping.setAgreedDate(null);
      }
    }
    setTabvApicUserDetails(dbFcwpMapping);
    setYorNvalues(dbFcwpMapping);
    setUserDetails(COMMAND_MODE.UPDATE, dbFcwpMapping);

  }

  /**
   * @param dbFcwpMapping
   */
  private void setTabvApicUserDetails(final TFc2wpMapping dbFcwpMapping) {
    UserLoader userLdr = new UserLoader(getServiceData());
    if (!CommonUtils.isEqual(getOldData().getAgreeWithCocRespUserId(), getInputData().getAgreeWithCocRespUserId())) {
      if (null != getInputData().getAgreeWithCocRespUserId()) {
        dbFcwpMapping.setTabvApicRespForAgrUser(userLdr.getEntityObject(getInputData().getAgreeWithCocRespUserId()));
      }
      else {
        dbFcwpMapping.setTabvApicRespForAgrUser(null);
      }
    }
    if (!CommonUtils.isEqual(getOldData().getComments(), getInputData().getComments())) {
      dbFcwpMapping.setComments(getInputData().getComments());
    }
    if (!CommonUtils.isEqual(getOldData().getFc2wpInfo(), getInputData().getFc2wpInfo())) {
      dbFcwpMapping.setFcWpInfo(getInputData().getFc2wpInfo());
    }
    if (!CommonUtils.isEqual(getOldData().getContactPersonId(), getInputData().getContactPersonId())) {
      if (null != getInputData().getContactPersonId()) {
        dbFcwpMapping.setTabvApicContactUser(userLdr.getEntityObject(getInputData().getContactPersonId()));
      }
      else {
        dbFcwpMapping.setTabvApicContactUser(null);
      }
    }
    if (!CommonUtils.isEqual(getOldData().getContactPersonSecondId(), getInputData().getContactPersonSecondId())) {
      if (null != getInputData().getContactPersonSecondId()) {
        dbFcwpMapping.setTabvApicSecondContactUser(userLdr.getEntityObject(getInputData().getContactPersonSecondId()));
      }
      else {
        dbFcwpMapping.setTabvApicSecondContactUser(null);
      }
    }
  }

  /**
   * @param dbFcwpMapping
   */
  private void setYorNvalues(final TFc2wpMapping dbFcwpMapping) {
    if (getOldData().isAgreeWithCoc() != getInputData().isAgreeWithCoc()) {
      dbFcwpMapping.setAgreedWithCocFlag(getInputData().isAgreeWithCoc() ? "Y" : null);
    }
    if (getOldData().isDeleted() != getInputData().isDeleted()) {
      dbFcwpMapping.setDeleteFlag(getInputData().isDeleted() ? "Y" : null);
    }
    if (getOldData().isUsedInIcdm() != getInputData().isUsedInIcdm()) {
      dbFcwpMapping.setInIcdmA2lFlag(getInputData().isUsedInIcdm() ? "Y" : null);
    }
    if (getOldData().isUseWpDef() != getInputData().isUseWpDef()) {
      dbFcwpMapping.setUseWpDefaultsFlag(!getInputData().isUseWpDef() ? "N" : null);
    }
    if (getOldData().isFcWithParams() != getInputData().isFcWithParams()) {
      dbFcwpMapping.setFcWithParams(booleanToYorN(getInputData().isFcWithParams()));
    }
  }

  /**
   * @return true, if the user has privileges to execute this command
   */
  @Override
  protected boolean hasPrivileges() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws CommandException {
    // Not required
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // No actions
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return dataChangedCoc() || dataChangedCommentsContactPerson() || dataChangedWpDefAndIcdmFlags();
  }

  /**
   * @return
   */
  private boolean dataChangedWpDefAndIcdmFlags() {
    return (getOldData().isDeleted() != getInputData().isDeleted()) ||
        (getOldData().isUsedInIcdm() != getInputData().isUsedInIcdm()) || dataChangedWpDefAndWpDiv();
  }

  /**
   * @return
   */
  private boolean dataChangedWpDefAndWpDiv() {
    return (getOldData().isUseWpDef() != getInputData().isUseWpDef()) ||
        !CommonUtils.isEqual(getOldData().getWpDivId(), getInputData().getWpDivId());
  }

  /**
   * @return
   */
  private boolean dataChangedCommentsContactPerson() {
    return !CommonUtils.isEqual(getOldData().getComments(), getInputData().getComments()) ||
        !CommonUtils.isEqual(getOldData().getFc2wpInfo(), getInputData().getFc2wpInfo()) ||
        !CommonUtils.isEqual(getOldData().getContactPersonId(), getInputData().getContactPersonId()) ||
        !CommonUtils.isEqual(getOldData().getContactPersonSecondId(), getInputData().getContactPersonSecondId());
  }

  /**
   * @return
   */
  private boolean dataChangedCoc() {
    return !CommonUtils.isEqual(getOldData().getAgreeWithCocDate(), getInputData().getAgreeWithCocDate()) ||
        !CommonUtils.isEqual(getOldData().getAgreeWithCocRespUserId(), getInputData().getAgreeWithCocRespUserId()) ||
        (getOldData().isAgreeWithCoc() != getInputData().isAgreeWithCoc());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // Not Applicable

  }

}
