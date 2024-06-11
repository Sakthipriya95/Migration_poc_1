package com.bosch.caltool.icdm.bo.apic.cocwp;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivisionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcVariantCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;


/**
 * Command class for PidcVariantCocWp
 *
 * @author UKT1COB
 */
public class PidcVariantCocWpCommand extends AbstractCommand<PidcVariantCocWp, PidcVariantCocWpLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public PidcVariantCocWpCommand(final ServiceData serviceData, final PidcVariantCocWp input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new PidcVariantCocWpLoader(serviceData),
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

    TPidcVariantCocWp entity = new TPidcVariantCocWp();

    setValuesToEntity(entity);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {

    TPidcVariantCocWp entity = new PidcVariantCocWpLoader(getServiceData()).getEntityObject(getInputData().getId());

    setValuesToEntity(entity);

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * @param tpidcVarCocWp
   */
  private void setValuesToEntity(final TPidcVariantCocWp tpidcVarCocWp) {

    PidcVariantCocWp newPidcVarCocWp = getInputData();

    tpidcVarCocWp
        .setTabvprojvar(new PidcVariantLoader(getServiceData()).getEntityObject(newPidcVarCocWp.getPidcVariantId()));
    tpidcVarCocWp
        .setTwrkpkgdiv(new WorkPackageDivisionLoader(getServiceData()).getEntityObject(newPidcVarCocWp.getWPDivId()));
    tpidcVarCocWp.setUsedFlag(getInputData().getUsedFlag());
    tpidcVarCocWp.setIsAtChildLevel(
        getInputData().isAtChildLevel() ? CommonUtilConstants.CODE_YES : CommonUtilConstants.CODE_NO);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {

    TPidcVariantCocWp tpidcVarCocWp = new PidcVariantCocWpLoader(getServiceData()).getEntityObject(getInputData().getId());
    getEm().remove(tpidcVarCocWp);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    PidcVariantCocWp inputData = COMMAND_MODE.DELETE.equals(getCmdMode()) ? getInputData() : getNewData();
    new PidcCocWpUpdationBO(getServiceData()).updatePidcVarsCocWpReferenceEntity(inputData, getCmdMode());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return CommonUtils.isNotEqual(getOldData().getUsedFlag(), getInputData().getUsedFlag()) ||
        CommonUtils.isNotEqual(getOldData().isAtChildLevel(), getInputData().isAtChildLevel());
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
