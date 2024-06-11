package com.bosch.caltool.icdm.bo.cdr;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TDaDataAssessment;
import com.bosch.caltool.icdm.database.entity.cdr.TDaWpResp;
import com.bosch.caltool.icdm.model.cdr.DaWpResp;


/**
 * Command class for DaWpResp
 *
 * @author say8cob
 */
public class DaWpRespCommand extends AbstractCommand<DaWpResp, DaWpRespLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public DaWpRespCommand(final ServiceData serviceData, final DaWpResp input) throws IcdmException {
    super(serviceData, input, new DaWpRespLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TDaWpResp entity = new TDaWpResp();
    TDaDataAssessment tDaDataAssessment =
        new DaDataAssessmentLoader(getServiceData()).getEntityObject(getInputData().getDataAssessmentId());
    entity.setTDaDataAssessment(tDaDataAssessment);
    entity.setA2lWpId(getInputData().getA2lWpId());
    entity.setA2lWpName(getInputData().getA2lWpName());
    entity.setA2lRespId(getInputData().getA2lRespId());
    entity.setA2lRespAliasName(getInputData().getA2lRespAliasName());
    entity.setA2lRespName(getInputData().getA2lRespName());
    entity.setA2lRespType(getInputData().getA2lRespType());
    entity.setWpReadyForProductionFlag(getInputData().getWpReadyForProductionFlag());
    entity.setWpFinishedFlag(getInputData().getWpFinishedFlag());
    entity.setQnairesAnsweredFlag(getInputData().getQnairesAnsweredFlag());
    entity.setParameterReviewedFlag(getInputData().getParameterReviewedFlag());
    entity.setHexRvwEqualFlag(getInputData().getHexRvwEqualFlag());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);

    List<TDaWpResp> tDaWpResps = tDaDataAssessment.getTDaWpResps();
    if (tDaWpResps == null) {
      tDaWpResps = new ArrayList<>();
      tDaDataAssessment.setTDaWpResps(tDaWpResps);
    }
    tDaWpResps.add(entity);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // Not Applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // Not Applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // Not Applicable
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
    // Not Applicable
  }

}
