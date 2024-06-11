package com.bosch.caltool.icdm.bo.a2l;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lGroup;
import com.bosch.caltool.icdm.model.a2l.IcdmA2lGroup;


// TODO: Auto-generated Javadoc
/**
 * Command class for Attribute.
 *
 * @author dmo5cob
 */
public class IcdmA2lGroupCommand extends AbstractCommand<IcdmA2lGroup, IcdmA2lGroupLoader> {


  /**
   * Instantiates a new icdm A 2 l group command.
   *
   * @param serviceData ServiceData
   * @param inputData IcdmA2lGroup
   * @throws IcdmException Exception
   */
  public IcdmA2lGroupCommand(final ServiceData serviceData, final IcdmA2lGroup inputData) throws IcdmException {
    super(serviceData, inputData, new IcdmA2lGroupLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TA2lGroup dbA2lGroup = new TA2lGroup();
    dbA2lGroup.setGrpName(getInputData().getGrpName());
    dbA2lGroup.setGrpLongName(getInputData().getGrpLongName());
    dbA2lGroup.setA2lId(getInputData().getA2lId());
    dbA2lGroup
        .setTabvAttrValue(new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getWpRootId()));

    setUserDetails(COMMAND_MODE.CREATE, dbA2lGroup);
    persistEntity(dbA2lGroup);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // TODO Auto-generated method stub

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

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // TODO Auto-generated method stub

  }

}
