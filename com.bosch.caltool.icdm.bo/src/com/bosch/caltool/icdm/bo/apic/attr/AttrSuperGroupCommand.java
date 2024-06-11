package com.bosch.caltool.icdm.bo.apic.attr;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.TopLevelEntityCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;


/**
 * Command class for AttrSuperGroupCommand
 *
 * @author dmo5cob
 */
public class AttrSuperGroupCommand extends AbstractCommand<AttrSuperGroup, AttrSuperGroupLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public AttrSuperGroupCommand(final ServiceData serviceData, final AttrSuperGroup input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new AttrSuperGroupLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TabvAttrSuperGroup entity = new TabvAttrSuperGroup();

    entity.setSuperGroupNameEng(getInputData().getNameEng());
    entity.setSuperGroupNameGer(getInputData().getNameGer());
    entity.setSuperGroupDescEng(getInputData().getDescriptionEng());
    entity.setSuperGroupDescGer(getInputData().getDescriptionGer());
    entity.setDeletedFlag(getInputData().isDeleted() ? CommonUtilConstants.CODE_YES : CommonUtilConstants.CODE_NO);
    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
    TopLevelEntityCommand tleCmd = new TopLevelEntityCommand(getServiceData(), ApicConstants.TOP_LVL_ENT_ID_SUPER_GRP);
    executeChildCommand(tleCmd);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    AttrSuperGroupLoader loader = new AttrSuperGroupLoader(getServiceData());
    TabvAttrSuperGroup entity = loader.getEntityObject(getInputData().getId());

    entity.setSuperGroupNameEng(getInputData().getNameEng());
    entity.setSuperGroupNameGer(getInputData().getNameGer());
    entity.setSuperGroupDescEng(getInputData().getDescriptionEng());
    entity.setSuperGroupDescGer(getInputData().getDescriptionGer());


    setUserDetails(COMMAND_MODE.UPDATE, entity);
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
  protected void delete() throws IcdmException {
    // TODO Auto-generated method stub

  }

}
