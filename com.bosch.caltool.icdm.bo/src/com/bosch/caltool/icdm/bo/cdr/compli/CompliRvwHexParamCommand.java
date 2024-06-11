package com.bosch.caltool.icdm.bo.cdr.compli;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TCompliRvwHexParam;
import com.bosch.caltool.icdm.model.cdr.compli.CompliRvwHexParam;


/**
 * Command class for CompliReviewParamResultHex
 *
 * @author dmr1cob
 */
public class CompliRvwHexParamCommand extends AbstractCommand<CompliRvwHexParam, CompliRvwHexParamLoader> {


  /**
   * For NA value
   */
  private static final String HIPHEN_CONSTANT = "-";


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public CompliRvwHexParamCommand(final ServiceData serviceData, final CompliRvwHexParam input) throws IcdmException {
    super(serviceData, input, new CompliRvwHexParamLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TCompliRvwHexParam entity = new TCompliRvwHexParam();

    entity.setCheckValue(getInputData().getCheckValue());
    entity.setCompliResult(null == getInputData().getCompliResult() ? HIPHEN_CONSTANT : getInputData().getCompliResult());
    entity.setQssdResult(null == getInputData().getQssdResult() ? HIPHEN_CONSTANT : getInputData().getQssdResult());
    entity.setLabObjId(getInputData().getLabObjId());
    entity.setRevId(getInputData().getRevId());

    CompliReviewResultHexLoader compliHexLoader = new CompliReviewResultHexLoader(getServiceData());
    entity.setTCompliRvwHex(compliHexLoader.getEntityObject(getInputData().getCompliRvwHexId()));
    ParameterLoader paramLoader = new ParameterLoader(getServiceData());
    entity.setTParameter(paramLoader.getEntityObject(getInputData().getParamId()));

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // NA
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
    // NA
  }

}
