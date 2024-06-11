package com.bosch.caltool.icdm.bo.apic.cocwp;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivisionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcVersCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWp;


/**
 * Command class for PidcVersCocWp
 *
 * @author UKT1COB
 */
public class PidcVersCocWpCommand extends AbstractCommand<PidcVersCocWp, PidcVersCocWpLoader> {


  /**
   * Constructor
   *
   * @param input       input data
   * @param isUpdate    if true, update, else create
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public PidcVersCocWpCommand(final ServiceData serviceData, final PidcVersCocWp input, final boolean isUpdate)
      throws IcdmException {
    super(serviceData, input, new PidcVersCocWpLoader(serviceData),
        isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TPidcVersCocWp entity = new TPidcVersCocWp();

    setValuesToEntity(entity);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    TPidcVersCocWp entity = new PidcVersCocWpLoader(getServiceData()).getEntityObject(getInputData().getId());

    setValuesToEntity(entity);

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * @param entity
   */
  private void setValuesToEntity(final TPidcVersCocWp entity) {

    PidcVersCocWp newPidcVersCocWp = getInputData();

    entity.setTPidcVersion(new PidcVersionLoader(getServiceData()).getEntityObject(newPidcVersCocWp.getPidcVersId()));
    entity
        .setTWrkpkgdiv(new WorkPackageDivisionLoader(getServiceData()).getEntityObject(newPidcVersCocWp.getWPDivId()));

    entity.setUsedFlag(getInputData().getUsedFlag());
    entity.setIsAtChildLevel(
        getInputData().isAtChildLevel() ? CommonUtilConstants.CODE_YES : CommonUtilConstants.CODE_NO);
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
  protected void doPostCommit() throws IcdmException {
    //update the related entity
    new PidcCocWpUpdationBO(getServiceData()).updatePidcVersCocWpReferenceEntity(getNewData());
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
