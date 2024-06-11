package com.bosch.caltool.icdm.bo.a2l;

import java.util.ArrayList;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;


/**
 * Command class for A2LWPResponsibilityStatus
 *
 * @author UKT1COB
 */
public class A2lWpResponsibilityStatusCommand
    extends AbstractCommand<A2lWpResponsibilityStatus, A2lWpResponsibilityStatusLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public A2lWpResponsibilityStatusCommand(final ServiceData serviceData, final A2lWpResponsibilityStatus input,
      final boolean isUpdate, final boolean isDelete) throws IcdmException {
    super(serviceData, input, new A2lWpResponsibilityStatusLoader(serviceData),
        resolveCommandModeA(isDelete, isUpdate));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TA2lWpResponsibilityStatus entity = new TA2lWpResponsibilityStatus();

    setValuesToEntity(entity);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {

    TA2lWpResponsibilityStatus entity =
        new A2lWpResponsibilityStatusLoader(getServiceData()).getEntityObject(getInputData().getId());

    setValuesToEntity(entity);

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * @param entity
   */
  private void setValuesToEntity(final TA2lWpResponsibilityStatus entity) {

    A2lWpResponsibilityStatus newA2lWpResponsibilityStatus = getInputData();

    entity.setTabvProjVar(
        new PidcVariantLoader(getServiceData()).getEntityObject(newA2lWpResponsibilityStatus.getVariantId()));
    entity.setTA2lWpResp(
        new A2lWpResponsibilityLoader(getServiceData()).getEntityObject(newA2lWpResponsibilityStatus.getWpRespId()));

    Long a2lRespId = newA2lWpResponsibilityStatus.getA2lRespId();
    entity.settA2lResp(newA2lWpResponsibilityStatus.isInheritedFlag() || CommonUtils.isNull(a2lRespId) ? null
        : new A2lResponsibilityLoader(getServiceData()).getEntityObject(a2lRespId));
    entity.setWpRespFinStatus(newA2lWpResponsibilityStatus.getWpRespFinStatus());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    TA2lWpResponsibilityStatus tA2lRespStatus =
        new A2lWpResponsibilityStatusLoader(getServiceData()).getEntityObject(getInputData().getId());

    // Remove from reference entity
    removeFromA2lRespEntity(tA2lRespStatus);
    removeFromA2lWpRespEntity(tA2lRespStatus);
    removeFromTabvProjectIdCard(tA2lRespStatus);

    getEm().remove(tA2lRespStatus);
  }

  /**
   * @param entity
   */
  private void removeFromA2lWpRespEntity(final TA2lWpResponsibilityStatus tA2lRespStatus) {
    if (CommonUtils.isNotNull(tA2lRespStatus.getTA2lWpResp())) {
      tA2lRespStatus.getTA2lWpResp().gettA2lWPRespStatus().remove(tA2lRespStatus);
    }

  }

  /**
   * @param tA2lRespStatus
   */
  private void removeFromA2lRespEntity(final TA2lWpResponsibilityStatus tA2lRespStatus) {
    if (CommonUtils.isNotNull(tA2lRespStatus.gettA2lResp())) {
      tA2lRespStatus.gettA2lResp().gettA2lWPRespStatus().remove(tA2lRespStatus);
    }
  }

  /**
   * @param tA2lResp
   */
  private void removeFromTabvProjectIdCard(final TA2lWpResponsibilityStatus tA2lRespStatus) {
    if (CommonUtils.isNotNull(tA2lRespStatus.getTabvProjVar())) {
      tA2lRespStatus.getTabvProjVar().gettA2lWPRespStatus().remove(tA2lRespStatus);
    }
  }

  /**
   * newA2lWpResponsibilityStatus {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {


    // Update the referenced entity
    TA2lWpResponsibilityStatus tA2lWPRespStatus =
        new A2lWpResponsibilityStatusLoader(getServiceData()).getEntityObject(getInputData().getId());

    addOrUpdStatusInA2lWPRespEntity(tA2lWPRespStatus);

    addOrUpdStatusInProjVarEntity(tA2lWPRespStatus);

    addOrUpdStatusInA2lRespEntity(tA2lWPRespStatus);
  }

  /**
   * @param tA2lWPRespStatus
   */
  private void addOrUpdStatusInA2lWPRespEntity(final TA2lWpResponsibilityStatus tA2lWPRespStatus) {
    TA2lWpResponsibility tA2lWpResp =
        new A2lWpResponsibilityLoader(getServiceData()).getEntityObject(getInputData().getWpRespId());
    if (CommonUtils.isNotEmpty(tA2lWpResp.gettA2lWPRespStatus()) &&
        tA2lWpResp.gettA2lWPRespStatus().contains(tA2lWPRespStatus)) {
      tA2lWpResp.gettA2lWPRespStatus().remove(tA2lWPRespStatus);
    }
    else if (CommonUtils.isNull(tA2lWpResp.gettA2lWPRespStatus())) {
      tA2lWpResp.settA2lWPRespStatus(new ArrayList<>());
    }

    if (getCmdMode() != COMMAND_MODE.DELETE) {
      tA2lWpResp.gettA2lWPRespStatus().add(tA2lWPRespStatus);
    }
  }

  /**
   * @param tA2lWPRespStatus
   */
  private void addOrUpdStatusInProjVarEntity(final TA2lWpResponsibilityStatus tA2lWPRespStatus) {

    Long variantId = getInputData().getVariantId();
    if (CommonUtils.isNotNull(variantId)) {
      TabvProjectVariant tabvProjVar = new PidcVariantLoader(getServiceData()).getEntityObject(variantId);

      if (CommonUtils.isNotEmpty(tabvProjVar.gettA2lWPRespStatus()) &&
          tabvProjVar.gettA2lWPRespStatus().contains(tA2lWPRespStatus)) {
        tabvProjVar.gettA2lWPRespStatus().remove(tA2lWPRespStatus);
      }
      else if (CommonUtils.isNull(tabvProjVar.gettA2lWPRespStatus())) {
        tabvProjVar.settA2lWPRespStatus(new ArrayList<>());
      }
      if (getCmdMode() != COMMAND_MODE.DELETE) {
        tabvProjVar.gettA2lWPRespStatus().add(tA2lWPRespStatus);
      }
    }
  }

  /**
   * @param tA2lWPRespStatus
   */
  private void addOrUpdStatusInA2lRespEntity(final TA2lWpResponsibilityStatus tA2lWPRespStatus) {

    Long respId = getInputData().getA2lRespId();
    if (CommonUtils.isNotNull(respId)) {
      TA2lResponsibility tA2lResp = new A2lResponsibilityLoader(getServiceData()).getEntityObject(respId);
      if (CommonUtils.isNotEmpty(tA2lResp.gettA2lWPRespStatus()) &&
          tA2lResp.gettA2lWPRespStatus().contains(tA2lWPRespStatus)) {
        tA2lResp.gettA2lWPRespStatus().remove(tA2lWPRespStatus);
      }
      else if (CommonUtils.isNull(tA2lResp.gettA2lWPRespStatus())) {
        tA2lResp.settA2lWPRespStatus(new ArrayList<>());
      }

      if (getCmdMode() != COMMAND_MODE.DELETE) {
        tA2lResp.gettA2lWPRespStatus().add(tA2lWPRespStatus);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return CommonUtils.isNotEqual(getOldData().getWpRespFinStatus(), getInputData().getWpRespFinStatus());
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
    // NA

  }


}
