package com.bosch.caltool.icdm.bo.cdr;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResultsSecondary;
import com.bosch.caltool.icdm.model.cdr.RvwResultsSecondary;


/**
 * Command class for TRvwResultsSecondary
 *
 * @author say8cob
 */
public class RvwResultsSecondaryCommand extends AbstractCommand<RvwResultsSecondary, RvwResultsSecondaryLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public RvwResultsSecondaryCommand(final ServiceData serviceData, final RvwResultsSecondary input,
      final boolean isUpdate, final boolean isDelete) throws IcdmException {
    super(serviceData, input, new RvwResultsSecondaryLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TRvwResultsSecondary entity = new TRvwResultsSecondary();
    CDRReviewResultLoader loader = new CDRReviewResultLoader(getServiceData());
    RuleSetLoader ruleSetLoader = new RuleSetLoader(getServiceData());

    TRvwResult resultEntity = loader.getEntityObject(getInputData().getResultId());
    entity.setTRvwResult(resultEntity);
    Set<TRvwResultsSecondary> tRvwResultsSec = resultEntity.getTRvwResultsSecondaries();
    if (null == tRvwResultsSec) {
      tRvwResultsSec = new HashSet<TRvwResultsSecondary>();
    }
    tRvwResultsSec.add(entity);
    resultEntity.setTRvwResultsSecondaries(tRvwResultsSec);
    if (getInputData().getRsetId() != null) {
      entity.setTRuleSet(ruleSetLoader.getEntityObject(getInputData().getRsetId()));
    }
    if (null != getInputData().getSsdReleaseId()) {
      entity.setSsdReleaseID(getInputData().getSsdReleaseId());
    }
    entity.setSource(getInputData().getSource());
    entity.setSsdVersID(getInputData().getSsdVersionId());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    CDRReviewResultLoader rvwLoader = new CDRReviewResultLoader(getServiceData());
    RuleSetLoader ruleSetLoader = new RuleSetLoader(getServiceData());
    RvwResultsSecondaryLoader loader = new RvwResultsSecondaryLoader(getServiceData());
    TRvwResultsSecondary entity = loader.getEntityObject(getInputData().getId());

    entity.setTRvwResult(rvwLoader.getEntityObject(getInputData().getResultId()));
    if (getInputData().getRsetId() != null) {
      entity.setTRuleSet(ruleSetLoader.getEntityObject(getInputData().getRsetId()));
    }
    entity.setSsdReleaseID(getInputData().getSsdReleaseId());
    entity.setSource(getInputData().getSource());
    entity.setSsdVersID(getInputData().getSsdVersionId());

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    RvwResultsSecondaryLoader loader = new RvwResultsSecondaryLoader(getServiceData());
    TRvwResultsSecondary entity = loader.getEntityObject(getInputData().getId());

    entity.getTRvwResult().getTRvwResultsSecondaries().remove(entity);
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
