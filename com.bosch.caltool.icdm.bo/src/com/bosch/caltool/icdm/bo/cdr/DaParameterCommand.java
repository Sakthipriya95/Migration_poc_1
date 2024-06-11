package com.bosch.caltool.icdm.bo.cdr;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TDaParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TDaWpResp;
import com.bosch.caltool.icdm.model.cdr.DaParameter;


/**
 * Command class for DaParameter
 *
 * @author say8cob
 */
public class DaParameterCommand extends AbstractCommand<DaParameter, DaParameterLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public DaParameterCommand(final ServiceData serviceData, final DaParameter input) throws IcdmException {
    super(serviceData, input, new DaParameterLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TDaParameter entity = new TDaParameter();

    TDaWpResp tDaWpResp = new DaWpRespLoader(getServiceData()).getEntityObject(getInputData().getDaWpRespId());
    entity.setTDaWpResp(tDaWpResp);
    entity.setParameterId(getInputData().getParameterId());
    entity.setParameterName(getInputData().getParameterName());
    entity.setParameterType(getInputData().getParameterType());
    entity.setFunctionName(getInputData().getFunctionName());
    entity.setFunctionVersion(getInputData().getFunctionVersion());
    entity.setRvwA2lVersion(getInputData().getRvwA2lVersion());
    entity.setRvwFuncVersion(getInputData().getRvwFuncVersion());
    entity.setQuestionnaireStatus(getInputData().getQuestionnaireStatus());
    entity.setReviewedFlag(getInputData().getReviewedFlag());
    entity.setEqualsFlag(getInputData().getEqualsFlag());
    entity.setCompliResult(getInputData().getCompliResult());
    entity.setReviewScore(getInputData().getReviewScore());
    entity.setReviewRemark(getInputData().getReviewRemark());
    entity.setResultId(getInputData().getResultId());
    entity.setRvwParamId(getInputData().getRvwParamId());
    entity.setRvwResultName(getInputData().getRvwResultName());
    entity.setHexValue(getInputData().getHexValue());
    entity.setReviewedValue(getInputData().getReviewedValue());
    entity.setCompliFlag(getInputData().getCompliFlag());
    entity.setQssdFlag(getInputData().getQssdFlag());
    entity.setReadOnlyFlag(getInputData().getReadOnlyFlag());
    entity.setDependentCharacteristicFlag(getInputData().getDependentCharacteristicFlag());
    entity.setBlackListFlag(getInputData().getBlackListFlag());
    entity.setNeverReviewedFlag(getInputData().getNeverReviewedFlag());
    entity.setQssdResult(getInputData().getQssdResult());
    entity.setCompliTooltip(getInputData().getCompliTooltip());
    entity.setQssdTooltip(getInputData().getQssdTooltip());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);


    List<TDaParameter> daParameters = tDaWpResp.getTDaParameters();
    if (daParameters == null) {
      daParameters = new ArrayList<>();
      tDaWpResp.setTDaParameters(daParameters);
    }
    daParameters.add(entity);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // Not Yet implemented
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // Not Yet implemented
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // Not Yet implemented
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
    // Not Yet implemented
  }

}
