package com.bosch.caltool.icdm.bo.a2l;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;


/**
 * Command class for A2lWpParamMapping
 *
 * @author pdh2cob
 */
public class A2lWpParamMappingCommand extends AbstractCommand<A2lWpParamMapping, A2lWpParamMappingLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public A2lWpParamMappingCommand(final ServiceData serviceData, final A2lWpParamMapping input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new A2lWpParamMappingLoader(serviceData), resolveCommandModeA(isDelete, isUpdate));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TA2lWpParamMapping entity = new TA2lWpParamMapping();
    setValuesToEntityForCreate(entity);
    setUserDetails(COMMAND_MODE.CREATE, entity);
    // Updating the pidc working set modified flag , since there is new change
    updatePidcA2l(entity);
    persistEntity(entity);
  }

  /**
   * @param entity
   */
  private void setValuesToEntityForCreate(final TA2lWpParamMapping entity) {
    new ParameterLoader(getServiceData()).getEntityObject(getInputData().getParamId()).addTA2lWpParamMapping(entity);
    new A2lWpResponsibilityLoader(getServiceData()).getEntityObject(getInputData().getWpRespId())
        .addTA2lWpParamMapping(entity);

    if (!getInputData().isWpNameCustInherited()) {
      entity.setWpNameCust(getInputData().getWpNameCust());
    }

    // if the wp resp is inherited then responsibility willl be taken from A2lWpResponsibility table
    if (getInputData().isWpRespInherited()) {
      entity.setTA2lResponsibility(null);
    }
    // for Default wp resp - inherited from Wp resp. Also check if the pidc wp resp id is set from input data
    else if (!getInputData().isWpRespInherited() || (getInputData().getParA2lRespId() != null)) {
      TA2lResponsibility a2lRespEntity =
          new A2lResponsibilityLoader(getServiceData()).getEntityObject(getInputData().getParA2lRespId());
      entity.setTA2lResponsibility(a2lRespEntity);

      if (a2lRespEntity != null) {
        if (a2lRespEntity.getWpParamMappingList() == null) {
          List<TA2lWpParamMapping> paramMappingList = new ArrayList<>();
          paramMappingList.add(entity);
          a2lRespEntity.setWpParamMappingList(paramMappingList);
        }
        else {
          a2lRespEntity.getWpParamMappingList().add(entity);
        }
      }
    }


    entity.setWpNameCustInheritedFlag(booleanToYorN(getInputData().isWpNameCustInherited()));
    entity.setWpRespInheritedFlag(booleanToYorN(getInputData().isWpRespInherited()));
  }

  /**
   * @param entity
   */
  private void setValuesToEntityForUpd(final TA2lWpParamMapping entity) {
    if (isObjectChanged(getInputData().getWpRespId(), getOldData().getWpRespId())) {
      A2lWpResponsibilityLoader wpRespPalLoader = new A2lWpResponsibilityLoader(getServiceData());
      // remove existing
      entity.getTA2lWpResponsibility().removeTA2lWpParamMapping(entity);
      entity.setTA2lWpResponsibility(wpRespPalLoader.getEntityObject(getInputData().getWpRespId()));
      entity.getTA2lWpResponsibility().addTA2lWpParamMapping(entity);

    }
    if (isObjectChanged(getInputData().isWpNameCustInherited(), getOldData().isWpNameCustInherited())) {
      entity.setWpNameCustInheritedFlag(booleanToYorN(getInputData().isWpNameCustInherited()));
    }
    if (isObjectChanged(getInputData().isWpRespInherited(), getOldData().isWpRespInherited())) {
      entity.setWpRespInheritedFlag(booleanToYorN(getInputData().isWpRespInherited()));
    }


    // if the wp resp is inherited then sync with the parent table. Reuse the same logic for inherited No and Yes
    if (getInputData().isWpRespInherited()) {
      entity.setTA2lResponsibility(null);
    }
    else {
      setValueToEntityForUpdtRespUser(entity);
    }
    if (getInputData().isWpNameCustInherited()) {
      entity.setWpNameCust(null);
    }
    else if (isObjectChanged(getInputData().getWpNameCust(), getOldData().getWpNameCust())) {
      entity.setWpNameCust(getInputData().getWpNameCust());
    }
  }

  /**
   * Update the pidc working set modified flag , if there is new change
   *
   * @param entity
   */
  private void updatePidcA2l(final TA2lWpParamMapping entity) throws IcdmException {
    PidcA2lLoader pidcA2lLoader = new PidcA2lLoader(getServiceData());
    PidcA2l pidcA2l = pidcA2lLoader
        .getDataObjectByID(entity.getTA2lWpResponsibility().gettA2lWpDefnVersion().getTPidcA2l().getPidcA2lId());
    if (!pidcA2l.isWorkingSetModified()) {
      pidcA2l.setWorkingSetModified(true);
      PidcA2lCommand pidcA2lCmd = new PidcA2lCommand(getServiceData(), pidcA2l, true, true, false);
      executeChildCommand(pidcA2lCmd);
    }
  }

  /**
   * @param entity
   */
  private void setValueToEntityForUpdtRespUser(final TA2lWpParamMapping entity) {
    if ((getInputData().getParA2lRespId() != null) &&
        isObjectChanged(getInputData().getParA2lRespId(), getOldData().getParA2lRespId())) {
      TA2lResponsibility a2lRespEntity =
          new A2lResponsibilityLoader(getServiceData()).getEntityObject(getInputData().getParA2lRespId());
      entity.setTA2lResponsibility(a2lRespEntity);
      if (a2lRespEntity.getWpParamMappingList() == null) {
        a2lRespEntity.setWpParamMappingList(new ArrayList<TA2lWpParamMapping>());
      }
      a2lRespEntity.getWpParamMappingList().remove(entity);
      a2lRespEntity.getWpParamMappingList().add(entity);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    A2lWpParamMappingLoader loader = new A2lWpParamMappingLoader(getServiceData());
    TA2lWpParamMapping entity = loader.getEntityObject(getInputData().getId());
    setValuesToEntityForUpd(entity);
    updatePidcA2l(entity);
    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    A2lWpParamMappingLoader loader = new A2lWpParamMappingLoader(getServiceData());
    TA2lWpParamMapping entity = loader.getEntityObject(getInputData().getId());
    entity.getTParameter().removeTA2lWpParamMapping(entity);
    entity.getTA2lWpResponsibility().removeTA2lWpParamMapping(entity);

    if (entity.getTA2lResponsibility() != null) {
      TA2lResponsibility a2lRespEntity =
          new A2lResponsibilityLoader(getServiceData()).getEntityObject(getInputData().getParA2lRespId());
      a2lRespEntity.getWpParamMappingList().remove(entity);
    }

    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return validateRespIds() || validateWpNameCust() ||
        isObjectChanged(getInputData().isWpRespInherited(), getOldData().isWpRespInherited());
  }

  /**
   * @return
   */
  private boolean validateWpNameCust() {
    return isObjectChanged(getInputData().getWpNameCust(), getOldData().getWpNameCust()) ||
        isObjectChanged(getInputData().isWpNameCustInherited(), getOldData().isWpNameCustInherited());
  }

  /**
   * @return
   */
  private boolean validateRespIds() {
    return isObjectChanged(getInputData().getWpRespId(), getOldData().getWpRespId()) ||
        isObjectChanged(getInputData().getParA2lRespId(), getOldData().getParA2lRespId());
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
    // TODO Auto-generated method stub

  }


}
