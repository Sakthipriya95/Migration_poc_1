package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleLinks;
import com.bosch.caltool.icdm.model.cdr.RuleLinks;


/**
 * Command class for RuleLinks
 *
 * @author UKT1COB
 */
public class RuleLinksCommand extends AbstractCommand<RuleLinks, RuleLinksLoader> {


  /**
   * Constructor
   *
   * @param inputData input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public RuleLinksCommand(final ServiceData serviceData, final RuleLinks inputData, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, inputData, new RuleLinksLoader(serviceData), resolveCommandModeA(isDelete, isUpdate));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TRuleLinks entity = new TRuleLinks();

    entity.setRuleId(getInputData().getRuleId());
    entity.setRevId(getInputData().getRevId());
    entity.setLink(getInputData().getLink());
    entity.setDescEng(getInputData().getDescEng());
    entity.setDescGer(getInputData().getDescGer());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    final TRuleLinks entity = new RuleLinksLoader(getServiceData()).getEntityObject(getInputData().getId());

    setNewValues(entity);

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }


  /**
   * updates the db entity with new values
   *
   * @param modifiedLink
   */
  private void setNewValues(final TRuleLinks modifiedLink) {
    if (!CommonUtils.isEqual(getOldData().getLink(), getInputData().getLink())) {
      modifiedLink.setLink(getInputData().getLink());
    }
    if (!CommonUtils.isEqual(getOldData().getDescEng(), getInputData().getDescEng())) {
      modifiedLink.setDescEng(getInputData().getDescEng());
    }
    if (!CommonUtils.isEqual(getOldData().getDescGer(), getInputData().getDescGer())) {
      modifiedLink.setDescGer(getInputData().getDescGer());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    TRuleLinks entity = new RuleLinksLoader(getServiceData()).getEntityObject(getInputData().getId());
    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return isObjectChanged(getInputData().getLink(), getOldData().getLink()) ||
        isObjectChanged(getInputData().getDescEng(), getOldData().getDescEng()) ||
        isObjectChanged(getInputData().getDescGer(), getOldData().getDescGer());
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
