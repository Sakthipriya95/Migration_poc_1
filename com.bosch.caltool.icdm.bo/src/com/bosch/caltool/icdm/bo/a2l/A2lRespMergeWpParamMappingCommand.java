/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpParamMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityMergeModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.WP_RESP_STATUS_TYPE;

/**
 * @author dmr1cob
 */
public class A2lRespMergeWpParamMappingCommand extends AbstractSimpleCommand {

  private final A2lResponsibilityMergeModel mergeModel;

  private final Map<Long, A2lWpParamMapping> a2lWpParamMappingUpdate = new HashMap<>();

  private final Map<Long, A2lWpResponsibility> a2lWpResponsibilityUpdate = new HashMap<>();

  private final Set<A2lWpParamMapping> a2lWpParamMappingOld = new HashSet<>();

  private final Set<A2lWpResponsibility> a2lWpResponsibilityOld = new HashSet<>();

  private final List<TA2lWpResponsibilityStatus> mergedA2lWpRespStatusList = new ArrayList<>();

  /**
   * @param serviceData Service data
   * @param mergeModel Merge Model
   * @throws IcdmException Exception in webservice
   */
  public A2lRespMergeWpParamMappingCommand(final ServiceData serviceData, final A2lResponsibilityMergeModel mergeModel)
      throws IcdmException {
    super(serviceData);
    this.mergeModel = mergeModel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    A2lResponsibilityLoader a2lResponsibilityLoader = new A2lResponsibilityLoader(getServiceData());
    for (Long a2lRespMergeFromId : this.mergeModel.getA2lRespMergeFromIdSet()) {
      TA2lResponsibility tA2lResponsibility = a2lResponsibilityLoader.getEntityObject(a2lRespMergeFromId);

      mergeA2lWpResponsibility(tA2lResponsibility);

      mergeA2lWpParamMapping(tA2lResponsibility);

      this.mergedA2lWpRespStatusList.addAll(tA2lResponsibility.gettA2lWPRespStatus());
    }

    // Reset Status of to be merged Status to 'Not Finished'
    updateToBeMergedA2lWPRespStatus();

    // Delete the entry of Merge From Responsibility Status
    delMergeFromA2lWpRespStatus();

    this.mergeModel.setA2lWpParamMappingUpdate(this.a2lWpParamMappingUpdate);
    this.mergeModel.setA2lWpResponsibilityUpdate(this.a2lWpResponsibilityUpdate);
    this.mergeModel.setA2lWpParamMappingOld(this.a2lWpParamMappingOld);
    this.mergeModel.setA2lWpResponsibilityOld(this.a2lWpResponsibilityOld);

  }

  /**
   * @throws IcdmException
   */
  private void delMergeFromA2lWpRespStatus() throws IcdmException {
    A2lWpResponsibilityStatusLoader a2lWpResponsibilityStatusLoader =
        new A2lWpResponsibilityStatusLoader(getServiceData());
    A2lWpResponsibilityStatusCommand a2lWpResponsibilityStatusCommand;

    for (TA2lWpResponsibilityStatus tA2lWpRespStatus : this.mergedA2lWpRespStatusList) {
      A2lWpResponsibilityStatus a2lWpResponsibilityStatus =
          a2lWpResponsibilityStatusLoader.getDataObjectByID(tA2lWpRespStatus.getA2lWpRespStatusId());
      a2lWpResponsibilityStatusCommand =
          new A2lWpResponsibilityStatusCommand(getServiceData(), a2lWpResponsibilityStatus, false, true);
      executeChildCommand(a2lWpResponsibilityStatusCommand);
    }
  }

  /**
   * @throws IcdmException
   */
  private void updateToBeMergedA2lWPRespStatus() throws IcdmException {
    TA2lResponsibility toBeMergedTA2lResp =
        new A2lResponsibilityLoader(getServiceData()).getEntityObject(this.mergeModel.getA2lRespMergeToId());
    List<TA2lWpResponsibility> toBeMergedA2LWPRespList = toBeMergedTA2lResp.getWpRespPalList();
    A2lWpResponsibilityStatusLoader a2lWpResponsibilityStatusLoader =
        new A2lWpResponsibilityStatusLoader(getServiceData());
    A2lWpResponsibilityStatusCommand a2lWpResponsibilityStatusCommand;

    for (TA2lWpResponsibility tA2lWpResp : toBeMergedA2LWPRespList) {
      for (TA2lWpResponsibilityStatus tA2lWpRespStatus : tA2lWpResp.gettA2lWPRespStatus()) {

        A2lWpResponsibilityStatus a2lWpResponsibilityStatus =
            a2lWpResponsibilityStatusLoader.getDataObjectByID(tA2lWpRespStatus.getA2lWpRespStatusId());
        a2lWpResponsibilityStatus.setWpRespFinStatus(WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType());
        a2lWpResponsibilityStatusCommand =
            new A2lWpResponsibilityStatusCommand(getServiceData(), a2lWpResponsibilityStatus, true, false);
        executeChildCommand(a2lWpResponsibilityStatusCommand);
      }
    }
  }

  /**
   * @param tA2lResponsibility
   * @throws DataException
   * @throws IcdmException
   */
  private void mergeA2lWpResponsibility(final TA2lResponsibility tA2lResponsibility) throws IcdmException {
    A2lWpResponsibilityLoader a2lWpResponsibilityLoader = new A2lWpResponsibilityLoader(getServiceData());
    A2lWpResponsibilityCommand a2lWpResponsibilityCommand;
    List<TA2lWpResponsibility> tA2lWpRespList = tA2lResponsibility.getWpRespPalList();

    if (CommonUtils.isNotEmpty(tA2lWpRespList)) {
      for (TA2lWpResponsibility ta2lWpResponsibility : tA2lWpRespList) {
        A2lWpResponsibility a2lWpResponsibility =
            a2lWpResponsibilityLoader.getDataObjectByID(ta2lWpResponsibility.getWpRespId());
        this.a2lWpResponsibilityOld.add(a2lWpResponsibility.clone());
        a2lWpResponsibility.setA2lRespId(this.mergeModel.getA2lRespMergeToId());
        a2lWpResponsibilityCommand = new A2lWpResponsibilityCommand(getServiceData(), a2lWpResponsibility, true, false);
        executeChildCommand(a2lWpResponsibilityCommand);
      }
    }
  }

  /**
   * @param a2lResponsibilityLoader
   * @param a2lRespMergeFromId
   * @throws DataException
   * @throws IcdmException
   */
  private void mergeA2lWpParamMapping(final TA2lResponsibility tA2lResponsibility) throws IcdmException {
    A2lWpParamMappingLoader a2lWpParamMappingLoader = new A2lWpParamMappingLoader(getServiceData());
    A2lWpParamMappingCommand a2lWpParamMappingCommand;
    List<TA2lWpParamMapping> wpParamMappingList = tA2lResponsibility.getWpParamMappingList();

    if (CommonUtils.isNotEmpty(wpParamMappingList)) {
      for (TA2lWpParamMapping tA2lWpParamMapping : wpParamMappingList) {
        A2lWpParamMapping wpParamMapping =
            a2lWpParamMappingLoader.getDataObjectByID(tA2lWpParamMapping.getWpParamMappingId());
        this.a2lWpParamMappingOld.add(wpParamMapping.clone());
        wpParamMapping.setParA2lRespId(this.mergeModel.getA2lRespMergeToId());
        a2lWpParamMappingCommand = new A2lWpParamMappingCommand(getServiceData(), wpParamMapping, true, false);
        executeChildCommand(a2lWpParamMappingCommand);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // No Implementation

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }
}
