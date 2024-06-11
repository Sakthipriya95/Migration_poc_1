package com.bosch.caltool.icdm.bo.apic.attr;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TPredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;


/**
 * Command class for PredefinedAttrValue
 *
 * @author dmo5cob
 */
public class PredefinedAttrValueCommand extends AbstractCommand<PredefinedAttrValue, PredefinedAttrValueLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public PredefinedAttrValueCommand(final ServiceData serviceData, final PredefinedAttrValue input,
      final boolean isUpdate, final boolean isDelete) throws IcdmException {
    super(serviceData, input, new PredefinedAttrValueLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TPredefinedAttrValue entity = new TPredefinedAttrValue();

    entity.setGrpAttrVal(new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getGrpAttrValId()));

    entity.setPreDefAttrVal(
        new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getPredefinedValueId()));
    entity
        .setPreDefinedAttr(new AttributeLoader(getServiceData()).getEntityObject(getInputData().getPredefinedAttrId()));

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    PredefinedAttrValueLoader loader = new PredefinedAttrValueLoader(getServiceData());
    TPredefinedAttrValue entity = loader.getEntityObject(getInputData().getId());

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
