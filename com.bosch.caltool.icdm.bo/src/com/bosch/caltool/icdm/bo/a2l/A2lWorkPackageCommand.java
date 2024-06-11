package com.bosch.caltool.icdm.bo.a2l;

import java.util.BitSet;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.bo.a2l.A2lObjectIdentifierValidator;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWorkPackage;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;


/**
 * Command class for A2l Workpackage
 *
 * @author pdh2cob
 */
public class A2lWorkPackageCommand extends AbstractCommand<A2lWorkPackage, A2lWorkPackageLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public A2lWorkPackageCommand(final ServiceData serviceData, final A2lWorkPackage input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new A2lWorkPackageLoader(serviceData), resolveCommandModeA(isDelete, isUpdate));
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    // crete the entity
    TA2lWorkPackage entity = new TA2lWorkPackage();

    setValuesToEntity(entity);


    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    A2lWorkPackageLoader loader = new A2lWorkPackageLoader(getServiceData());
    // fetch the DB entity which has to be updated
    TA2lWorkPackage entity = loader.getEntityObject(getInputData().getId());
    setValuesToEntity(entity);
    updateReferencingEntities(entity);
    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  private void updateReferencingEntities(final TA2lWorkPackage entity) {
    // update the relevant PIDCVersion entity
    TPidcVersion pidcVersionEntity =
        new PidcVersionLoader(getServiceData()).getEntityObject(getInputData().getPidcVersId());
    pidcVersionEntity.getTA2lWorkPackageList().remove(entity);
    pidcVersionEntity.getTA2lWorkPackageList().add(entity);
  }

  private void setValuesToEntity(final TA2lWorkPackage entity) throws InvalidInputException {
    // 489386 - Handling special characters in WP names
    A2lObjectIdentifierValidator validator = new A2lObjectIdentifierValidator();
    BitSet validationResult = validator.isValidName(getInputData().getName());
    if (validationResult.cardinality() != 0) {
      throw new InvalidInputException("The given work package name '" + getInputData().getName() +
          "' does not comply with A2L specification\n" + validator.createErrorMsg(validationResult));
    }
    entity.setWpName(getInputData().getName());
    entity.setWpDesc(getInputData().getDescription());
    entity.setWpNameCust(getInputData().getNameCustomer());
    TPidcVersion pidcVersionEntity =
        new PidcVersionLoader(getServiceData()).getEntityObject(getInputData().getPidcVersId());
    entity.setPidcVersion(pidcVersionEntity);
    entity.setParentTA2lWorkPackage(
        new A2lWorkPackageLoader(getServiceData()).getEntityObject(getInputData().getParentA2lWpId()));
    // add new a2l wp pal entity to new pidc version
    entity.getPidcVersion().addTA2lWorkPackage(entity);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    A2lWorkPackageLoader loader = new A2lWorkPackageLoader(getServiceData());
    TA2lWorkPackage entity = loader.getEntityObject(getInputData().getId());
    TPidcVersion pidcVersionEntity =
        new PidcVersionLoader(getServiceData()).getEntityObject(getInputData().getPidcVersId());
    pidcVersionEntity.getTA2lWorkPackageList().remove(entity);


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

    return dataChangedIdNameDesc() ||
        isObjectChanged(getInputData().getNameCustomer(), getOldData().getNameCustomer()) ||
        isObjectChanged(getInputData().getPidcVersId(), getOldData().getPidcVersId());
  }


  /**
   * @return
   */
  private boolean dataChangedIdNameDesc() {
    return isObjectChanged(getInputData().getId(), getOldData().getId()) ||
        isObjectChanged(getInputData().getName(), getOldData().getName()) ||
        isObjectChanged(getInputData().getDescription(), getOldData().getDescription());
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
