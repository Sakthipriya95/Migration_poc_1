package com.bosch.caltool.icdm.bo.uc;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.TopLevelEntityCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseGroup;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;


/**
 * Command class for Usecase Group
 *
 * @author MKL2COB
 */
public class UseCaseGroupCommand extends AbstractCommand<UseCaseGroup, UseCaseGroupLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public UseCaseGroupCommand(final ServiceData serviceData, final UseCaseGroup input, final boolean isUpdate)
      throws IcdmException {
    super(serviceData, input, new UseCaseGroupLoader(serviceData), getCommandMode(isUpdate));
  }

  /**
   * @param isUpdate
   * @return COMMAND_MODE
   */
  private static com.bosch.caltool.dmframework.bo.AbstractCommand.COMMAND_MODE getCommandMode(final boolean isUpdate) {
    return isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TabvUseCaseGroup entity = new TabvUseCaseGroup();

    UseCaseGroupLoader loader = new UseCaseGroupLoader(getServiceData());
    TabvUseCaseGroup parentGroup = loader.getEntityObject(getInputData().getParentGroupId());
    if (null != parentGroup) {
      // when there is a parent
      if (null == parentGroup.getTabvUseCaseGroups()) {
        // when the child group list is null
        List<TabvUseCaseGroup> childGroupList = new ArrayList<TabvUseCaseGroup>();
        parentGroup.setTabvUseCaseGroups(childGroupList);
      }
      parentGroup.getTabvUseCaseGroups().add(entity);
    }
    entity.setTabvUseCaseGroup(parentGroup);
    entity.setNameEng(getInputData().getNameEng());
    entity.setNameGer(getInputData().getNameGer());
    entity.setDescEng(getInputData().getDescEng());
    entity.setDescGer(getInputData().getDescGer());
    entity.setDeletedFlag(getInputData().isDeleted() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
    TopLevelEntityCommand tleCmd = new TopLevelEntityCommand(getServiceData(), ApicConstants.TOP_LVL_ENT_ID_UCG);
    executeChildCommand(tleCmd);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    UseCaseGroupLoader loader = new UseCaseGroupLoader(getServiceData());
    TabvUseCaseGroup entity = loader.getEntityObject(getInputData().getId());

    entity.setNameEng(getInputData().getNameEng());
    entity.setNameGer(getInputData().getNameGer());
    entity.setDescEng(getInputData().getDescEng());
    entity.setDescGer(getInputData().getDescGer());
    entity.setDeletedFlag(getInputData().isDeleted() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);

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
