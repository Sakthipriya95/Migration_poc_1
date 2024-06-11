package com.bosch.caltool.icdm.bo.apic.cocwp;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcSubVariantLoader;
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivisionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcSubVarCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;


/**
 * Command class for PidcSubVarCocWp
 *
 * @author UKT1COB
 */
public class PidcSubVarCocWpCommand extends AbstractCommand<PidcSubVarCocWp, PidcSubVarCocWpLoader> {


  /**
   * Constructor
   *
   * @param input       input data
   * @param isUpdate    if true, update, else create
   * @param isDelete    if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public PidcSubVarCocWpCommand(final ServiceData serviceData, final PidcSubVarCocWp input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new PidcSubVarCocWpLoader(serviceData),
        (isDelete ? COMMAND_MODE.DELETE : isUpdate(isUpdate)));
  }

  /**
   * @param isUpdate
   * @return
   */
  private static COMMAND_MODE isUpdate(final boolean isUpdate) {
    return isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TPidcSubVarCocWp entity = new TPidcSubVarCocWp();

    setValuesToEntity(entity);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    TPidcSubVarCocWp entity = new PidcSubVarCocWpLoader(getServiceData()).getEntityObject(getInputData().getId());

    setValuesToEntity(entity);

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * @param entity
   */
  private void setValuesToEntity(final TPidcSubVarCocWp entity) {

    PidcSubVarCocWp newPidcSubVarCocWp = getInputData();

    entity.setTabvprojsubvar(
        new PidcSubVariantLoader(getServiceData()).getEntityObject(newPidcSubVarCocWp.getPidcSubVarId()));
    entity.setTwrkpkgdiv(
        new WorkPackageDivisionLoader(getServiceData()).getEntityObject(newPidcSubVarCocWp.getWPDivId()));
    entity.setUsedFlag(getInputData().getUsedFlag());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    TPidcSubVarCocWp tPidcSubVarCocWp =
        new PidcSubVarCocWpLoader(getServiceData()).getEntityObject(getInputData().getId());
    getEm().remove(tPidcSubVarCocWp);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    PidcSubVarCocWp pidcSubVarCocWp = COMMAND_MODE.DELETE.equals(getCmdMode()) ? getInputData() : getNewData();
    new PidcCocWpUpdationBO(getServiceData()).updatePidcSubVarsCocWpReferenceEntity(pidcSubVarCocWp, getCmdMode());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return CommonUtils.isNotEqual(getOldData().getUsedFlag(), getInputData().getUsedFlag());
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
