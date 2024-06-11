/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwFunction;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwWpResp;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.RvwUserCmntHistory;


/**
 * Command class for CDR Result Parameter
 *
 * @author BRU2COB
 */
public class CDRResultParameterCommand extends AbstractCommand<CDRResultParameter, CDRResultParameterLoader> {

  private RvwUserCmntHistory createdRvwCmntHistory;

  private RvwUserCmntHistory deletedRvwCmntHistory;


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public CDRResultParameterCommand(final ServiceData serviceData, final CDRResultParameter input,
      final boolean isUpdate, final boolean isDelete) throws IcdmException {
    super(serviceData, input, new CDRResultParameterLoader(serviceData), resolveCommandModeA(isDelete, isUpdate));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TRvwParameter entity = new TRvwParameter();

    entity.setTParameter(new ParameterLoader(getServiceData()).getEntityObject(getInputData().getParamId()));
    TRvwResult resultEntity = new CDRReviewResultLoader(getServiceData()).getEntityObject(getInputData().getResultId());
    entity.setTRvwResult(resultEntity);
    TRvwFunction rvwFuncEntity =
        new CDRResultFunctionLoader(getServiceData()).getEntityObject(getInputData().getRvwFunId());
    entity.setTRvwFunction(rvwFuncEntity);
    entity.setRvwMethod(getInputData().getRvwMethod());
    entity.setLowerLimit(getInputData().getLowerLimit());
    entity.setUpperLimit(getInputData().getUpperLimit());
    entity.setMaturityLvl(getInputData().getMaturityLvl());
    entity.setRefValue(getInputData().getRefValue());
    entity.setRefUnit(getInputData().getRefUnit());
    entity.setHint(getInputData().getHint());
    entity.setCheckedValue(getInputData().getCheckedValue());
    entity.setResult(getInputData().getResult());
    entity.setRvwComment(getInputData().getRvwComment());
    entity.setCheckUnit(getInputData().getCheckUnit());
    entity.setChangeFlag(getInputData().getChangeFlag().intValue());
    entity.setMatchRefFlag(getInputData().getMatchRefFlag());
    // Added to map wp and resp to a parameter
    TRvwWpResp tRvwWpResp = new RvwWpRespLoader(getServiceData()).getEntityObject(getInputData().getRvwWpRespId());
    entity.settRvwWpResp(tRvwWpResp);
    // TO-DO
    entity.setTRvwFile(new RvwFileLoader(getServiceData()).getEntityObject(getInputData().getRvwFileId()));
    entity.setBitwiseLimit(getInputData().getBitwiseLimit());
    entity.setIsbitwise(getInputData().getIsbitwise());
    entity.setTRvwParameter(
        new CDRResultParameterLoader(getServiceData()).getEntityObject(getInputData().getParentParamId()));
    entity.setReviewScore(getInputData().getReviewScore());
    entity.setCompliResult(getInputData().getCompliResult());
    entity.setQssdResult(getInputData().getQssdResult());
    entity.setLabObjId(getInputData().getLabObjId() == null ? null : getInputData().getLabObjId().longValue());
    entity.setRevId(getInputData().getRevId() == null ? null : getInputData().getRevId().longValue());
    entity.setQssdLabObjId(
        getInputData().getQssdLabObjId() == null ? null : getInputData().getQssdLabObjId().longValue());
    entity.setQssdRevId(getInputData().getQssdRevId() == null ? null : getInputData().getQssdRevId().longValue());
    entity.setCompliLabObjId(
        getInputData().getCompliLabObjId() == null ? null : getInputData().getCompliLabObjId().longValue());
    entity.setCompliRevId(getInputData().getCompliRevId() == null ? null : getInputData().getCompliRevId().longValue());
    entity.setSecondaryResult(getInputData().getSecondaryResult());
    entity.setSecondaryResultState(getInputData().getSecondaryResultState());
    entity.setSrResult(getInputData().getSrResult());
    entity.setSrErrorDetails(getInputData().getSrErrorDetails());
    entity.setReadOnlyParamFlag(booleanToYorN(getInputData().isReadOnlyParam()));
    entity.setArcReleasedFlag(booleanToYorN(getInputData().getArcReleasedFlag()));
    setUserDetails(COMMAND_MODE.CREATE, entity);

    Set<TRvwParameter> paramSet = resultEntity.getTRvwParameters();
    if (paramSet == null) {
      paramSet = new HashSet<>();
    }
    paramSet.add(entity);
    resultEntity.setTRvwParameters(paramSet);
    Set<TRvwParameter> functionParamSet = rvwFuncEntity.getTRvwParameters();
    if (functionParamSet == null) {
      functionParamSet = new HashSet<>();
    }
    functionParamSet.add(entity);
    rvwFuncEntity.setTRvwParameters(functionParamSet);
    // To Associate parameters to rvw wp resp
    tRvwWpResp.addTRvwParameter(entity);

    persistEntity(entity);

    if (isRvwCmntChanged()) {
      setRvwCmntHistory();
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    CDRResultParameterLoader loader = new CDRResultParameterLoader(getServiceData());
    TRvwParameter entity = loader.getEntityObject(getInputData().getId());


    entity.setResult(getInputData().getResult());
    entity.setRvwComment(getInputData().getRvwComment());

    entity.setChangeFlag(getInputData().getChangeFlag().intValue());
    entity.setMatchRefFlag(getInputData().getMatchRefFlag());

    entity.setReviewScore(getInputData().getReviewScore());
    entity.settRvwWpResp(new RvwWpRespLoader(getServiceData()).getEntityObject(getInputData().getRvwWpRespId()));
    entity.setSecondaryResult(getInputData().getSecondaryResult());
    entity.setSecondaryResultState(getInputData().getSecondaryResultState());
    entity.setSrResult(getInputData().getSrResult());
    entity.setSrErrorDetails(getInputData().getSrErrorDetails());
    entity.setSrAcceptedFlag(getInputData().getSrAcceptedFlag());
    entity.setSrAcceptedUser(getInputData().getSrAcceptedUser());
    entity.setSrAcceptedDate(getCurrentTime());
    entity.setArcReleasedFlag(booleanToYorN(getInputData().getArcReleasedFlag()));
    
    setUserDetails(COMMAND_MODE.UPDATE, entity);

    if (isRvwCmntChanged()) {
      setRvwCmntHistory();
    }
  }

  private boolean isRvwCmntChanged() {
    return (getOldData() != null) && CommonUtils.isNotEmptyString(getInputData().getRvwComment()) &&
        isObjectChanged(getInputData().getRvwComment(), getOldData().getRvwComment());
  }

  private void setRvwCmntHistory() throws IcdmException {

    Map<Long, RvwUserCmntHistory> rvwCmntHistoryMap =
        new RvwUserCmntHistoryLoader(getServiceData()).getRvwCmntHistoryForUser(getServiceData().getUserId());

    List<RvwUserCmntHistory> rvwCmtHistList = new ArrayList<>(rvwCmntHistoryMap.values());

    Collections.sort(rvwCmtHistList);

    // check if comment already exists
    if (!doesCmntExist(rvwCmtHistList)) {

      if (rvwCmtHistList.size() == 10) {
        // if number of comments of user in DB is 10, delete oldest comment history
        RvwUserCmntHistory rvwCmntHistory = rvwCmtHistList.get(0);
        RvwUserCmntHistoryCommand deleteCmd =
            new RvwUserCmntHistoryCommand(getServiceData(), rvwCmntHistory, false, true);
        executeChildCommand(deleteCmd);
        this.deletedRvwCmntHistory = rvwCmntHistory;
      }

      RvwUserCmntHistory newRvwCmntHistory = new RvwUserCmntHistory();
      newRvwCmntHistory.setRvwCmntUserId(getServiceData().getUserId());
      newRvwCmntHistory.setRvwComment(getInputData().getRvwComment());
      RvwUserCmntHistoryCommand createCmd =
          new RvwUserCmntHistoryCommand(getServiceData(), newRvwCmntHistory, false, false);
      executeChildCommand(createCmd);
      this.createdRvwCmntHistory = createCmd.getNewData();
    }
  }

  private boolean doesCmntExist(final List<RvwUserCmntHistory> rvwCmtHistList) {
    for (RvwUserCmntHistory rvwCmntHistory : rvwCmtHistList) {
      if (CommonUtils.isEqual(rvwCmntHistory.getRvwComment().trim(), getInputData().getRvwComment().trim())) {
        return true;
      }
    }
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    CDRResultParameterLoader loader = new CDRResultParameterLoader(getServiceData());
    TRvwParameter entity = loader.getEntityObject(getInputData().getId());
    entity.getTRvwResult().getTRvwParameters().remove(entity);
    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // implementation not needed
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
    // implementation not required
  }


  /**
   * @return the createdRvwCmntHistory
   */
  public RvwUserCmntHistory getCreatedRvwCmntHistory() {
    return this.createdRvwCmntHistory;
  }


  /**
   * @return the deletedRvwCmntHistory
   */
  public RvwUserCmntHistory getDeletedRvwCmntHistory() {
    return this.deletedRvwCmntHistory;
  }


}
