/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.cdr.CDRResultParameterCommand;
import com.bosch.caltool.icdm.bo.cdr.CDRResultParameterLoader;
import com.bosch.caltool.icdm.bo.cdr.RvwResultWpRespUniqueRespMerge;
import com.bosch.caltool.icdm.bo.cdr.RvwWpRespCommand;
import com.bosch.caltool.icdm.bo.cdr.RvwWpRespLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwWpResp;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityMergeModel;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.RvwWpResp;

/**
 * @author dmr1cob
 */
public class A2lRespMergeRvwResultCommand extends AbstractSimpleCommand {

  private final A2lResponsibilityMergeModel mergeModel;

  private final Set<RvwWpResp> rvwWpRespOld = new HashSet<>();

  private final Map<Long, RvwWpResp> rvwWpRespUpdate = new HashMap<>();

  private final Set<RvwWpResp> rvwWpRespDelete = new HashSet<>();

  private final Map<Long, CDRResultParameter> cdrResultParameterUpdate = new HashMap<>();

  private final Set<CDRResultParameter> cdrResultParameterOld = new HashSet<>();

  /**
   * @param serviceData Service data
   * @param mergeModel Merge Model
   * @throws IcdmException Exception in webservice
   */
  public A2lRespMergeRvwResultCommand(final ServiceData serviceData, final A2lResponsibilityMergeModel mergeModel)
      throws IcdmException {
    super(serviceData);
    this.mergeModel = mergeModel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    // Get destination a2l resp RvwResultWpResp map Key - RvwResultWpRespUniqueRespMerge Value - RvwResultwpresp id
    Map<RvwResultWpRespUniqueRespMerge, Long> rvwResultWpRespUniqueMap = new HashMap<>();
    getDestRvwResultWpRespList(rvwResultWpRespUniqueMap);

    for (Long a2lRespMergeFromId : this.mergeModel.getA2lRespMergeFromIdSet()) {
      mergeRvwResultWpResp(a2lRespMergeFromId, rvwResultWpRespUniqueMap);
    }

    this.mergeModel.setRvwWpRespOld(this.rvwWpRespOld);
    this.mergeModel.setRvwWpRespUpdate(this.rvwWpRespUpdate);
    this.mergeModel.setRvwWpRespDelete(this.rvwWpRespDelete);
    this.mergeModel.setCdrResultParameterOld(this.cdrResultParameterOld);
    this.mergeModel.setCdrResultParameterUpdate(this.cdrResultParameterUpdate);

    getLogger().info("Update count : Review wp resp - {},, CDR result parameter - {}", this.rvwWpRespUpdate.size(),
        this.cdrResultParameterUpdate.size());
    getLogger().info("Delete count : Review wp resp - {}", this.rvwWpRespDelete.size());
  }

  /**
   * In Rvw wp resp table the following fields resultid, wpid, respid are unique fields. In order to prevent unique
   * constrain, check whether the data is already available. The data will be stored in a class
   * {@link RvwResultWpRespUniqueRespMerge} where the equality will be checked using the unique fields(resultid, wpid,
   * respid). In this method fill the set for source a2l
   *
   * @param rvwResultWpRespUniqueMap Destination unique rvw wp resp map
   * @param a2lRespMergeFromId source resp id
   */
  private void mergeRvwResultWpResp(final Long a2lRespMergeFromId,
      final Map<RvwResultWpRespUniqueRespMerge, Long> rvwResultWpRespUniqueMap)
      throws IcdmException {
    A2lResponsibilityLoader a2lResponsibilityLoader = new A2lResponsibilityLoader(getServiceData());
    Set<TRvwWpResp> tRvwWpRespMergeFromSet =
        a2lResponsibilityLoader.getEntityObject(a2lRespMergeFromId).gettRvwWpResps();

    if (CommonUtils.isNotEmpty(tRvwWpRespMergeFromSet)) {
      Set<RvwResultWpRespUniqueRespMerge> rvwResultWpRespUniqueMergeFromSet = new HashSet<>();

      for (TRvwWpResp tRvwWpResp : tRvwWpRespMergeFromSet) {
        RvwResultWpRespUniqueRespMerge rvwResultWpRespUniqueRespMerge =
            new RvwResultWpRespUniqueRespMerge(tRvwWpResp.getRvwWpRespId(), tRvwWpResp.getTRvwResult().getResultId(),
                tRvwWpResp.getTA2lWorkPackage().getA2lWpId(), tRvwWpResp.gettA2lResponsibility().getA2lRespId());
        rvwResultWpRespUniqueMergeFromSet.add(rvwResultWpRespUniqueRespMerge);
      }

      getLogger().info("Unique wp resp combination for source a2l responsibility - {}",
          rvwResultWpRespUniqueMergeFromSet.size());

      updateRvwResultWpResp(rvwResultWpRespUniqueMap, rvwResultWpRespUniqueMergeFromSet);
    }
  }


  /**
   * Merge a2l responsibility in parent and child tables
   *
   * @param rvwResultWpRespUniqueMap rvwResultWpRespUniqueMap
   * @param rvwResultWpRespUniqueMergeFromSet rvwResultWpRespUniqueMergeFromSet
   * @throws IcdmException exception in ws call
   */
  private void updateRvwResultWpResp(final Map<RvwResultWpRespUniqueRespMerge, Long> rvwResultWpRespUniqueMap,
      final Set<RvwResultWpRespUniqueRespMerge> rvwResultWpRespUniqueMergeFromSet)
      throws IcdmException {
    RvwWpRespLoader rvwWpRespLoader = new RvwWpRespLoader(getServiceData());
    RvwWpRespCommand rvwWpRespCommand;

    for (RvwResultWpRespUniqueRespMerge rvwResultWpRespUnique : rvwResultWpRespUniqueMergeFromSet) {
      rvwResultWpRespUnique.setRespId(this.mergeModel.getA2lRespMergeToId());
      RvwWpResp rvwWpResp = rvwWpRespLoader.getDataObjectByID(rvwResultWpRespUnique.getRvwResultWpRespId());

      if (!rvwResultWpRespUniqueMap.containsKey(rvwResultWpRespUnique)) {
        rvwResultWpRespUniqueMap.put(rvwResultWpRespUnique, rvwResultWpRespUnique.getRvwResultWpRespId());
        updateRvwWpResp(rvwWpResp);
      }
      else {
        Set<TRvwParameter> tRvwParameterSet =
            rvwWpRespLoader.getEntityObject(rvwResultWpRespUnique.getRvwResultWpRespId()).gettRvwParameter();

        if (CommonUtils.isNotEmpty(tRvwParameterSet)) {
          updateCdrResultParam(rvwResultWpRespUniqueMap.get(rvwResultWpRespUnique), tRvwParameterSet);
        }

        rvwWpRespCommand = new RvwWpRespCommand(getServiceData(), rvwWpResp, false, true);
        executeChildCommand(rvwWpRespCommand);

        this.rvwWpRespDelete.add(rvwWpResp);
      }
    }
  }

  /**
   * Update to rvwwprespid which used destination a2l respid
   *
   * @param rvwWpRespId rvwwprespid which needs to be update for src rvwwprespid
   * @param tRvwParameterSet tRvwParameterSet
   * @throws IcdmException Exception in ws call
   */
  private void updateCdrResultParam(final Long rvwWpRespId, final Set<TRvwParameter> tRvwParameterSet)
      throws IcdmException {
    CDRResultParameterCommand cdrResultParameterCommand;
    CDRResultParameterLoader cdrResultParameterLoader = new CDRResultParameterLoader(getServiceData());

    for (TRvwParameter tRvwParameter : tRvwParameterSet) {
      CDRResultParameter cdrResultParam = cdrResultParameterLoader.getDataObjectByID(tRvwParameter.getRvwParamId());
      this.cdrResultParameterOld.add(cdrResultParam.clone());
      cdrResultParam.setRvwWpRespId(rvwWpRespId);
      cdrResultParameterCommand = new CDRResultParameterCommand(getServiceData(), cdrResultParam, true, false);
      executeChildCommand(cdrResultParameterCommand);
    }

  }

  /**
   * Update to destination a2l resp id
   *
   * @param rvwWpResp {@link RvwWpResp}
   * @throws IcdmException Exception in ws call
   */
  private void updateRvwWpResp(final RvwWpResp rvwWpResp) throws IcdmException {
    this.rvwWpRespOld.add(rvwWpResp.clone());
    rvwWpResp.setA2lRespId(this.mergeModel.getA2lRespMergeToId());
    RvwWpRespCommand rvwWpRespCommand = new RvwWpRespCommand(getServiceData(), rvwWpResp, true, false);
    executeChildCommand(rvwWpRespCommand);
  }

  /**
   * In Rvw wp resp table the following fields resultid, wpid, respid are unique fields. In order to prevent unique
   * constrain, check whether the data is already available. The data will be stored in a class
   * {@link RvwResultWpRespUniqueRespMerge} where the equality will be checked using the unique fields(resultid, wpid,
   * respid). In this method fill the map where Key is {@link RvwResultWpRespUniqueRespMerge} and value is rvwwpresp for
   * destination a2l resp
   *
   * @param rvwResultWpRespUniqueMap Key is {@link RvwResultWpRespUniqueRespMerge} and value is rvwwpresp for
   *          destination a2l resp
   */
  private void getDestRvwResultWpRespList(final Map<RvwResultWpRespUniqueRespMerge, Long> rvwResultWpRespUniqueMap) {
    A2lResponsibilityLoader a2lResponsibilityLoader = new A2lResponsibilityLoader(getServiceData());
    Set<TRvwWpResp> tRvwWpRespSet =
        a2lResponsibilityLoader.getEntityObject(this.mergeModel.getA2lRespMergeToId()).gettRvwWpResps();

    if (CommonUtils.isNotEmpty(tRvwWpRespSet)) {
      for (TRvwWpResp tRvwWpResp : tRvwWpRespSet) {
        RvwResultWpRespUniqueRespMerge rvwResultWpRespUnique =
            new RvwResultWpRespUniqueRespMerge(tRvwWpResp.getRvwWpRespId(), tRvwWpResp.getTRvwResult().getResultId(),
                tRvwWpResp.getTA2lWorkPackage().getA2lWpId(), tRvwWpResp.gettA2lResponsibility().getA2lRespId());
        rvwResultWpRespUniqueMap.put(rvwResultWpRespUnique, tRvwWpResp.getRvwWpRespId());
      }
    }
    getLogger().info("Unique wp resp combination for destination a2l responsibility count - {}",
        rvwResultWpRespUniqueMap.size());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // No implementation

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }
}
