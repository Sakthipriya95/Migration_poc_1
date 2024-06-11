package com.bosch.caltool.icdm.bo.apic.attr;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;


/**
 * Command class for AttrGroup
 *
 * @author dmo5cob
 */
public class AttrGroupCommand extends AbstractCommand<AttrGroup, AttrGroupLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public AttrGroupCommand(final ServiceData serviceData, final AttrGroup input, final boolean isUpdate)
      throws IcdmException {
    super(serviceData, input, new AttrGroupLoader(serviceData), (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TabvAttrGroup entity = new TabvAttrGroup();

    entity.setGroupNameEng(getInputData().getNameEng());
    entity.setGroupNameGer(getInputData().getNameGer());
    entity.setGroupDescEng(getInputData().getDescriptionEng());
    entity.setGroupDescGer(getInputData().getDescriptionGer());
    entity.setTabvAttrSuperGroup(
        new AttrSuperGroupLoader(getServiceData()).getEntityObject(getInputData().getSuperGrpId()));

    entity.setDeletedFlag(getInputData().isDeleted() ? CommonUtilConstants.CODE_YES : CommonUtilConstants.CODE_NO);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    AttrGroupLoader loader = new AttrGroupLoader(getServiceData());
    TabvAttrGroup entity = loader.getEntityObject(getInputData().getId());

    entity.setGroupNameEng(getInputData().getNameEng());
    entity.setGroupNameGer(getInputData().getNameGer());
    entity.setGroupDescEng(getInputData().getDescriptionEng());
    entity.setGroupDescGer(getInputData().getDescriptionGer());
    entity.setTabvAttrSuperGroup(
        new AttrSuperGroupLoader(getServiceData()).getEntityObject(getInputData().getSuperGrpId()));

    entity.setDeletedFlag(getInputData().isDeleted() ? CommonUtilConstants.CODE_YES : CommonUtilConstants.CODE_NO);


    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // not applicable
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
