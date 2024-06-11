package com.bosch.caltool.icdm.bo.apic.attr;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TPredefinedValidity;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;


/**
 * Command class for PredefinedValidity
 *
 * @author dmo5cob
 */
public class PredefinedValidityCommand extends AbstractCommand<PredefinedValidity, PredefinedValidityLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public PredefinedValidityCommand(final ServiceData serviceData, final PredefinedValidity input,
      final boolean isUpdate, final boolean isDelete) throws IcdmException {
    super(serviceData, input, new PredefinedValidityLoader(serviceData), resolveCommandModeA(isDelete, isUpdate));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TPredefinedValidity entity = new TPredefinedValidity();


    entity.setGrpAttrVal(new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getGrpAttrValId()));

    entity.setValidityValue(
        new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getValidityValueId()));
    entity.setValidityAttribute(
        new AttributeLoader(getServiceData()).getEntityObject(getInputData().getValidityAttrId()));

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    PredefinedValidityLoader loader = new PredefinedValidityLoader(getServiceData());
    TPredefinedValidity entity = loader.getEntityObject(getInputData().getId());

    entity.setGrpAttrVal(new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getGrpAttrValId()));

    entity.setValidityValue(
        new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getValidityValueId()));
    entity.setValidityAttribute(
        new AttributeLoader(getServiceData()).getEntityObject(getInputData().getValidityAttrId()));

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    PredefinedValidityLoader loader = new PredefinedValidityLoader(getServiceData());
    TPredefinedValidity entity = loader.getEntityObject(getInputData().getId());

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
    return true;
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
