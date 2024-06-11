package com.bosch.caltool.icdm.bo.cdr;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TDaQnaireResp;
import com.bosch.caltool.icdm.database.entity.cdr.TDaWpResp;
import com.bosch.caltool.icdm.model.cdr.DaQnaireResp;


/**
 * Command class for DaQnaireResp
 *
 * @author say8cob
 */
public class DaQnaireRespCommand extends AbstractCommand<DaQnaireResp, DaQnaireRespLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public DaQnaireRespCommand(final ServiceData serviceData, final DaQnaireResp input) throws IcdmException {
    super(serviceData, input, new DaQnaireRespLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TDaQnaireResp entity = new TDaQnaireResp();
    TDaWpResp tDaWpResp = new DaWpRespLoader(getServiceData()).getEntityObject(getInputData().getDaWpRespId());
    entity.setTDaWpResp(tDaWpResp);
    entity.setQnaireRespId(getInputData().getQnaireRespId());
    entity.setQnaireRespName(getInputData().getQnaireRespName());
    entity.setQnaireRespVersId(getInputData().getQnaireRespVersId());
    entity.setQnaireRespVersionName(getInputData().getQnaireRespVersionName());
    entity.setReadyForProductionFlag(getInputData().getReadyForProductionFlag());
    entity.setBaselineExistingFlag(getInputData().getBaselineExistingFlag());
    entity.setNumPositiveAnswers(getInputData().getNumPositiveAnswers());
    entity.setNumNeutralAnswers(getInputData().getNumNeutralAnswers());
    entity.setNumNegativeAnswers(getInputData().getNumNegativeAnswers());
    try {
      entity.setReviewedDate(string2timestamp(getInputData().getReviewedDate()));
    }
    catch (ParseException e) {
      throw new IcdmException("Error while parsing reviewed date string", e);
    }
    entity.setReviewedUser(getInputData().getReviewedUser());


    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);


    List<TDaQnaireResp> daQnaireResps = tDaWpResp.getTDaQnaireResps();
    if (daQnaireResps == null) {
      daQnaireResps = new ArrayList<>();
      tDaWpResp.setTDaQnaireResps(daQnaireResps);
    }
    daQnaireResps.add(entity);


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
