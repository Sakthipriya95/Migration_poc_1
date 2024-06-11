package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleRemark;
import com.bosch.caltool.icdm.model.cdr.RuleRemark;


/**
 * Command class for RuleRemarks
 *
 * @author dja7cob
 */
public class RuleRemarkCommand extends AbstractCommand<RuleRemark, RuleRemarkLoader> {


  /**
   * Constructor
   *
   * @param input       input data
   * @param isUpdate    if true, update, else create
   * @param isDelete    if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public RuleRemarkCommand(final ServiceData serviceData, final RuleRemark input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {

    super(serviceData, input, new RuleRemarkLoader(serviceData), resolveCommandModeA(isDelete, isUpdate));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TRuleRemark entity = new TRuleRemark();

    entity.setRuleId(getInputData().getRuleId());
    entity.setRevId(getInputData().getRevId());
    entity.setRemark(getInputData().getRemark());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    RuleRemarkLoader loader = new RuleRemarkLoader(getServiceData());
    TRuleRemark entity = loader.getEntityObject(getInputData().getId());

    entity.setRuleId(getInputData().getRuleId());
    entity.setRevId(getInputData().getRevId());
    entity.setRemark(getInputData().getRemark());

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    RuleRemarkLoader loader = new RuleRemarkLoader(getServiceData());
    TRuleRemark entity = loader.getEntityObject(getInputData().getId());

    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // Not applicable
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
    // Not applicable
  }

}
