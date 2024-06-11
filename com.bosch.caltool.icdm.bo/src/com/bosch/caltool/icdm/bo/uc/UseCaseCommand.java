package com.bosch.caltool.icdm.bo.uc;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.NodeAccessCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCase;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseGroup;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UsecaseCreationData;
import com.bosch.caltool.icdm.model.user.NodeAccess;


/**
 * Command class for Usecase
 *
 * @author MKL2COB
 */
public class UseCaseCommand extends AbstractCommand<UseCase, UseCaseLoader> {


  /**
   * true if the last confirmation date is to be set
   */
  private boolean isUpToDate;
  /**
   * true if only up to date status has to be changed in update
   */
  private boolean changeUpToDateStatus;
  /**
   * UsecaseCreationData
   */
  private UsecaseCreationData ucData;

  private NodeAccess nodeAccess;

  /**
   * Constructor for updating usecase
   *
   * @param input input data
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public UseCaseCommand(final ServiceData serviceData, final UseCase input) throws IcdmException {
    super(serviceData, input, new UseCaseLoader(serviceData), COMMAND_MODE.UPDATE);
  }

  /**
   * Constructor for changing Last confirmation date(Up to Date Status)
   *
   * @param serviceData ServiceData
   * @param input UseCase
   * @param isUpToDate boolean
   * @throws IcdmException error when initializing
   */
  public UseCaseCommand(final ServiceData serviceData, final UseCase input, final boolean isUpToDate)
      throws IcdmException {
    super(serviceData, input, new UseCaseLoader(serviceData), COMMAND_MODE.UPDATE);
    this.changeUpToDateStatus = true;
    this.isUpToDate = isUpToDate;
  }

  /**
   * Constructor for creating usecase
   *
   * @param serviceData input data
   * @param ucData input
   * @param isUpdate boolean
   * @param isDelete boolean
   * @throws IcdmException error while creation
   */
  public UseCaseCommand(final ServiceData serviceData, final UsecaseCreationData ucData, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, ucData.getUsecase(), new UseCaseLoader(serviceData), COMMAND_MODE.CREATE);
    this.ucData = ucData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TabvUseCase entity = new TabvUseCase();

    UseCaseGroupLoader loader = new UseCaseGroupLoader(getServiceData());
    TabvUseCaseGroup parentGroup = loader.getEntityObject(getInputData().getGroupId());
    entity.setTabvUseCaseGroup(parentGroup);
    if (null == parentGroup.getTabvUseCases()) {
      // when the child usecase list is null
      List<TabvUseCase> childUCList = new ArrayList<TabvUseCase>();
      parentGroup.setTabvUseCases(childUCList);
    }
    parentGroup.getTabvUseCases().add(entity);
    entity.setNameEng(getInputData().getNameEng());
    entity.setNameGer(getInputData().getNameGer());
    entity.setDescEng(getInputData().getDescEng());
    entity.setDescGer(getInputData().getDescGer());
    entity.setDeletedFlag(getInputData().isDeleted() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    entity.setFocusMatrixRelevant(getInputData().getFocusMatrixYn() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    if (null != getInputData().getLastConfirmationDate()) {
      try {
        entity.setLastConfirmationDate(string2timestamp(getInputData().getLastConfirmationDate()));
      }
      catch (ParseException exp) {
        throw new CommandException(exp.getMessage(), exp);
      }
    }
    setUserDetails(COMMAND_MODE.CREATE, entity);
    persistEntity(entity);
    createNodeAccess(entity);
  }

  /**
   * @param entity
   * @throws IcdmException
   */
  private void createNodeAccess(final TabvUseCase entity) throws IcdmException {
    NodeAccess pidcNodeAccess = new NodeAccess();
    pidcNodeAccess.setOwner(true);
    pidcNodeAccess.setGrant(true);
    pidcNodeAccess.setWrite(true);
    pidcNodeAccess.setRead(true);
    pidcNodeAccess.setNodeId(entity.getUseCaseId());
    pidcNodeAccess.setNodeType(ApicConstants.UC_NODE_TYPE);
    pidcNodeAccess.setUserId(this.ucData.getOwnerId());
    pidcNodeAccess.setVersion(1L);
    NodeAccessCommand nodeAccessCmd = new NodeAccessCommand(getServiceData(), pidcNodeAccess);
    executeChildCommand(nodeAccessCmd);
    this.nodeAccess = nodeAccessCmd.getNewData();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    UseCaseLoader loader = new UseCaseLoader(getServiceData());
    TabvUseCase entity = loader.getEntityObject(getInputData().getId());

    if (this.changeUpToDateStatus) {
      // up to date status change
      if (this.isUpToDate) {
        entity.setLastConfirmationDate(getCurrentTime());
      }
      else {
        entity.setLastConfirmationDate(null);
      }
    }
    else {
      // other updates
      entity.setNameEng(getInputData().getNameEng());
      entity.setNameGer(getInputData().getNameGer());
      entity.setDescEng(getInputData().getDescEng());
      entity.setDescGer(getInputData().getDescGer());
      entity.setDeletedFlag(getInputData().isDeleted() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
      entity.setFocusMatrixRelevant(getInputData().getFocusMatrixYn() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    }

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

  /**
   * @return the nodeAccess
   */
  public NodeAccess getNodeAccess() {
    return this.nodeAccess;
  }
}
